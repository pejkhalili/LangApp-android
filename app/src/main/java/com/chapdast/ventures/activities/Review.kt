package com.chapdast.ventures.activities

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.StrictMode
import android.speech.tts.TextToSpeech
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.chapdast.ventures.*
import com.chapdast.ventures.Configs.*

import kotlinx.android.synthetic.main.activity_review.*
import kotlinx.android.synthetic.main.new_quest_loader.*
import kotlinx.android.synthetic.main.review_loader.*
import kotlinx.android.synthetic.main.vw_timer.*
import org.json.JSONException
import org.json.JSONObject

import java.util.*


class Review : AppCompatActivity(), TextToSpeech.OnInitListener {
    var locked = false
    var timeRun = false
    var remainTime: Int = 0
    var tim: CountDownTimer? = null
    var currentQuest = 0
    var ChallengeCount: Int = 20
    var timeForEachChallenge = 20
    var trueAns: String? = null
    var qid = 0
    var iransans: Typeface? = null
    var temp: Any = ""
    var tts: TextToSpeech? = null
    var LoadedReview: Array<MutableMap<String, String>?>? = null
    var allWrong = 0
    var allRight = 0
    var speaker: Boolean = true
    val handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)
        speaker = SPref(applicationContext, "setting")!!.getBoolean("speaker", true)

        var net = isNetworkAvailable(this)
        if (!net) {
            var noCon = Intent(this, NoConnection::class.java)
            startActivity(noCon)
            finish()
        } else {
            GetQuestion()
            var assetManager = applicationContext.assets;
            iransans = Typeface.createFromAsset(assetManager, String.format(Locale.ENGLISH, "fonts/%s", "iransans.ttf"))
            var bebas = Typeface.createFromAsset(assetManager, String.format(Locale.ENGLISH, "fonts/%s", "bebas.otf"))
            timeForEachChallenge = SPref(this, "level")!!.getInt("timeOnChalleng", 20).toInt()
            ChallengeCount = SPref(this, "level")!!.getInt("numChallenge", 20).toInt()
            tts = TextToSpeech(applicationContext, this)

            vw_ans_st.setTypeface(iransans)
            vw_ans_nd.setTypeface(iransans)
            vw_ans_rd.setTypeface(iransans)
            vw_ans_th.setTypeface(iransans)
            vw_ans_un.setTypeface(iransans)
            vw_timer_time.setTypeface(bebas)
            vw_timer_time.setTextSize(35F)

            if (LoadedReview != null) {
                QuestLoader()
            } else {
                End()
            }
            rv_side_menu.setOnClickListener {
                tim?.cancel()
                finish()
            }
            vw_unknown.setOnClickListener { CheckAnswer(0) }
            vw_first_answer.setOnClickListener { CheckAnswer(1) }
            vw_second_answer.setOnClickListener { CheckAnswer(2) }
            vw_third_answer.setOnClickListener { CheckAnswer(3) }
            vw_fourth_answer.setOnClickListener { CheckAnswer(4) }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            var result = tts!!.setLanguage(Locale.US)
            tts!!.setPitch(1.3f)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS_SRV", "SOUND PROBLEM")
            }
        } else {
            Log.e("TTS_SRV", "INIT FAILED!!")
        }
    }

    fun SpeakOut(word: String) {
        tts!!.speak(word, TextToSpeech.QUEUE_FLUSH, null)

    }

    override fun onBackPressed() {
        if (tim != null) {
            tim!!.cancel()
        }
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        if (tim != null) {
            tim!!.cancel()
        }
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }

    fun QuestLoader() {
        if (currentQuest <= LoadedReview!!.lastIndex) {
            var Load = Helper(currentQuest)
            var quest = Load

            var resQuest = quest?.get("res").toString()

            if (quest != null && resQuest == "true") {

                try {
                    vw_count.setText((currentQuest!! + 1).toString() + "/" + (LoadedReview!!.lastIndex + 1).toString())

//                    currentQuest = currentQuest!! + 1

                    temp = SPref(applicationContext, "runReview")!!.edit().putInt("qid", quest.get("qid")!!.toInt()).commit()


//                    sToast(applicationContext, quest.get("res").toString())
                    var questInTitle = quest.get("q").toString().toUpperCase()
                    rv_quest_num.text = "#" + currentQuest.toString() + " " + questInTitle




                    vw_word.setText(quest.get("q").toString().toUpperCase())
                    vw_ans_st.text = quest.get("ans1")
                    vw_ans_nd.text = quest.get("ans2")
                    vw_ans_rd.text = quest.get("ans3")
                    vw_ans_th.text = quest.get("ans4")
                    vw_timer_right_times.text = quest.get("trues")
                    vw_timer_wrong_times.text = quest.get("wrongs")
                    vw_all_timer_right_times.text = allRight.toString()
                    vw_all_timer_wrong_times.text = allWrong.toString()
                    vw_speak.setOnClickListener {
                        var word = vw_word.text.toString()
                        SpeakOut(word)
                    }
                    handler.postDelayed(Runnable {
                        if (speaker) SpeakOut(vw_word.text.toString())
                    }, 200)


                    trueAns = quest.get(quest!!.get("ansTrue")!!)
                    if (tim != null) {
                        tim!!.cancel()
                    }
                    tim = Timer(timeForEachChallenge).start()
                    vw_timer_pause_btn.setOnClickListener {
                        TimeControl()
                    }

                } catch (e: Exception) {
                    //                Log.d("Err",e.message)
                }

            } else {
                Log.d("Err-RESQUEST", resQuest)
                if (tim != null) {
                    tim!!.cancel()
                }
                temp = SPref(applicationContext, "runQuest")!!.edit().putInt("currentQuest", 1).commit()
                finish()
            }
            currentQuest++
        } else {
            if (tim != null) {
                tim!!.cancel()
            }
            var time: Long = (Calendar.getInstance().timeInMillis / 1000).toString().toLong()
            var intent = Intent(this, QuestFinish::class.java)
            intent.putExtra("hide_RWS", true)
            startActivity(intent)
            finish()
        }
    }

    fun CheckAnswer(ans: Int) {
        if (locked) {
            sToast(applicationContext, getString(R.string.setAnswerWait), true)
        } else {
            locked = true
            var userAnswer: String? = null

            when (ans) {
                1 -> userAnswer = vw_ans_st.text.toString()
                2 -> userAnswer = vw_ans_nd.text.toString()
                3 -> userAnswer = vw_ans_rd.text.toString()
                4 -> userAnswer = vw_ans_th.text.toString()
            }

            if (userAnswer == trueAns && ans != 0) {
                //send true ans to server
//                Toast.makeText(applicationContext, "True Answer", Toast.LENGTH_SHORT).show()
                SetAnswer()
            } else {

                WordShow(vw_word.text.toString(), "", trueAns.toString(), "")
            }
            locked = false
        }

    }

    fun TimeControl() {
        if (timeRun) {
            vw_timer_pause_btn.setImageDrawable(resources.getDrawable(R.mipmap.timer_bg))

            tim = Timer(remainTime.toInt()).start()

            Toast.makeText(applicationContext, "Started", Toast.LENGTH_SHORT).show()
            rv_questLoader.visibility = View.VISIBLE
            timeRun = false
        } else {
            vw_timer_pause_btn.setImageDrawable(resources.getDrawable(R.mipmap.timer_bg))
            rv_questLoader.visibility = View.GONE
            var remTime = vw_timer_time.text.toString()
            var min = remTime.substring(0, 2).toInt()
            var sec = remTime.substring(3, 5).toInt()
            var r: Int = min * 60 + sec
            remainTime = r
            tim?.cancel()
            Toast.makeText(applicationContext, "Paused", Toast.LENGTH_SHORT).show()
            timeRun = true
        }

    }

    fun WordShow(w: String, sound: String, verb: String, noun: String) {
        val wordToCheck = w.toLowerCase()
        if (tim != null) {
            tim!!.cancel()
        }
        val wordDesc = AlertDialog.Builder(this).create()
        val dialogView = layoutInflater.inflate(R.layout.word_desc, null)
        wordDesc.setView(dialogView)
        val word = dialogView.findViewById<View>(R.id.wd_word) as TextView
        word.setTypeface(iransans)
        val speak = dialogView.findViewById<View>(R.id.wd_speak) as ImageView
        val verbDesc = dialogView.findViewById<View>(R.id.wd_verb_dec) as TextView
        verbDesc.setTypeface(iransans)
        val nounDesc = dialogView.findViewById<View>(R.id.wd_noun_dec) as TextView
        val nextBtn = dialogView.findViewById<Button>(R.id.wd_next_question)
        var moreInfo = dialogView.findViewById<Button>(R.id.wd_more_info)
        val moreList = dialogView.findViewById<ListView>(R.id.wd_description)
        nextBtn.typeface = iransans
        moreInfo.typeface = iransans

        moreList.visibility = View.GONE


        moreInfo.setOnClickListener {

            var nwTrans = khttp.get(DESC_TRANS + wordToCheck)

            if (nwTrans.statusCode == 200) {
                var res = nwTrans.jsonArray
                var meaningList: Array<WordTransObject?> = arrayOfNulls(res.length())
                for (i in 0..res.length() - 1) {
                    var row = res.get(i) as JSONObject
                    meaningList.set(i, WordTransObject(
                            row.getString("type"),
                            row.getString("definition"),
                            row.getString("example")
                    )
                    )
                }
                Log.d("RESULT", res.toString())
                var adapter = listObjAdapter(applicationContext, meaningList)

                moreList.adapter = adapter
                moreList.deferNotifyDataSetChanged()
                moreList.visibility = View.VISIBLE
            }
        }
        nextBtn.setOnClickListener {
            wordDesc.dismiss()
        }
        nounDesc.setTypeface(iransans)
        word.setText(w)
        speak.setOnClickListener {
            Toast.makeText(applicationContext, "Play Pronunciation Of $sound", Toast.LENGTH_SHORT).show()
        }
        verbDesc.setText(verb)
        nounDesc.setText(noun)

        speak.setOnClickListener { SpeakOut(w) }


        wordDesc.setOnDismissListener {
            wordDesc.cancel()
            SetAnswer(false)
        }
        wordDesc.show()

    }

    fun Timer(time: Int): CountDownTimer {
        var input = time.toLong() * 1000

        var re = object : CountDownTimer(input, 1000) { // adjust the milli seconds here

            override fun onTick(millisUntilFinished: Long) {
                var m = ((millisUntilFinished / 1000) / 60).toInt()
                var s = ((millisUntilFinished / 1000) - (m * 60)).toInt()
                if (m < 10 && s < 10) {
                    vw_timer_time.text = "0" + m + ":0" + s
                } else if (s < 10) {
                    vw_timer_time.text = "" + m + ":0" + s
                } else if (m < 10) {
                    vw_timer_time.text = "0" + m + ":" + s
                } else {
                    vw_timer_time.text = "" + m + ":" + s
                }
            }

            override fun onFinish() {
                CheckAnswer(0)
                vw_timer_time.setText("00:00")
            }

        }

        return re
    }

    fun GetQuestion() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        var userid = SPref(applicationContext, "userCreds")!!.getString("userId", null)
        var level = SPref(applicationContext, "level")!!.getInt("level", 0)
        var countQuestion = SPref(applicationContext, "level")!!.getInt("numChallenge", 20)

//        var noQuest =arrayOf(mapOf<String,String>("q" to "elect","ans1" to "برتر انگاشته" ,"ans2" to "ابکی" ,"ans3" to "اب" ,"ans4" to "آفتاب","ansTrue" to "ans1"))
        var strLevel = SRV_LEVEL_DETECTOR(level)
        if (strLevel != 0.toString()) {

            var reviews = khttp.post(SERVER_ADDRESS, data = mapOf<String, Any>("m" to "review", "phone" to userid, "level" to SRV_LEVEL_DETECTOR(level).toString(), "count" to countQuestion))

            if (reviews.statusCode == 200) {
                var result = reviews.jsonObject
                Log.d("mk", result.toString())
                Log.d("RESU", result.toString())
                if (result.getBoolean("result") && result.getInt("count_items") > 0) {
                    try {

                        var reviewables = result.getJSONArray("items")

                        var count = reviewables.length()
                        Log.d("JSON", "3-->" + count)
                        var LoadedReviews = arrayOfNulls<MutableMap<String, String>>(count)
                        var i = 0
                        while (i < count) {
                            var jes = reviewables.getJSONObject(i)


                            Log.d("JSON", "4--->" + jes.getString("question").toString())
                            var listing = RanArray()

                            LoadedReviews[i] = mutableMapOf<String, String>(
                                    "res" to (if (jes.getString("quest_id") != jes.getString("question")) "true" else "false"),
                                    "qid" to jes.getString("quest_id"),
                                    "q" to jes.getString("question"),
                                    listing[0] to jes.getString("trueanswer"),
                                    listing[1] to jes.getString("wrong1"),
                                    listing[2] to jes.getString("wrong2"),
                                    listing[3] to jes.getString("wrong3"),
                                    "ansTrue" to listing[0],
                                    "trues" to jes.getString("count_true"),
                                    "wrongs" to jes.getString("count_wrong")
                            )
                            i++
                        }
                        LoadedReview = LoadedReviews
                    } catch (E: JSONException) {

                    }
                } else {
                    End()
                }

            } else {
                Log.d("Server-Error ", reviews.statusCode.toString())
            }
        }
    }

    fun Helper(Index: Int): MutableMap<String, String>? {
        return LoadedReview?.get(Index)
    }

    fun SetAnswer(Answer: Boolean = true) {

        var con = isNetworkAvailable(this)
        if (con) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            var userId = SPref(applicationContext, "userCreds")!!.getString("userId", 0.toString())
            var level = SPref(applicationContext, "level")!!.getInt("level", 0)
            var qid = SPref(applicationContext, "runReview")!!.getInt("qid", 0)
            var ansStat = if (Answer) 1 else 2
            if (userId != 0.toString() && level != 0 && qid != 0) {

                var setAnswer = khttp.post(SERVER_ADDRESS, data = mapOf<String, String>("m" to "setanswer", "phone" to userId.toString(), "level" to SRV_LEVEL_DETECTOR(level).toString(), "qid" to qid.toString(), "ans" to ansStat.toString()))
                Log.d("REQ_ANSWER", setAnswer.text)

                if (setAnswer.statusCode == 200) {
                    try {
                        var jes = setAnswer.jsonObject
                        if (jes.getBoolean("result")) {
                            if (Answer) allRight++ else allWrong++
                            QuestLoader()
                        } else {
//                            sToast(applicationContext, "SENT $userId $qid")
                            SetAnswer(Answer)
                        }

                    } catch (e: Exception) {
                        Log.d("SetAns", e.message.toString())
                    }

                }
            }
        } else {
            var noCon = Intent(this, NoConnection::class.java)
            startActivity(noCon)
            finish()
        }

    }

    fun End() {
//        sToast(applicationContext,applicationContext.resources.getString(R.string.no_reviews))
        sToast(applicationContext, applicationContext.resources.getString(R.string.noReviewAvalable))
        finish()

    }

    inner class WordTransObject(type: String, meaning: String, eg: String) {
        var T = type

        var D = meaning
        var E = eg
    }

    inner class listObjAdapter(private val context: Context, private val dataSource: Array<WordTransObject?>) : BaseAdapter() {
        private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            var listLay = inflater.inflate(R.layout.translation_list_item, null)
            var type = listLay.findViewById<TextView>(R.id.trlist_type)
            var mean = listLay.findViewById<TextView>(R.id.trlist_mean)
            var eg = listLay.findViewById<TextView>(R.id.trlist_eg)

            var item = getItem(p0) as WordTransObject
            type.text = android.text.Html.fromHtml(item.T)
            mean.text = android.text.Html.fromHtml(item.D)
            eg.text = android.text.Html.fromHtml(item.E)
            return listLay
        }

        override fun getItem(p0: Int): WordTransObject? {
            return dataSource!!.get(p0)
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {
            return dataSource.size
        }

    }
}
