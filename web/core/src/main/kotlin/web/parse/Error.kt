package web.parse

sealed class ParseError
data class Aggregate(val parseError: List<ParseError>): ParseError()
data class ExpectedField(val fieldName: String, val cause: ParseError?): ParseError()
data class ExpectedArray(val actual: Any?, val cause: ParseError?): ParseError()
data class ExpectedString(val value: Any?): ParseError()
data class ExpectedObject(val value: Any?, val cause: ParseError): ParseError()
data class CastError(val error: Exception): ParseError()
data class JsonParse(val error: Exception): ParseError()
data class UnexpectedBindError(val error: Exception): ParseError()