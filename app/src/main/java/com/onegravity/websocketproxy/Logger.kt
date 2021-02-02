package com.onegravity.websocketproxy

interface Logger {
    fun v(msg: String)

    fun d(msg: String)

    fun i(msg: String)

    fun w(msg: String)
    fun w(msg: String, t: Throwable)

    fun e(msg: String)
    fun e(msg: String, t: Throwable)
}
