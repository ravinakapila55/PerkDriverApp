package com.app.perk99driver.help

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.app.perk99driver.R
import kotlinx.android.synthetic.main.activity_help.*
import kotlinx.android.synthetic.main.header_small.*

class HelpActivity : AppCompatActivity(), View.OnClickListener
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        txt_header.setText("Help")
        img_back_btn.setOnClickListener(this)
        btContact!!.setOnClickListener(this)
    }

    override fun onClick(v: View?)
    {
        when (v!!.id)
        {
            R.id.img_back_btn ->
            {
                Log.e("backClick ","backImage")
                finish()
            }

            R.id.btContact ->
            {
                var intent = Intent(this, ContactUs::class.java)
                startActivity(intent)
            }
        }
    }
}
