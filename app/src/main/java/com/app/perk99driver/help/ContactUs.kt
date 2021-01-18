package com.app.perk99driver.help

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.app.perk99driver.R
import com.app.perk99driver.home.HomeActivity
import com.app.perk99driver.networking.Injector
import com.app.perk99driver.networking.InterfaceApi
import com.app.perk99driver.ui.LoginActivity
import com.app.perk99driver.utils.CommonMethod
import com.app.perk99driver.utils.GeneralResponse
import com.app.perk99driver.utils.MyApplication
import com.app.perk99driver.utils.SharedPrefUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.contact_us.*
import kotlinx.android.synthetic.main.reset_password.btSubmitReset
import kotlinx.android.synthetic.main.reset_password.rlSubmitReset
import java.util.HashMap

class ContactUs : AppCompatActivity(), View.OnClickListener
{
    var disposable: Disposable? = null
    var api: InterfaceApi? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contact_us)



        api = Injector.provideApi()
        rlSubmitReset.setOnClickListener(this)
        btSubmitReset.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.rlSubmitReset -> {
                if (validateCredentials())
                {
                    if (!MyApplication.hasNetwork())
                    {
                        CommonMethod.showToast(getString(R.string.no_internet))
                    } else
                    {
                        callContact()
                    }
                }
            }

            R.id.btSubmitReset ->
            {
                Log.e("SUbmitButton ","inside")
                if (validateCredentials())
                {
                    if (!MyApplication.hasNetwork())
                    {
                        CommonMethod.showToast(getString(R.string.no_internet))
                    }
                    else
                    {
                        callContact()
                    }
                }
            }
        }
    }


    fun validateCredentials(): Boolean
    {
        if (etMessage!!.text.isEmpty())
        {
            CommonMethod.showToast(this, "Enter your concern")
            return false
        }
        return true
    }

    var email:String=""

    private fun callContact()
    {
        CommonMethod.showProgress(this)
        val helper = SharedPrefUtil.getInstance()

        val hashMap: HashMap<String, String> = HashMap<String, String>()
        hashMap.put("message", etMessage!!.text.toString())
        hashMap.put("provider", "drivers")


        Log.e("sendContact ",hashMap.toString())

        disposable = api!!.contactUs(hashMap)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    CommonMethod.hideProgress()
                    try
                    {
                        val generalResponse = GeneralResponse(result)
                        val jsonObject = generalResponse.getResponse_object();
                        Log.v("jsonObjectPassword ", "" + jsonObject)

                        if (jsonObject.getString("status").equals("success"))
                        {
                            CommonMethod.showToast("Message sent successfully")
                            var intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                        }
                        else
                        {
                            CommonMethod.showToast(jsonObject.getString("message"))
                        }
                    }
                    catch (ex: java.lang.Exception)
                    {
                        CommonMethod.hideProgress()
                    }

                },

                {
                        error ->

                    CommonMethod.hideProgress()
                    CommonMethod.showToast(getString(R.string.server_error))


                }
            )
    }


}

