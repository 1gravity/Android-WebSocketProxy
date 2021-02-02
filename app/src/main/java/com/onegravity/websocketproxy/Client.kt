package com.onegravity.websocketproxy

import com.jakewharton.rxrelay3.ReplayRelay
import io.reactivex.rxjava3.core.Observable
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

class Client(serverURI: URI, private val logger: Logger) : WebSocketClient(serverURI) {

    private val events = ReplayRelay.create<Event>()

    fun events(): Observable<Event> = events

    override fun onOpen(handshakedata: ServerHandshake?) {
        events.accept(Started)
        logger.i("${javaClass.simpleName} - opened connection")
    }

    override fun onMessage(message: String) {
        events.accept(Message(message))
        logger.i("${javaClass.simpleName} - received $message")
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        // The codecodes are documented in class org.java_websocket.framing.CloseFrame
        logger.i(
            "${javaClass.simpleName} - connection closed by " + (if (remote) "remote peer" else "us") + " Code: " + code + " Reason: "
                    + reason
        )
    }

    override fun onError(ex: Exception) {
        events.accept(Error(ex))
        logger.e("${javaClass.simpleName}  - error: ${ex.message}")
    }

}