package web.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.HttpStatusCode
import web.core.Either
import web.core.Left
import web.core.Right
import web.core.bindRight
import web.parse.ParseError

class ApiCient<T>(val client: HttpClient,
                  val type: String,
                  val parse: (String) -> Either<ApiClientError, T>) {
    suspend fun getResource(name: String): Either<ApiClientError, T> {
        val response = client
                .get<HttpResponse>("http://localhost:8099/api/$type/$name") {}
        val readBody = try {
            Right(response.readText())
        } catch (e: Exception) {
            Left(RequestReadFailed(e))
        }
        return readBody.bindRight { body ->
            when (response.status.value) {
                in 200..299 -> parse(body)
                else -> Left(UnsuccessfulRequest(response.status, body))
            }
        }
    }
}

sealed class ApiClientError
data class UnsuccessfulRequest(val statusCode: HttpStatusCode, val body: String): ApiClientError()
data class RequestReadFailed(val e: Exception): ApiClientError()
data class ClientParseError(val error: ParseError): ApiClientError()