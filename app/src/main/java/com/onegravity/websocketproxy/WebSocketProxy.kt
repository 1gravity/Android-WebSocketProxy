package com.onegravity.websocketproxy

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.CompletableEmitter
import io.reactivex.rxjava3.schedulers.Schedulers
import java.net.URI

class WebSocketProxy(
    private val logger: Logger,
    private val serverAddress: String,
    private val port: Int
) {

    private var client: Client? = null
    private var server: Server? = null

    fun start(): Completable = Completable.create { emitter ->
        try {
            // client connecting to the target address
            client = Client(URI(serverAddress), logger)

            // server listening to incoming messages
            server = Server(port, logger)

            // start client first, then server
            startProxy(client, server, emitter)
        } catch (e: Exception) {
            emitter.onError(e)
        }
    }

    private fun startProxy(client: Client?, server: Server?, emitter: CompletableEmitter) {
        logger.i("Starting proxy")

        client?.events()
            ?.subscribeOn(Schedulers.io())
            ?.subscribe( {
                when (it) {
                    is Started -> startServer(client, server, emitter)
                    is Message -> server?.broadcast(it.content)
                    is MessageByte -> server?.broadcast(it.content)
                    is Error -> emitter.onError(it.ex)
                }
            }, {
                logger.e("Error processing server events", it)
            } )

        try {
            client?.connect()
        } catch (e: Exception) {
            emitter.onError(e)
        }
    }

    private fun startServer(client: Client?, server: Server?, emitter: CompletableEmitter) {
        server?.events()
            ?.subscribeOn(Schedulers.io())
            ?.subscribe( {
                when (it) {
                    is Started -> emitter.onComplete()
                    is Message -> client?.send(it.content)
                    is MessageByte -> client?.send(it.content)
                    is Error -> {}
                }
            }, {
                logger.e("Error processing client events", it)
            } )

        // server listening to incoming connections
        try {
            server?.start()
        } catch (e: Exception) {
            emitter.onError(e)
        }
    }

    fun stop(): Completable = Completable.create { emitter -> 
        logger.i("Stopping proxy")
        try {
            server?.stop()
            client?.close()
            emitter.onComplete()
        } catch (e: Exception) {
            emitter.onError(e)
        }
    }

}
