package com.app.perks99.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.app.perk99driver.R
import com.app.perk99driver.networking.Injector
import com.app.perk99driver.networking.InterfaceApi
import com.app.perk99driver.utils.CommonMethod
import com.app.perk99driver.utils.GeneralResponse
import com.app.perk99driver.utils.MyApplication
import com.app.perk99driver.utils.SharedPrefUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.edtEmail
import kotlinx.android.synthetic.main.forget_password_activity.*
import java.util.HashMap

class ForgetPass : AppCompatActivity(), View.OnClickListener
{
    var disposable: Disposable? = null
    var api: InterfaceApi? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forget_password_activity)
        api = Injector.provideApi()

        rlForgetButtons.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)
    }

    override fun onClick(v: View?)
    {
        when (v!!.id)
        {
            R.id.rlForgetButtons ->
            {
                Log.e("InsideCLick ","inside")
                if (validateCredentials())
                {
                    if (!MyApplication.hasNetwork())
                    {
                        CommonMethod.showToast(getString(R.string.no_internet))
                    }
                    else
                    {
                        callForget()
                    }
                }
            }
            R.id.btnSubmit ->
            {
                Log.e("InsideCLickbtnSubmit ","inside")
                if (validateCredentials())
                {
                    if (!MyApplication.hasNetwork())
                    {
                        CommonMethod.showToast(getString(R.string.no_internet))
                    }
                    else
                    {
                        callForget()
                    }
                }
            }
        }
    }

    fun validateCredentials(): Boolean
    {
        if (edtEmail!!.text.isEmpty())
        {
            CommonMethod.showToast(this, getString(R.string.please_enter_email_address))
            return false
        }
        else if (!CommonMethod.isValidEmail(edtEmail.text.toString()))
        {
            CommonMethod.showToast(this, getString(R.string.enter_valid_email))
            return false
        }
        return true
    }

    private fun callForget()
    {
        CommonMethod.showProgress(this)
        val helper = SharedPrefUtil.getInstance()

        val hashMap: HashMap<String, String> = HashMap<String, String>()
        hashMap.put("email", edtEmail!!.text.toString())
        Log.e("ForgetPasswordParams ",hashMap.toString())

        disposable = api!!.forgetPassword(hashMap)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    CommonMethod.hideProgress()
                    try {
                        val generalResponse = GeneralResponse(result)
                        val jsonObject = generalResponse.getResponse_object();
                        Log.v("jsonObjectPassword ", "" + jsonObject)

                        if (jsonObject.getString("success").equals("true")) {
                            CommonMethod.showToast("OTP Sent Successfully on registered email address")
                           /* val jsonObjectData = jsonObject.getJSONObject("data")

                            helper.saveBoolean(SharedPrefUtil.LOGIN, true)
                            helper.saveString(SharedPrefUtil.AUTH_TOKEN, jsonObjectData.getString("token"))*/

                            var intent = Intent(applicationContext, OtpScreen::class.java)
                            intent.putExtra("email",edtEmail.text.toString().trim())
                            startActivity(intent)
                            finishAffinity()
                        } else {
                            CommonMethod.showToast(jsonObject.getString("message"))
                        }

                    } catch (ex: java.lang.Exception) {
                        CommonMethod.hideProgress()
                    }

                },
                {
                    error ->
                    CommonMethod.hideProgress()
                    CommonMethod.showToast(getString(R.string.server_error))
                })
    }

}