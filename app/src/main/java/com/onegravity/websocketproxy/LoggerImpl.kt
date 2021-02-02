package com.onegravity.websocketproxy

import android.util.Log

class LoggerImpl(private val loggingEnabled: Boolean) : Logger {

    private val tag = "WebsocketProxy"

    override fun v(msg: String) {
        val checkedTag = checkTag(tag)
        if (loggingEnabled && Log.isLoggable(checkedTag, Log.VERBOSE)) {
            Log.v(checkedTag, msg)
        }
    }

    override fun d(msg: String) {
        val checkedTag = checkTag(tag)
        if (loggingEnabled && Log.isLoggable(checkedTag, Log.DEBUG)) {
            Log.d(checkedTag, msg)
        }
    }

    override fun i(msg: String) {
        val checkedTag = checkTag(tag)
        if (loggingEnabled && Log.isLoggable(checkedTag, Log.INFO)) {
            Log.i(checkedTag, msg)
        }
    }

    override fun w(msg: String) {
        val checkedTag = checkTag(tag)
        if (loggingEnabled && Log.isLoggable(checkedTag, Log.WARN)) {
            Log.w(checkedTag, msg)
        }
    }

    override fun w(msg: String, t: Throwable) {
        val checkedTag = checkTag(tag)
        if (loggingEnabled && Log.isLoggable(checkedTag, Log.WARN)) {
            Log.w(checkedTag, msg, t)
        }
    }

    override fun e(msg: String) {
        val checkedTag = checkTag(tag)
        if (Log.isLoggable(checkedTag, Log.ERROR)) {
            Log.e(checkedTag, msg)
        }
    }

    override fun e(msg: String, t: Throwable) {
        val checkedTag = checkTag(tag)
        if (Log.isLoggable(checkedTag, Log.ERROR)) {
            Log.e(checkedTag, msg, t)
        }
    }

    private fun checkTag(tag: String) = if (tag.length > 23) tag.substring(0, 23) else tag

}
