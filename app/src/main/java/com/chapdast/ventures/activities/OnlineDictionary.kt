package com.chapdast.ventures.activities

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.StrictMode
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.chapdast.ventures.*
import com.chapdast.ventures.Configs.*
import kotlinx.android.synthetic.main.activity_online_dictionary.*
import org.json.JSONObject
import java.util.*

@Suppress("DEPRECATION")
class OnlineDictionary : AppCompatActivity() {
var iransans:Typeface?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online_dictionary)

        //check Internet Connection
        if(!isNetworkAvailable(this)){
            var noCon = Intent(this, NoConnection::class.java)
            startActivity(noCon)
            finish()
        }
        else {



            var assetManager = applicationContext.assets;
            iransans = Typeface.createFromAsset(assetManager, String.format(Locale.ENGLISH, "fonts/%s", "iransans.ttf"))
            od_fa_to_en.typeface = iransans
            od_en_to_fa.typeface = iransans
            od_en.typeface = iransans

            od_input.setTypeface(iransans)
            od_trans_view.setTypeface(iransans)
            od_layout.visibility = View.GONE
            od_trans_btn.setTypeface(iransans)
            od_trans_btn.setOnClickListener {
                loadTrans()
            }

            od_input.setOnKeyListener ( View.OnKeyListener { view , keyCode, keyEvent ->

                if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.ACTION_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
                    loadTrans()
                    return@OnKeyListener true
                }
                false
            })
        }
    }

    fun loadTrans(){
        var input = od_input.text.toString().toLowerCase().trim()
        if (input.length != 0 ) {
            od_trans_sec.visibility=View.GONE
            od_progress.visibility = View.VISIBLE
            od_layout.visibility = View.VISIBLE
            od_trans_view.visibility= View.GONE
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            var direction = if (od_fa_to_en.isChecked) "fa-en" else if(od_en_to_fa.isChecked) "en-fa" else "en-en"

            if (direction == "fa-en") {
                od_trans_view.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
            } else {
                od_trans_view.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
            }
            if(direction != "en-en") {
                var trans = khttp.post(TRANS_SERVER_ADDRESS, data = mapOf<String, Any>("key" to TRANS_KEY, "text" to input, "lang" to direction, "format" to "plain"))
                if (trans.statusCode == 200) {
                    var tr = trans.jsonObject
                    var text = tr.getString("text")
                    od_trans_view.setText(text.substring(2, (text.length - 2)))
                    od_progress.visibility = View.GONE
                    od_trans_view.visibility = View.VISIBLE


                }
            } else {
                od_trans_sec.visibility=View.VISIBLE
                od_trans_view.visibility = View.GONE
                var nwTrans = khttp.get(DESC_TRANS + input)

                if(nwTrans.statusCode == 200 ){
                    var res = nwTrans.jsonArray
                    var meaningList : Array<WordTransObject?> = arrayOfNulls(res.length())
                    for (i in 0..res.length()-1) {
                        var row = res.get(i) as JSONObject
                        meaningList.set(i,WordTransObject(
                                row.getString("type"),
                                row.getString("definition"),
                                row.getString("example")
                        )
                        )
                    }

                    var adapter = listObjAdapter(this,meaningList)

                    od_trans_sec.adapter = adapter
                    od_trans_sec.deferNotifyDataSetChanged()
                }
                od_progress.visibility = View.GONE
            }

        } else {
            sToast(applicationContext, applicationContext.resources.getString(R.string.translateInputError))
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
    }
    inner class WordTransObject(type:String, meaning:String, eg:String){
        var T = type

        var D = meaning
        var E = eg
    }
    inner class listObjAdapter(private val context: Context, private val dataSource: Array<WordTransObject?>):BaseAdapter(){
        private val inflater: LayoutInflater
                = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            var listLay = inflater.inflate(R.layout.translation_list_item,p2,false)
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



/*
https://translate.yandex.net/api/v1.5/tr.json/translate ?
key=<API key>
 & text=<text to translate>
 & lang=<translation direction>
 & [format=<text format>]
 & [options=<translation options>]
 & [callback=<name of the callback function>]
 */