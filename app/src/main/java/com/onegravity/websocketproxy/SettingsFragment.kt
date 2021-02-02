package com.onegravity.websocketproxy

import android.os.Bundle
import android.widget.Toast
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.net.Inet4Address
import java.net.NetworkInterface

class SettingsFragment : PreferenceFragmentCompat() {

    private val logger = LoggerImpl(BuildConfig.DEBUG)

    private val ipAddress: String by lazy {
        getIpv4HostAddress()
    }

    private fun getIpv4HostAddress(): String {
        NetworkInterface.getNetworkInterfaces()?.toList()
            ?.map { networkInterface ->
                networkInterface.inetAddresses?.toList()
                    ?.find { !it.isLoopbackAddress && it is Inet4Address }
                    ?.let { return it.hostAddress }
            }
        return ""
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val settings = PreferenceManager.getDefaultSharedPreferences(activity)
        val portValue = settings.getString("port", "4444")
        val addressValue = settings.getString("address", "ws://echo.websocket.org:80")

        val switchPref = findPreference<SwitchPreferenceCompat>("proxy_switch")
        val addressPref = findPreference<EditTextPreference>("address")
        val portPref = findPreference<EditTextPreference>("port")

        switchPref?.isChecked = false

        addressPref?.summary = addressValue
        addressPref?.setOnPreferenceChangeListener { preference, newValue ->
            preference.summary = newValue as String
            switchPref?.isChecked = false
            true
        }

        portPref?.summary = if (ipAddress.isNotBlank()) "$portValue ($ipAddress)" else portValue
        portPref?.setOnPreferenceChangeListener { preference, newValue ->
            switchPref?.isChecked = false
            preference.summary = if (ipAddress.isNotBlank()) "$newValue ($ipAddress)" else newValue.toString()
            true
        }

        switchPref?.setOnPreferenceChangeListener { preference, newValue ->
            val enabled = newValue == true

            val address = addressPref?.text ?: "ws://echo.websocket.org:80"
            val port = portPref?.text?.toIntOrNull() ?: 80
            val proxy = WebSocketProxy(logger, address, port)

            val operation = if (enabled) proxy.start() else proxy.stop()
            operation
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        val msg = "Proxy successfully ${if (enabled) "started" else "stopped"}"
                        logger.i(msg)
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    },
                    {
                        val msg = "Proxy failed to ${if (enabled) "start" else "stop"}: ${it.message}"
                        logger.e(msg, it)
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                        (preference as SwitchPreferenceCompat).isChecked = false
                    }
                )
            true
        }
    }

}
