package com.onegravity.websocketproxy

import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import com.jakewharton.rxrelay3.ReplayRelay
import io.reactivex.rxjava3.core.Observable

sealed class Event
object Started : Event()
class Message(val content: String) : Event()
class MessageByte(val content: ByteBuffer) : Event()
class Error(val ex: Exception) : Event()

class Server(port: Int, private val logger: Logger) : WebSocketServer(InetSocketAddress(port)) {

    private val events = ReplayRelay.create<Event>()

    fun events(): Observable<Event> = events

    override fun onStart() {
        logger.i("${javaClass.simpleName} - successfully started!")
        events.accept(Started)
        connectionLostTimeout = 0
        connectionLostTimeout = 100
    }

    override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
        conn?.send("Welcome to the server!")
        logger.i("${conn?.remoteSocketAddress?.address?.hostAddress} connected")
    }

    override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        logger.i("${conn?.remoteSocketAddress?.address?.hostAddress} disconnected")
    }

    override fun onMessage(conn: WebSocket?, message: String?) {
        if (message != null) events.accept(Message(message))
        logger.i("${conn?.remoteSocketAddress?.address?.hostAddress}: $message")
    }

    override fun onMessage(conn: WebSocket?, message: ByteBuffer?) {
        if (message != null) events.accept(MessageByte(message))
        logger.i("${conn?.remoteSocketAddress?.address?.hostAddress}: $message")
    }

    override fun onError(conn: WebSocket?, ex: Exception?) {
        if (ex != null) events.accept(Error(ex))
        logger.e("${javaClass.simpleName} - error: ${ex?.message}")
    }

}
