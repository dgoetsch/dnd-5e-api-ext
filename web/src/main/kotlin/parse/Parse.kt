package parse

import dandd.character.automation.*
import kotlin.js.Json


interface Parser<T>: RightBinding<ParseError> {
    val node: T

    fun <S> nullable(fn: () -> S): S? =
        node?.let { fn() }

    fun readInt(): Int
    fun readIntField(name: String): Int

    fun readString(): String
    fun readStringField(name: String): String

    fun <T> readObject(body: Parser<Json?>.() -> T): T
    fun <T> readObjectField(name: String, body: Parser<Json?>.() -> T): T

    fun <T> readArray(body: Parser<Any?>.() -> T): List<T>
    fun <T> readArrayField(name: String, body: Parser<Any?>.() -> T): List<T>
}


fun <I, R> I.parse(comprehension: Parser<I>.() -> R): Either<ParseError, R> =
        Comprehension.comprehend {
            ParserImpl(this, this@parse).comprehension()
        }

private data class ParserImpl<T>(val binding: RightBinding<ParseError>, override val node: T): RightBinding<ParseError> by binding, Parser<T> {
    override fun readInt(): Int = parseInt().bind()
    override fun readIntField(name: String): Int = field(name) { readInt() }.bind()

    override fun readString() = parseString().bind()
    override fun readStringField(name: String) = field(name) { readString() }.bind()

    override fun <T> readObject(body: Parser<Json?>.() -> T) =
        expectObject { body() }.bind()

    override fun <T> readObjectField(name: String, body: Parser<Json?>.() -> T) =
        field(name) { readObject(body) }.bind()

    override fun <T> readArray(body: Parser<Any?>.() -> T) =
         expectArray { body() }.bind()

    override fun <T> readArrayField(name: String, body: Parser<Any?>.() -> T) =
        field(name) { readArray(body) }.bind()

    private fun parseInt(): Either<ParseError, Int> = when(node) {
        is Int -> Right(node)
        else -> Left(ExpectedString(node))
    }

    private fun parseString(): Either<ParseError, String> = when(node) {
        is String -> Right(node)
        else -> Left(ExpectedString(node))
    }

    private fun <T> expectObject(parseObject: Parser<Json?>.() -> T): Either<ParseError, T> = Either
            .catching { node as Json? }
            .mapLeft { CastError(it) }
            .bindRight { it.parse(parseObject) }
            .mapLeft { ExpectedObject(node, it) }

    private fun <Field> field(name: String, parseField: Parser<Any?>.() -> Field): Either<ParseError, Field> =
        expectObject {
            val fieldValue = node?.let { it[name] }
            fieldValue.parse(parseField)
                    .mapLeft { ExpectedField(name, it) }
                    .bind()
        }

    private fun <Child> expectArray(parseChild: Parser<Any?>.() -> Child): Either<ParseError, List<Child>> {
        val parseResult =  when(node) {
            is Array<*> -> node
                    .map { it.parse(parseChild) }
                    .liftRight()
                    .mapLeft { Aggregate(it) }
            else -> node
                    .parse(parseChild)
                    .mapRight { listOf(it) }
        }

        return parseResult.mapLeft { ExpectedArray(this, it) }
    }
}


private object Comprehension: ComprehensionContext<ParseError> {
    override fun BindError<ParseError>.unboxError(): ParseError =
            when(this) {
                is Expected -> error
                is Unexpected -> UnexpectedBindError(error)
            }
}

sealed class ParseError
data class Aggregate(val parseError: List<ParseError>): ParseError()
data class ExpectedField(val fieldName: String, val cause: ParseError?): ParseError()
data class ExpectedArray(val actual: Any?, val cause: ParseError?): ParseError()
data class ExpectedString(val value: Any?): ParseError()
data class ExpectedObject(val value: Any?, val cause: ParseError): ParseError()
data class CastError(val error: Exception): ParseError()
data class JsonParse(val error: Exception): ParseError()
data class UnexpectedBindError(val error: Exception): ParseError()

