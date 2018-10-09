package com.chapdast.ventures.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import com.android.billingclient.util.IabHelper
import com.android.billingclient.util.IabResult
import com.android.billingclient.util.Purchase
import com.chapdast.ventures.*
import com.chapdast.ventures.Configs.*
import com.chapdast.ventures.Handlers.Ana


import kotlinx.android.synthetic.main.activity_splash_page.*
import kotlinx.android.synthetic.main.splash_screen_five.*
import kotlinx.android.synthetic.main.splash_screen_four.*
import kotlinx.android.synthetic.main.splash_screen_one.*
import kotlinx.android.synthetic.main.splash_screen_three.*
import kotlinx.android.synthetic.main.splash_screen_two.*
import net.jhoobin.jhub.CharkhoneSdkApp
import net.jhoobin.jhub.util.AccountUtil
import org.json.JSONObject
import java.util.*


@Suppress("DEPRECATION")
open class SplashPage : ChapActivity(), View.OnClickListener {

    val HANDELER = Handler()
    var first_time = false
    lateinit var mHelper: IabHelper
    var payloadJoob = "subscribe"
        lateinit var codeRecReceiver: CodeReceiver
    lateinit var fill: IntentFilter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ChapActivity.netCheck(this)) {
            setContentView(R.layout.activity_splash_page)
            CharkhoneSdkApp.initSdk(applicationContext, getSecrets(), true, R.mipmap.icon)
            var ANA = Ana(applicationContext)
            ANA!!.loginPage()
            pageDots.visibility = View.GONE

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                setupPermissions()
            }

            codeRecReceiver = CodeReceiver()

            Log.e(PAY_TAG, "Rec Lockin")
            fill = IntentFilter()
            fill.addAction(smsAct)
            applicationContext.registerReceiver(codeRecReceiver, fill)

            sendBroadcast(Intent("mk.mk.mk"))

            Log.e(PAY_TAG, "Rec LOCKED")


            mHelper = IabHelper(applicationContext, RSA_KEY)
            mHelper.enableDebugLogging(false, "$PAY_TAG-deb")
            Log.d(PAY_TAG, mHelper.toString())
            Log.d(PAY_TAG, "Starting setup.")
            mHelper.startSetup(PayIabListener())


            lb.typeface = HelloApp.IRANSANS
//            sp_two_intro.typeface = HelloApp.IRANSANS_BLACK
            sp_three_num_box.typeface = HelloApp.IRANSANS
            sp_three_price.typeface = HelloApp.IRANSANS

            sp_four_confirm_box.typeface = HelloApp.IRANSANS


            val elm = findViewById<View>(R.id.spf_elm) as Button
            val int = findViewById<View>(R.id.spf_inter) as Button
            val adv = findViewById<View>(R.id.spf_adv) as Button
            val fof = findViewById<View>(R.id.spf_504) as Button
            val oneOone = findViewById<View>(R.id.spf_tofel) as Button

            elm.typeface = HelloApp.IRANSANS
            int.typeface = HelloApp.IRANSANS
            adv.typeface = HelloApp.IRANSANS
            fof.typeface = HelloApp.IRANSANS
            oneOone.typeface = HelloApp.IRANSANS

            sp_three_num_box.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.ACTION_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
//                    splash3()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm!!.hideSoftInputFromWindow(sp_three_num_box.getWindowToken(), 0)
                    return@OnKeyListener true
                }
                false
            })

            sp_four_confirm_box.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.ACTION_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
//                    splash4()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(sp_four_confirm_box.getWindowToken(), 0)
                    return@OnKeyListener true
                }
                false
            })


//            sp_one_start.setOnClickListener(this)
//            sp_two_next.setOnClickListener(this)
//            sp_two_prev.setOnClickListener(this)
            sp_three_confirm.setOnClickListener(this)
            sp_four_confirm.setOnClickListener(this)
            sp_four_change_number.setOnClickListener(this)
            sp_four_change_number.typeface = HelloApp.IRANSANS
            sp_four_confirm.typeface = HelloApp.IRANSANS
            sp_three_confirm.typeface = HelloApp.IRANSANS
            sp_three_text.typeface = HelloApp.IRANSANS
            sp_four_text.typeface = HelloApp.IRANSANS


            elm.setOnClickListener { intenter(applicationContext, 1) }
            int.setOnClickListener { intenter(applicationContext, 2) }
            adv.setOnClickListener { intenter(applicationContext, 3) }
            fof.setOnClickListener { intenter(applicationContext, 4) }
            oneOone.setOnClickListener { intenter(applicationContext, 5) }
        }
    }

    override fun onClick(item: View?) {

        if (item != null) {
            when (item.id) {
/*
                R.id.sp_one_start -> {
                    splash1()
                }

                R.id.sp_two_next -> {
                    splash2_next()
                }

                R.id.sp_two_prev -> {
                    splash2_prev()
                }
*/
                R.id.sp_three_confirm -> {
                    //check Phone Number

                    splash3()
                }

                R.id.sp_four_confirm -> {
                    //check Entered RespCode With server or ...

                    splash4()
                }

                R.id.sp_four_change_number -> {
                    sp3.visibility = View.VISIBLE
                    sp4.visibility = View.GONE
                }

            }
        }
    }

    inner class CodeReceiver : BroadcastReceiver() {

        override fun onReceive(p0: Context?, p1: Intent?) {

            if (p1!!.action == smsAct) {

                var tempUserId = SPref(applicationContext,"Temp")!!.getString("userId",0.toString())
                sp_four_confirm_box.setText(p1!!.getStringExtra("code").toString())
                var ANA = Ana(applicationContext)
                ANA!!.recievedCode(tempUserId)

//                splash4()

            }
        }


    }

    //joobin
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 9009) {
            Log.d("$PAY_TAG-onActRes", "onActivityResult(" + requestCode + "," + resultCode + "," + data);
            if (mHelper == null) return
            // Pass on the activity result to the helper for handling
            if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
                super.onActivityResult(requestCode, resultCode, data)
            } else {
                Log.d("$PAY_TAG-OnActRes", "onActivityResult handled by IABUtil.")
            }
        }
    }

    inner class PayIabListener : IabHelper.OnIabSetupFinishedListener {
        override fun onIabSetupFinished(result: IabResult?) {
            Log.d("$PAY_TAG-iab", result!!.message)
            if (!result.isSuccess) return
            if (mHelper == null) return
//             Let's get an inventory of stuff we own.
            Log.d("$PAY_TAG", "Setup successful. Querying inventory.");
            try {

//                mHelper.queryInventoryAsync(mGotInventoryListener(mHelper))

            } catch (e: IabHelper.IabAsyncInProgressException) {
                Log.e("$PAY_TAG", "Error querying inventory. Another async operation inprogress. ${e.message}")
            }
        }

    }

    inner class PayDown : IabHelper.OnIabPurchaseFinishedListener {

        override fun onIabPurchaseFinished(result: IabResult?, purchase: Purchase?) {
            Log.d("$PAY_TAG", "Purchase finished: " + result + ",\npurchase: " + purchase.toString())
            SPref(applicationContext, "purchase")!!.edit().putString("purchase", purchase.toString()).apply()
            // if we were disposed of in the meantime, quit.
            if (mHelper == null) {
                Log.d("$PAY_TAG", "mHelper is Null")
                return
            }

            if (result!!.isFailure) {
                Log.i("$PAY_TAG", "Error purchasing: " + result)
                return
            }

            var ANA = Ana(applicationContext)
            var tempUserId = SPref(applicationContext,"Temp")!!.getString("userId",0.toString())
            ANA!!.sub(tempUserId)

            var userId = SPref(applicationContext, "userCreds")!!.getString("insertedPhone", "null")
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

            StrictMode.setThreadPolicy(policy)
            if (userId.equals("null")) Log.d("Err1", userId.toString())
            var purchaseToken = SPref(applicationContext, "purchase")!!.getString("purchase", null)
            if (purchaseToken != null) {
                var puJson: JSONObject = JSONMAKER(purchaseToken)
                var resp = khttp.post(SERVER_ADDRESS, data = mapOf(
                        "m" to "register",
                        "phone" to userId,
                        "purchaseToken" to puJson.getString("purchaseToken"),
                        "purchaseTime" to puJson.getString("purchaseTime"),
                        "orderId" to puJson.getString("orderId"),
                        "msisdn" to puJson.getString("msisdn")))
                if (resp.statusCode == 200) {

                    try {
                        var jes = resp.jsonObject
                        if (jes.getBoolean("result")) {
                            //send to responseble service provider
                            SPref(applicationContext, "userCreds")!!.edit().putString("userId", userId).apply() // set userid to old userId field
                            Log.i("SET_ID", "USER SET $userId")
                            sp3.visibility = View.GONE
                            sp5.visibility = View.VISIBLE
                            pageDots.visibility = View.INVISIBLE
                            //send Request To register
                            //Load next Page
                            intenter(applicationContext, 1, false)
                            Log.d("$PAY_TAG-OnFine", "PURCHASE COMP!")
                        } else if (!jes.getBoolean("result")) {
                            Log.d("Err1", jes.toString())
                        }
                    } catch (e: Exception) {
                        Log.d("Err1", e.message)
                    }
                } else {
                    Log.d("Err1", resp.statusCode.toString())
                }

                Log.d("$PAY_TAG", "Purchase successful.");
            }
        }

    }

    fun launchPay() {

        try {
            mHelper.launchSubscriptionPurchaseFlow(
                    this,
                    SUK_KEY,
                    9009,
                    PayDown(),
                    payloadJoob)
        } catch (e: Exception) {
            Log.e("$PAY_TAG", "Error-->> " + e.message)
        }
    }

    fun getSecrets(): Array<out String>? {
        return resources.getStringArray(R.array.secrets)
    }


    //joobin

//    fun splash1() {
//        sp1.visibility = View.GONE
//        sp2.visibility = View.VISIBLE
//        sp_p1.setImageDrawable(applicationContext.resources.getDrawable(R.mipmap.yellow_dot))
//        sp_p2.setImageDrawable(applicationContext.resources.getDrawable(R.mipmap.dot))
//    }
//
//    fun splash2_next() {
//        sp_three_num_box.setText(SPref(this, "userCreds")!!.getString("number", ""))
//        sp2.visibility = View.GONE
//        sp3.visibility = View.VISIBLE
//        sp_p2.setImageDrawable(applicationContext.resources.getDrawable(R.mipmap.yellow_dot))
//        sp_p3.setImageDrawable(applicationContext.resources.getDrawable(R.mipmap.dot))
//        pageDots.visibility = View.INVISIBLE
//    }
//
//    fun splash2_prev() {
//        sp2.visibility = View.GONE
//        sp1.visibility = View.VISIBLE
//        sp_p1.setImageDrawable(applicationContext.resources.getDrawable(R.mipmap.yellow_dot))
//        sp_p2.setImageDrawable(applicationContext.resources.getDrawable(R.mipmap.dot))
//    }

    fun splash3() {
        var phoneNum = sp_three_num_box.text.toString()
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        if (phoneNum.length > 10) {

            SPref(applicationContext, "userCreds")!!.edit().putString("insertedPhone", phoneNum).apply()

            var getUserStatus = khttp.post(SERVER_ADDRESS, data = mapOf("m" to "checkUser", "phone" to phoneNum))
            Log.d("USTAT", getUserStatus.statusCode.toString())
            if (getUserStatus.statusCode == 200) {
                Log.d("USTAT", getUserStatus.text.toString())
                var jes = getUserStatus.jsonObject
                if (jes.getBoolean("result")) {


                    if (jes.getString("status") == "sub") {
                        var ANA = Ana(applicationContext)
                        ANA!!.reLog(phoneNum)
                    }



                    if (CheckPhoneNumber(phoneNum, 0)) {
                        SPref(applicationContext, "Temp")!!.edit().putString("userId", phoneNum).commit()
                        var ANA = Ana(applicationContext)
                        ANA!!.requestCode(phoneNum)
                        AccountUtil.removeAccount()
                        var fillNumber = Intent()
                        fillNumber.putExtra("msisdn", phoneNum)
                        fillNumber.putExtra("editAble", false)
                        fillNumber.putExtra("autoRenewing", true)
                        mHelper.setFillInIntent(fillNumber)
                        launchPay()
                    }
                    else if (CheckPhoneNumber(phoneNum, 1)) {


                        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                        StrictMode.setThreadPolicy(policy)

                        var sendMessage = khttp.post(SERVER_ADDRESS, data = mapOf("m" to "sendsms", "phone" to phoneNum))


                        if (sendMessage.statusCode == 200) {
                            var jes = sendMessage.jsonObject
                            Log.i("ERR_SMS", "RSP:" + jes.toString())
                            if (jes.getBoolean("result") && jes.getBoolean("status")) {
                                var ANA = Ana(applicationContext)
                                ANA!!.requestCode(phoneNum)
                                SPref(applicationContext, "Temp")!!.edit().putString("userId", phoneNum).commit()
                                SPref(applicationContext, "userCreds")!!.edit().putString("activeCode", jes.getString("tid")).apply()
                                sp3.visibility = View.GONE
                                sp_four_confirm_box.setText("");
                                sp4.visibility = View.VISIBLE

                            } else {
                                Log.d("ERR_SMS", "3cant Send")
                                sToast(applicationContext, getString(R.string.pleaseTryAgain), true)
                                var ANA = Ana(applicationContext)
                                ANA!!.mciFail(phoneNum)
                            }
                        }

                    }
                    else {
                        var ANA = Ana(applicationContext)
                        ANA!!.NotSupported(phoneNum)
                        sToast(applicationContext, resources.getString(R.string.unSupportedNumber), false)
                    }
                }
            }


        }
        else {
            var ANA = Ana(applicationContext)
            ANA!!.wrongNumber(phoneNum)
            sToast(this, applicationContext.resources.getString(R.string.wrongNum))
        }
    }

    fun splash4() {
        var UserId = SPref(applicationContext, "userCreds")!!.getString("insertedPhone", 0.toString())
        var PurchaseToken = SPref(this, "userCreds")!!.getString("activeCode", 0.toString()).replace("SUCCESS.", "")
        var verfiyText = sp_four_confirm_box.text.toString()
        Log.e(PAY_TAG, "+++++++ $PurchaseToken, $verfiyText")

        if (verfiyText.length == 4) {
            var ANA = Ana(applicationContext)
            var tempUserId = SPref(applicationContext,"Temp")!!.getString("userId",0.toString())
            ANA!!.sub(tempUserId)
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            Log.d("Err1", UserId.toString())
            var resp = khttp.post(SERVER_ADDRESS, data = mapOf("m" to "register", "phone" to UserId, "tid" to PurchaseToken, "pin" to verfiyText))
            Log.e("ERR_CONFI", "RSP:" + resp.text.toString())
            if (resp.statusCode == 200) {
                SPref(applicationContext, "userCreds")!!.edit().putString("userId", UserId).apply() // set userid to old userId field
                Log.e("SET_ID", "USER SET $UserId")
                try {
                    var jes = resp.jsonObject
                    if (jes.getBoolean("result")) {
                        intenter(applicationContext, 0, false)
                        //send to responseble service provider
                        sp4.visibility = View.GONE
                        sp5.visibility = View.VISIBLE
                        //send Request To register
                        //Load next Page
                    } else if (!jes.getBoolean("result")) {
                        Log.d("Err1", jes.toString())
                        sToast(applicationContext, getString(R.string.pleaseTryAgain), true)
                        sp4.visibility = View.GONE
                        sp3.visibility = View.VISIBLE
                    }
                } catch (e: Exception) {
                    sToast(applicationContext, getString(R.string.cantConnectToServer), true)
                    Log.d("Err1", e.message)
                }
            } else {
                sToast(applicationContext, getString(R.string.cantConnectToServer), true)
                Log.d("Err1", resp.statusCode.toString())
            }
            //send the level selection Page


        } else {
            sToast(applicationContext, getString(R.string.code_not_match))
        }
    }

    fun intenter(context: Context, lvl: Int, loadHub: Boolean = true) {
        var userId = SPref(this, "userCreds")!!.getString("userId", null)
        if (userId != null) {
            SPref(this, "level")!!.edit().putInt("level", lvl).apply()
            var strLevel: String? = null
            when (lvl) {
                1 -> strLevel = "elementry"
                2 -> strLevel = "intermediate"
                3 -> strLevel = "advance"
                4 -> strLevel = "504"
                5 -> strLevel = "tofel"
                else -> strLevel = "elementry"
            }
//            sToast(applicationContext,"Level Set To :" + strLevel)
            var setLevel = khttp.post(SERVER_ADDRESS, data = mapOf("m" to "setLevel", "phone" to userId, "level" to strLevel))
            if (setLevel.statusCode == 200) {
                try {
                    var res: JSONObject = setLevel.jsonObject
                    if (res.getBoolean("result") && loadHub) {
                        var intent = Intent(context, Hub::class.java)
                        finish()
                        startActivity(intent)
                    } else {
//                        sToast(applicationContext, "ERROR:Result" + res.getBoolean("result"))
                    }
                } catch (e: Exception) {
                    Log.d("Err", e.message)
                }

            } else {
//                sToast(applicationContext,"Stat:"+setLevel.statusCode)
            }
        } else {
//            Toast.makeText(applicationContext,"User Id Not Found",Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupPermissions() {
        val permission = applicationContext.checkSelfPermission("android.permission.RECEIVE_SMS")

        if (permission != PackageManager.PERMISSION_GRANTED) {

            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
                arrayOf("android.permission.RECEIVE_SMS", "android.permission.READ_PHONE_STATE"),
                SMS_REC_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            SMS_REC_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    Log.i("SMS_PRR", "Permission has been denied by user")
                } else {
                    Log.i("SMS_PRR", "Permission has been granted by user")
                }
            }
        }
    }

    override fun onBackPressed() {

//        if (sp2.visibility == View.VISIBLE) {
//            sp1.visibility = View.VISIBLE
//            sp2.visibility = View.INVISIBLE
//        } else {
        if (first_time) {
            finish()
            finishAffinity()
        } else {
            sToast(applicationContext, resources.getString(R.string.exit), true)
            first_time = true
            HANDELER.postDelayed(Runnable {
                first_time = false
            }, 2000)
        }

//        }

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("$PAY_TAG-Disp", "Destroying helper.")
        if (mHelper != null) {
            mHelper.disposeWhenFinished()
//            mHelper = null;
        }

//        applicationContext.unregisterReceiver(codeRecReceiver)


    }



}

