package tech.kocel.jiratimelogger

import io.netty.handler.logging.LogLevel
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import reactor.netty.resources.ConnectionProvider.DEFAULT_POOL_ACQUIRE_TIMEOUT
import reactor.netty.transport.logging.AdvancedByteBufFormat
import java.time.Duration

@Suppress("LongParameterList")
fun webClient(
    webClientBuilder: WebClient.Builder,
    verboseLogging: Boolean,
    baseUrl: String,
    user: String,
    password: String,
    connectionProviderName: String,
    pendingMaxCount: Int,
    connectTimeout: Long = 5,
    readTimeout: Long = 5,
    writeTimeout: Int = 5,
    pendingAcquireTimeout: Long = DEFAULT_POOL_ACQUIRE_TIMEOUT,
    idleTimeoutSeconds: Long = AWS_TIMEOUT
): WebClient =
    webClientBuilder
        .build()
        .mutate()
        .clientConnector(
            ReactorClientHttpConnector(
                HttpClient
                    .create(
                        ConnectionProvider
                            .builder(connectionProviderName)
                            .maxIdleTime(Duration.ofSeconds(idleTimeoutSeconds))
                            .maxConnections(ConnectionProvider.DEFAULT_POOL_MAX_CONNECTIONS)
                            .pendingAcquireMaxCount(pendingMaxCount)
                            .pendingAcquireTimeout(Duration.ofMillis(pendingAcquireTimeout))
                            .build()
                    ).followRedirect(true)
                    .apply {
                        if (verboseLogging) {
                            wiretap(
                                "reactor.netty.http.client.HttpClient",
                                LogLevel.DEBUG,
                                AdvancedByteBufFormat.TEXTUAL
                            )
                        }
                    }.responseTimeout(Duration.ofSeconds(connectTimeout))
                    .doOnConnected { connection ->
                        connection
                            .addHandlerLast(ReadTimeoutHandler(readTimeout.toInt()))
                            .addHandlerLast(WriteTimeoutHandler(writeTimeout))
                    }
            )
        ).defaultHeaders { h -> h.setBasicAuth(user, password) }
        .baseUrl(baseUrl)
        .build()

const val AWS_TIMEOUT = 340L
