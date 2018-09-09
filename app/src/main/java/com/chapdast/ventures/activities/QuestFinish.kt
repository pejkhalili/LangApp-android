package com.chapdast.ventures.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle

import android.support.v7.app.AppCompatActivity
import android.view.View
import com.chapdast.ventures.R
import com.chapdast.ventures.Configs.SPref

import kotlinx.android.synthetic.main.activity_quest_finish.*
import java.util.*

class QuestFinish : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quest_finish)
        var assetManager = applicationContext.assets
        var iransans = Typeface.createFromAsset(assetManager, String.format(Locale.ENGLISH, "fonts/%s", "iransans.ttf"))
        var iranBlack = Typeface.createFromAsset(assetManager, String.format(Locale.ENGLISH, "fonts/%s", "iranblack.ttf"))
        //add date and time to keep user out of quest
        fq_text.typeface = iranBlack
        fq_main_menu.typeface = iransans
        fq_reviews.typeface = iranBlack
        SPref(applicationContext, "quest_stat")!!.edit().clear().commit()
        if(intent.extras!=null){
            if(intent.extras.getBoolean("hide_RWS")) fq_reviews.visibility = View.INVISIBLE
        }
        fq_main_menu.setOnClickListener{
            var intent = Intent(this, Hub::class.java)
            startActivity(intent)
            finish()
        }
        fq_reviews.setOnClickListener{
            var intent = Intent(this, Review::class.java)
            startActivity(intent)
            finish()
        }


    }

}
