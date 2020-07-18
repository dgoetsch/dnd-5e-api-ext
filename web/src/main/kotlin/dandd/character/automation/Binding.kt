package dandd.character.automation

sealed class BindError<out L>
data class Expected<L>(val error: L): BindError<L>()
data class Unexpected(val error: Exception): BindError<Nothing>()

interface ComprehensionContext<E> {
    fun BindError<E>.unboxError(): E
}

interface RightBinding<L> {
    fun <R> Either<L, R>.bind(): R
}

fun <L, R> ComprehensionContext<L>.comprehend(comprehension: RightBinding<L>.() -> R): Either<L, R> =
        RightBindingImpl<L>().apply(comprehension).mapLeft { it.unboxError() }

class RightBindingImpl<L>: RightBinding<L> {
    var error: L? = null
    object BindException: Exception()

    override fun <R> Either<L, R>.bind(): R {
        when(this) {
            is Right -> return right
            is Left -> {
                error = left
                throw BindException
            }
        }
    }

    fun <R> apply(comprehension: RightBinding<L>.() -> R): Either<BindError<L>, R> =
            Either.catching { comprehension() }
                    .mapLeft { error?.let { Expected(it) } ?: Unexpected(it) }
}