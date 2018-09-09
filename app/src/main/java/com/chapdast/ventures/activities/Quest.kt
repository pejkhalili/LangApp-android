package com.chapdast.ventures.activities


import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.StrictMode
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.chapdast.ventures.*
import com.chapdast.ventures.Configs.*
import kotlinx.android.synthetic.main.activity_quest_new.*
import kotlinx.android.synthetic.main.new_quest_loader.*
import kotlinx.android.synthetic.main.new_timer.*
import org.json.JSONObject
import java.util.*


class Quest : AppCompatActivity(), OnInitListener {
    var locked = false

    var rightAnswers = 0
    var wrongAnswers = 0

    var timeRun = false
    var remainTime: Int = 0
    var tim: CountDownTimer? = null
    var currentQuest: Int? = null
    var ChallengeCount: Int = 20
    var timeForEachChallenge = 20
    var trueAns: String? = null
    var qid = 0
    var iransans: Typeface? = null
    var bebas: Typeface? = null
    var temp: Any = ""
    var tts: TextToSpeech? = null
    var LoadedReview: Array<MutableMap<String, String>?>? = null
    var speaker:Boolean = true
    var handler = Handler()
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            var result = tts!!.setLanguage(Locale.ENGLISH)
            tts!!.setPitch(1.1f)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quest_new)
        speaker = SPref(applicationContext,"setting")!!.getBoolean("speaker",true)
        //check Internet Connection
        if (!isNetworkAvailable(this)) {
            var noCon = Intent(this, NoConnection::class.java)
            startActivity(noCon)
            finish()
        } else {


            var assetManager = applicationContext.assets;
            iransans = Typeface.createFromAsset(assetManager, String.format(Locale.ENGLISH, "fonts/%s", "iransans.ttf"))
            bebas = Typeface.createFromAsset(assetManager, String.format(Locale.ENGLISH, "fonts/%s", "bebas.otf"))

            wrongAnswers = SPref(applicationContext, "quest_stat")!!.getInt("WRONG_ANS", 0)
            rightAnswers = SPref(applicationContext, "quest_stat")!!.getInt("RIGHT_ANS", 0)

            timeForEachChallenge = SPref(this, "level")!!.getInt("timeOnChalleng", 20).toInt()
            ChallengeCount = SPref(this, "level")!!.getInt("numChallenge", 20).toInt()

            tts = TextToSpeech(this, this)

            nq_first.setTypeface(iransans)
            nq_sec.setTypeface(iransans)
            nq_third.setTypeface(iransans)
            nq_fourth.setTypeface(iransans)
            nq_unknown.setTypeface(iransans)
            nq_time.setTypeface(bebas)
            nq_time.setTextSize(35F)

            QuestLoader()

            nq_side.setOnClickListener {
                tim?.cancel()
                finish()
            }
            nq_unknown.setOnClickListener { CheckAnswer(0) }

            nq_first.setOnClickListener { CheckAnswer(1) }
            nq_sec.setOnClickListener { CheckAnswer(2) }
            nq_third.setOnClickListener { CheckAnswer(3) }
            nq_fourth.setOnClickListener { CheckAnswer(4) }

        }
    }

    fun QuestLoader() {
        var quest = GetQuestion()
        Log.d("RESULT", quest.toString())

        currentQuest = SPref(applicationContext, "runQuest")!!.getInt("currentQuest", 1)

        var resQuest = quest.get("res").toString()

        if (resQuest == "true") {
            if (currentQuest!! <= ChallengeCount) {
                try {
                    nq_counter.text = currentQuest!!.toString() + "/" + ChallengeCount.toString()
                    var questInTitle = quest.get("q").toString().toUpperCase()
                    nq_quest.text = "#" + currentQuest.toString() + " " + questInTitle

                    currentQuest = currentQuest!! + 1

                    temp = SPref(applicationContext, "runQuest")!!.edit().putInt("qid", quest.get("qid")!!.toInt()).commit()
                    temp = SPref(applicationContext, "runQuest")!!.edit().putInt("currentQuest", currentQuest!!).commit()

                    nq_question.text = quest.get("q").toString().toUpperCase()
                    nq_first.text = quest.get("ans1")
                    nq_sec.text = quest.get("ans2")
                    nq_third.text = quest.get("ans3")
                    nq_fourth.text = quest.get("ans4")
                    nq_trues.text = rightAnswers.toString()//quest.get("trues")
                    nq_wrong.text = wrongAnswers.toString() // quest.get("wrongs")

                    nq_speak.setOnClickListener {
                        SpeakOut(nq_question.text.toString())
                    }
                    handler.postDelayed(Runnable {
                        if(speaker) SpeakOut(nq_question.text.toString())
                    },200)


                    trueAns = quest.get(quest.get("ansTrue")!!)
                    if (tim != null) {
                        tim!!.cancel()
                    }
                    tim = Timer(timeForEachChallenge).start()
                    nq_pus.setOnClickListener {
                        TimeControl()
                    }

                } catch (e: Exception) {
                    //                Log.d("Err",e.message)
                }

            } else {
                if (tim != null) {
                    tim!!.cancel()
                }
                var time: Long = (Calendar.getInstance().timeInMillis / 1000).toString().toLong()
                temp = SPref(applicationContext, "runQuest")!!.edit().putInt("currentQuest", 1).commit()
                temp = SPref(applicationContext, "quest")!!.edit().putString("lastFinish", time.toString()).commit()
                var intent = Intent(this, QuestFinish::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            Log.d("Err-RESQUEST", resQuest)
            sToast(this, applicationContext.resources.getString(R.string.noQuestAvalable))
            if (tim != null) {
                tim!!.cancel()
            }
//                temp = SPref(applicationContext,"runQuest")!!.edit().putInt("currentQuest",1).commit()
            temp = SPref(applicationContext, "quest")!!.edit().putString("lastFinish", 0.toString()).commit()
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
                1 -> userAnswer = nq_first.text.toString()
                2 -> userAnswer = nq_sec.text.toString()
                3 -> userAnswer = nq_third.text.toString()
                4 -> userAnswer = nq_fourth.text.toString()
            }

            if (userAnswer == trueAns && ans != 0) {
                //send true ans to server
//                Toast.makeText(applicationContext, "True Answer", Toast.LENGTH_SHORT).show()
                SetAnswer()
            } else {

                WordShow(nq_question.text.toString(), "", trueAns.toString(), "")
            }

            currentQuest = qid
            qid++
            locked = false
        }


    }

    fun TimeControl() {
        if (timeRun) {
            nq_pus.setImageDrawable(resources.getDrawable(R.mipmap.timer_bg))

            tim = Timer(remainTime.toInt()).start()

            sToast(applicationContext, applicationContext.resources.getString(R.string.started))
            nq_q_load.visibility = View.VISIBLE
            timeRun = false
        } else {
            nq_pus.setImageDrawable(resources.getDrawable(R.mipmap.timer_bg))
            nq_q_load.visibility = View.GONE
            var remTime = nq_time.text.toString()
            var min = remTime.substring(0, 2).toInt()
            var sec = remTime.substring(3, 5).toInt()
            var r: Int = min * 60 + sec
            remainTime = r
            tim?.cancel()
            sToast(applicationContext, applicationContext.resources.getString(R.string.stopped))
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
                Log.d("RESULT", wordToCheck + res.toString())
                var adapter = listObjAdapter(applicationContext, meaningList)

                moreList.adapter = adapter
                moreList.deferNotifyDataSetChanged()
                moreList.visibility = View.VISIBLE
            } else {
                Log.d("RESULT", nwTrans.text)
            }
        }

        nextBtn.setOnClickListener {
            wordDesc.dismiss()
        }
        nounDesc.setTypeface(iransans)
        word.setText(w)
        speak.setOnClickListener {
            SpeakOut(sound)
//                sToast(applicationContext, "Play Pronunciation Of $sound").show()
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
                    nq_time.text = "0" + m + ":0" + s
                } else if (s < 10) {
                    nq_time.text = "" + m + ":0" + s
                } else if (m < 10) {
                    nq_time.text = "0" + m + ":" + s
                } else {
                    nq_time.text = "" + m + ":" + s
                }
            }

            override fun onFinish() {
                CheckAnswer(0)
                nq_time.setText("00:00")
            }

        }

        return re
    }

    fun GetQuestion(): Map<String, String> {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        var userid = SPref(applicationContext, "userCreds")!!.getString("userId", null)
        var level = SPref(applicationContext, "level")!!.getInt("level", 0)

        var noQuest = mapOf<String, String>("q" to "elect", "ans1" to "برتر انگاشته", "ans2" to "ابکی", "ans3" to "اب", "ans4" to "آفتاب", "ansTrue" to "ans1")
        var strLevel = SRV_LEVEL_DETECTOR(level)
        Log.d("RESULT", mapOf("m" to "getQuestion", "phone" to userid, "level" to strLevel).toString())
        if (strLevel != 0.toString()) {
            var getQuest = khttp.post(SERVER_ADDRESS, data = mapOf("m" to "getQuestion", "phone" to userid, "level" to strLevel))
            Log.d("RESULT", getQuest.toString())
            if (getQuest.statusCode == 200) {
                var jes = getQuest.jsonObject
                Log.d("RESULT", jes.toString())
                if (jes.getBoolean("result")) {
                    var listing = RanArray()
                    return mapOf<String, String>(
                            "res" to (if (jes.getString("quest_id") != jes.getString("question")) "true" else "false"),
                            "qid" to jes.getString("quest_id"),
                            "q" to jes.getString("question"),
                            listing[0] to jes.getString("trueanswer"),
                            listing[1] to jes.getString("wrong1"),
                            listing[2] to jes.getString("wrong2"),
                            listing[3] to jes.getString("wrong3"),
                            "ansTrue" to listing[0],
                            "trues" to jes.getString("count_true"),
                            "wrongs" to jes.getString("count_wrongs")
                    )
                } else {
                    Log.d("REQ_RES", "ERROR IN SERVER DATA")
                }
            } else {
                Log.d("Server-Error ", getQuest.statusCode.toString())
            }
        }
        return noQuest!!
    }

    fun SetAnswer(Answer: Boolean = true) {

        if (Answer) {
            rightAnswers++
            SPref(applicationContext, "quest_stat")!!.edit().putInt("RIGHT_ANS", rightAnswers).apply()
        } else {
            wrongAnswers++
            SPref(applicationContext, "quest_stat")!!.edit().putInt("WRONG_ANS", wrongAnswers).apply()
        }
        var con = isNetworkAvailable(this)
        if (con) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            var userId = SPref(applicationContext, "userCreds")!!.getString("userId", 0.toString())
            var level = SPref(applicationContext, "level")!!.getInt("level", 0)
            var qid = SPref(applicationContext, "runQuest")!!.getInt("qid", 0)
            var ansStat = if (Answer) 1 else 2
            if (userId != 0.toString() && level != 0 && qid != 0) {

                var setAnswer = khttp.post(SERVER_ADDRESS, data = mapOf<String, String>("m" to "setanswer", "phone" to userId.toString(), "level" to SRV_LEVEL_DETECTOR(level).toString(), "qid" to qid.toString(), "ans" to ansStat.toString()))
                Log.d("REQ_ANSWER", setAnswer.text)

                if (setAnswer.statusCode == 200) {
                    try {
                        var jes = setAnswer.jsonObject
                        if (jes.getBoolean("result")) {

                            QuestLoader()
                        } else {
                            sToast(applicationContext, "SENT $userId $qid")
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
