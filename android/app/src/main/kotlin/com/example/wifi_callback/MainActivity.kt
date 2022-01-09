package com.example.wifi_callback
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES

import android.view.View
import android.os.Bundle
import android.widget.Toast
import android.widget.Button
import android.app.admin.DevicePolicyManager

import android.provider.Settings.EXTRA_WIFI_NETWORK_LIST
import android.net.wifi.WifiNetworkSuggestion
import android.provider.Settings.ACTION_WIFI_ADD_NETWORKS

import androidx.annotation.NonNull

import android.provider.Settings.EXTRA_WIFI_NETWORK_LIST
import android.provider.Settings.EXTRA_WIFI_NETWORK_RESULT_LIST
import android.provider.Settings.ADD_WIFI_RESULT_SUCCESS
import android.provider.Settings.ADD_WIFI_RESULT_ADD_OR_UPDATE_FAILED
import android.provider.Settings.ADD_WIFI_RESULT_ALREADY_EXISTS


class MainActivity: FlutterActivity() {
  private val CHANNEL = "samples.flutter.dev/battery"
  //private var giveCallback = 0
  protected lateinit var myResult : MethodChannel.Result

  override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
    super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
      // Note: this method is invoked on the main thread.
      call, result -> myResult = result

      if (call.method == "connect") {
        val ssid = call.argument<String>("ssid")
        val password = call.argument<String>("password")
        if (ssid != null && password != null) {
            connectToWiFi(ssid, password)
        }
      }


    }

  }


    fun connectToWiFi(ssid: String?, password: String?) {
        if (ssid != null && password != null) {
            val suggestion = WifiNetworkSuggestion.Builder()
                .setSsid(ssid ?: "")
                .setWpa2Passphrase(password ?: "")
                .build()

            val intent = Intent(ACTION_WIFI_ADD_NETWORKS)
            intent.putExtra(EXTRA_WIFI_NETWORK_LIST, arrayListOf(suggestion))
            startActivityForResult(intent, 1002)
        }
    }

    override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK) {
            // user agreed to save configurations: still need to check individual results
            if (data != null && data.hasExtra(EXTRA_WIFI_NETWORK_RESULT_LIST)) {
                for (code in data.getIntegerArrayListExtra(EXTRA_WIFI_NETWORK_RESULT_LIST).orEmpty()) {
                    if(code == ADD_WIFI_RESULT_SUCCESS) {
                        Toast.makeText(this, "WIFI CONNECTED SUCCESSFULLY", Toast.LENGTH_SHORT).show()
                        myResult.success(true)
                    }
                     else if(code == ADD_WIFI_RESULT_ADD_OR_UPDATE_FAILED) {
                        Toast.makeText(this, "FAILED TO CONNECT", Toast.LENGTH_SHORT).show()
                        myResult.success(false)
                    } else if(code == ADD_WIFI_RESULT_ALREADY_EXISTS) {
                        Toast.makeText(this, "WIFFI ALREADY SAVED, TURN ON TO AUTO CONNECT", Toast.LENGTH_SHORT).show()
                        myResult.success(false)
                    }else {
                        Toast.makeText(this, "SOME ERROR OCCURED", Toast.LENGTH_SHORT).show()
                        myResult.success(false)
                    }
                }
            }
        } else {
            // User refused to save configurations
            Toast.makeText(this, "SOME ERROR OCCURED, USER REFUSED TO SAVEs", Toast.LENGTH_SHORT).show()
            myResult.success(false)
        }
  }



}
