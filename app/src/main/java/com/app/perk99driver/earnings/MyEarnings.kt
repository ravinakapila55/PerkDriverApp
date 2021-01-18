package com.app.perk99driver.earnings

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.app.perk99driver.R
import com.app.perk99driver.networking.Injector
import com.app.perk99driver.networking.InterfaceApi
import com.app.perk99driver.utils.CommonMethod
import com.app.perk99driver.utils.GeneralResponse
import com.app.perk99driver.utils.SharedPrefUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_earning.*
import kotlinx.android.synthetic.main.header_small.*

class MyEarnings : AppCompatActivity(), View.OnClickListener
{
    var api: InterfaceApi? = null
    var disposable: Disposable? = null


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_earning)
        api = Injector.provideApi()
        helper = SharedPrefUtil.getInstance()
        txt_header.setText("My Earnings")
        img_back_btn.setOnClickListener(this)
        rlButtons.setOnClickListener(this)
    }

    private fun sendTransferRequest()
    {
        //     Log.e("sassa","lat "+mSourceLat +"ss "+mSourceLon)
        Log.e("amount ",tvFund.text.toString().trim())
        disposable = api!!.sendTransferRequest(etAmount.text.toString().trim(),"drivers")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    CommonMethod.hideProgress()
                    try {
                        val generalResponse = GeneralResponse(result)
                        val jsonObject = generalResponse.getResponse_object();
                        Log.v("DropOffedResponse ", "" + jsonObject)
                        if (jsonObject.getString("status").equals("success"))
                        {
                            CommonMethod.showToast(jsonObject.getString("message"));
                            showEarning()

                        } else {
                            CommonMethod.showToast("Server Error");
                        }

                    } catch (ex: java.lang.Exception)
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

    var availableBalnace=0

    override fun onClick(v: View?)
    {
        when (v!!.id)
        {
            R.id.img_back_btn ->
            {
                Log.e("backClick ","backImage")
                finish()
            }

            R.id.rlButtons ->
            {
                if (etAmount!!.text.toString().trim().isEmpty())
                {
                    CommonMethod.showToast("Please enter some amount to transfer")
                }
                else
                {
                    var value = 0
                    var amount:String=""
                    amount=etAmount!!.text.toString().trim()

                    value=amount.toInt()
                    Log.e("Value ",value.toString())
                    if(value>availableBalnace)
                    {
                        CommonMethod.showToast("Entered Amount should n't be more than available amount")
                    }
                    else
                    {
                        sendTransferRequest()
                    }
                }
            }

        }
    }

    lateinit var helper:SharedPrefUtil

    var click:String="0"


    override fun onResume() {
        super.onResume()
        showEarning()
    }

    private fun showEarning()
    {
        CommonMethod.showProgress(this)
        disposable = api!!.getEarning("drivers").subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    CommonMethod.hideProgress()
                    try {
                        val generalResponse = GeneralResponse(result)
                        val jsonObject = generalResponse.getResponse_object()
                        if (jsonObject.getString("status").equals("success")) {
                            val jsonObject1 = jsonObject.getJSONObject("data")

                            helper.saveString(SharedPrefUtil.EARNING, jsonObject1.getString("earning"))

                            tvFund.setText("$ "+ jsonObject1.getString("earning"))


                            if (jsonObject1.getString("earning").equals("0",ignoreCase = true))
                            {
                                click="0"
                                availableBalnace=0
                                rlButtons.visibility=View.VISIBLE
                            }
                            else{
                                click="1"

                                availableBalnace=jsonObject1.getString("earning").toInt()

                                rlButtons.visibility=View.VISIBLE

                            }

                        } else {
                            CommonMethod.showToast(jsonObject.getString("message"))
                        }

                    } catch (ex: java.lang.Exception)
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
