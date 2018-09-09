package com.chapdast.ventures.activities

import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.chapdast.ventures.R
import kotlinx.android.synthetic.main.activity_eula_.*
import java.util.*

class EULA_Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eula_)
        var assetManager = applicationContext.assets
        var iransans = Typeface.createFromAsset(assetManager, String.format(Locale.ENGLISH, "fonts/%s", "iransans.ttf"))
        var iranBlack = Typeface.createFromAsset(assetManager, String.format(Locale.ENGLISH, "fonts/%s", "iranblack.ttf"))

        eula_back.setOnClickListener { finish() }
        eula_back.typeface = iranBlack
        eula_text.textDirection = TextView.TEXT_DIRECTION_ANY_RTL
        eula_text.typeface = iransans
        if(intent!=null && intent.extras!=null){
            var type = intent.extras.getString("type")
            if(type.equals("eula")){
                eula_text.text = applicationContext.resources.getString(R.string.EULA_TEXT).trim()
            }else{
                eula_text.text = applicationContext.resources.getString(R.string.ABOUT_TEXT).trim()
            }
        }

    }
}
