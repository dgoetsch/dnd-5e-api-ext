package web.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.HttpStatusCode
import web.core.*
import web.parse.ParseError

interface ApiClient<T>{
    val httpClient: HttpClient
    val parse: (String) -> Either<ApiClientError, T>

    suspend fun getResourceByUri(uri: String): Either<ApiClientError, T> = Either
            .suspendCatching {
                val prefixedUri = if(uri.startsWith("/")) uri else "/$uri"
                httpClient.get<HttpResponse>("http://localhost:8099$prefixedUri") {}
            }
            .mapLeft { RequestFailed(uri, it) }
            .suspendBindRight { response -> Either
                    .suspendCatching { response.readText() }
                    .mapLeft { RequestReadFailed(uri, it) }
                    .bindRight { body ->
                        when (response.status.value) {
                            in 200..299 -> parse(body)
                            else -> Left(UnsuccessfulRequest(response.status, body))
                        }
                    }
            }
}

sealed class ApiClientError
data class UnsuccessfulRequest(val statusCode: HttpStatusCode, val body: String): ApiClientError()
data class RequestFailed(val uri: String, val e: Exception): ApiClientError()
data class RequestReadFailed(val uri: String, val e: Exception): ApiClientError()
data class ClientParseError(val responseBody: String, val error: ParseError): ApiClientError()