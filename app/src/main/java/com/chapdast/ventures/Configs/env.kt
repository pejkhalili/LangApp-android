package com.chapdast.ventures.Configs

import com.google.firebase.analytics.FirebaseAnalytics

/**
 * Created by pejman on 5/25/18.
 */



//Time Limit Of Number Of Quest
val QUEST_TIME_LIMIT = 60*60*24

val PAY_TAG = "mk-pay"
//Main Server ADDRESS

val SERVER_ADDRESS = "https://www.hellogram.app/EnglishApp/sendrequest.php"
val MEDIA_SERVER_ADDRESS = "https://www.hellogram.app/EnglishApp/"

//Support Creds

val SUPP_TELL = "02154872052"
val SUPP_EMAIL = "info@aban.mobi"


//Analytics Server Address

val ANA_SERVER = "https://www.hellogram.app/EnglishApp/ana/ana.php"

val SMS_LINE_NO = "98308240"

// yandex Translate
val TRANS_SERVER_ADDRESS="https://translate.yandex.net/api/v1.5/tr.json/translate"
val TRANS_KEY = "trnsl.1.1.20180525T130433Z.43cc22f3a3e31cf2.e5e668ce174e2d2a6f63393fd889285fed6578e4"

val DESC_TRANS = "https://owlbot.info/api/v2/dictionary/"

val RSA_KEY="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCAOb9udhWv2o57oSCFj/Nh+SV20Fex2RuWVw0Dz4yinJQ/4RmvnKjskmWy1Y9xD9IEUEa4aa8RlD9zC1ebjGY9DcBH1TfpHFWMI7B3+ddjk2xrdnDOpHYzU/8umUG1SAujP1bbm6xHXLLVwqd5xKAVBv6h6LphDFXTQS7fgPITEwIDAQAB"
val SUK_KEY = "ABAHEL30"

var smsCode=-1
var gotSms=false
val smsAct="mk.com.chapdast.ventures.smscode"

// REQUEST CODES
val STRG_REQUEST_CODE = 8600
val CAT_STRG_REQUEST_CODE = 8601
val SMS_REC_CODE = 9880
val IMEI_CODE = 9881

var FIREBASE_CLI :FirebaseAnalytics?=null