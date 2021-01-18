package com.app.perk99driver

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.app.perk99driver.home.HomeActivity
import com.app.perk99driver.ui.LoginActivity
import com.app.perk99driver.utils.SharedPrefUtil

class SpalshActivity : AppCompatActivity() {

//    Perk API
//    AIzaSyCciQfQ_9_TNZQrV5nVGM8xMKZfTNSZZKg

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_spalsh)
        val helper=SharedPrefUtil.getInstance()

        val handler = Handler()
        // generateKeyHash()
        val runnable = Runnable {

            var intent = chooseActivity(helper.getBoolean(SharedPrefUtil.LOGIN))
            startActivity(intent)
            finish()


        }
        handler.postDelayed(runnable, 3000) }

    private fun chooseActivity(boolean: Boolean): Intent? {

        return when (boolean) {

            true ->  Intent(applicationContext, HomeActivity::class.java)

            false -> {
                Intent(applicationContext, LoginActivity::class.java)

            }
        }
    }
}

