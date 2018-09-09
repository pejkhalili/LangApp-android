package com.chapdast.ventures.activities

import android.content.Intent
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.chapdast.ventures.Configs.isNetworkAvailable
import com.chapdast.ventures.R
import kotlinx.android.synthetic.main.activity_media_selection.*
import java.util.*

class MediaSelection : AppCompatActivity() {
    lateinit var iransans:Typeface
    lateinit var iranBlack:Typeface
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_selection)
        var assetManager = applicationContext.assets
        iransans = Typeface.createFromAsset(assetManager, String.format(Locale.ENGLISH, "fonts/%s", "iransans.ttf"))
        iranBlack = Typeface.createFromAsset(assetManager, String.format(Locale.ENGLISH, "fonts/%s", "iranblack.ttf"))
        if (!isNetworkAvailable(applicationContext)) {
            var noCon = Intent(this, NoConnection::class.java)
            startActivity(noCon)
            finish()
        }
        ms_message.typeface = iransans


        var intent = Intent(applicationContext,MediaLoader::class.java)
        ms_back.setOnClickListener {
            finish()
        }

        ms_pod_key.setOnClickListener {
            intent.putExtra("type",2)
            startActivity(intent)
        }

        ms_vid_key.setOnClickListener {
            intent.putExtra("type",1)
            startActivity(intent)
        }

        ms_all.setOnClickListener {
            intent.putExtra("type",0)
            startActivity(intent)
        }

    }
}
