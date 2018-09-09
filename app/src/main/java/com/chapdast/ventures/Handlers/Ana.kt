package com.chapdast.ventures.Handlers

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.TelephonyManager
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.chapdast.ventures.Configs.ANA_SERVER
import com.chapdast.ventures.Configs.FIREBASE_CLI
import com.chapdast.ventures.Configs.SPref
import com.google.firebase.analytics.FirebaseAnalytics

import ir.mono.monolyticsdk.Monolyitcs


@Suppress("DEPRECATION")
@SuppressLint("Registered")
/**
 * Created by pejman on 5/27/18.
 */
class Ana(context: Context) : Activity() {
    val con = context
    var userId: String = SPref(con.applicationContext, "userCreds")!!.getString("userId", "installl")
    var pm = con.packageManager.getPackageInfo(con.applicationInfo.packageName.toString(), 0)
    var ver: String = "${pm.versionName.toString()}/${pm.versionCode.toString()}"

    //    var imei  = SPref(con,"ana")!!.getString("imei","NotAllowed")
    var tm = con.applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val perm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        con.checkSelfPermission("android.permission.READ_PHONE_STATE")
    } else {
        PackageManager.PERMISSION_GRANTED
    }


    @SuppressLint("MissingPermission")
    var imei = if (perm == PackageManager.PERMISSION_GRANTED) tm.deviceId else "not_allowed"
//    var imei = tm.allCellInfo.toString()

    fun sub() {
        val SUB = SPref(con, "ana")!!.getBoolean("sub", false)
        if (!SUB) {
            Log.d("RESULT-sub", "event to sub imei to $imei ver $ver phone $userId")
            SendApiRequest(mapOf<String, String>("event" to "sub", "imei" to imei.toString(), "ver" to ver, "phone" to userId))
            SPref(con, "ana")!!.edit().putBoolean("sub", true).apply()
        }
    }

    fun unSub(): Boolean {

        Log.d("RESULT-unsub", "event to unsub imei to $imei ver $ver phone $userId")
        return SendApiRequest(mapOf<String, String>("event" to "unsub", "imei" to imei.toString(), "ver" to ver, "phone" to userId))
    }

    fun splash() {

        val SPLASH = SPref(con, "ana")!!.getBoolean("splash", false)
        if (!SPLASH) {
            Log.d("RESULT-spl", "event to splash imei to $imei ver $ver phone $userId")
            SendApiRequest(mapOf<String, String>("event" to "splash", "imei" to imei.toString(), "ver" to ver))
            SPref(con, "ana")!!.edit().putBoolean("splash", true).apply()
        }
    }

    fun install() {

        val INSTALLED = SPref(con, "ana")!!.getBoolean("install", false)
        if (!INSTALLED) {
            Log.d("RESULT-inst", "event to Install imei to $imei ver $ver phone $userId")
            SendApiRequest(mapOf<String, String>("event" to "install", "imei" to imei.toString(), "ver" to ver))
            SPref(con, "ana")!!.edit().putBoolean("install", true).apply()
        }
    }

    fun reLog(phone: String) {
        userId = phone
//        val INSTALLED = SPref(con, "ana")!!.getBoolean("relogin", false)
//        if (!INSTALLED) {
            Log.d("RESULT-relog", "event to Relogin imei to $imei ver $ver phone $userId")
            SendApiRequest(mapOf<String, String>("event" to "relogin", "imei" to imei.toString(), "ver" to ver, "phone" to userId))
            SPref(con, "ana")!!.edit().putBoolean("install", true).apply()
//        }
    }

    fun SendApiRequest(data: Map<String, String>): Boolean {

        try {
            var event: String? = data.get("event")
            var cat = event
            var act = event
            var lbl = LableMaker(event!!)
            var res = Monolyitcs.addEvent(con, cat, act, lbl, 1)
            if (FIREBASE_CLI != null) {
                var bundle: Bundle = Bundle()
                bundle.putString("userId", userId)
                bundle.putString("version", ver)
                bundle.putString("deviceId", imei)
                bundle.putString("event", event)
                FIREBASE_CLI!!.logEvent(event, bundle)
                Log.i("FRIEBASE","$event on FB Called!")
            }
            Log.d("Result_mono", res.toString())
            var sendReq = khttp.post(ANA_SERVER, data = data)
            if (sendReq.statusCode == 200) {
                Log.d("RESULT-req", sendReq.jsonObject.toString())
                var res = sendReq.jsonObject
                return res.getBoolean("result")
            }
        } catch (e: Exception) {
            Log.d("mk", e.message)
//            SendApiRequest(data)
        }
        return false
    }

    fun LableMaker(event: String): String {
        when (event) {
            "sub" -> {
                return "Subscribe"
            }
            "unsub" -> {
                return "Unsubscribe"
            }
            "install" -> {
                return "Install"
            }
            "splash" -> {
                return "Splash"
            }
        }
        return "notSpec"
    }


}