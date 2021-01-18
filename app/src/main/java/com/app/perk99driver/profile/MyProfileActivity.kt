package com.app.perk99driver.profile


import android.os.Bundle
import android.util.Log
import android.view.View
import com.app.perk99driver.R
import com.app.perk99driver.networking.Injector
import com.app.perk99driver.networking.InterfaceApi
import com.app.perk99driver.utils.CommonMethod
import com.app.perk99driver.utils.GeneralResponse
import com.app.perk99driver.utils.MyApplication
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_my_profile.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import tv.gamesee.utils.helper.ImagePickerLatest
import java.io.File

class MyProfileActivity : ImagePickerLatest(), View.OnClickListener
{

    override fun selectedImage(imagePath: String?)
    {
        image=imagePath
        imageClicked="yes"
        if (imagePath != null)
            Picasso.get().load(File(imagePath)).into(img_profile_show)
        img_profile.visibility=View.GONE
    }

    private var imageClicked: String? = null
    var api: InterfaceApi? = null
    var disposable: Disposable? = null
    private var image: String? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        img_profile.setOnClickListener(this)
        img_back_btn.setOnClickListener(this)
        btnSave.setOnClickListener(this)
        api = Injector.provideApi()
        progress_bar.setVisibility(View.GONE)

        if (MyApplication.hasNetwork())
        getProfile()
        else
        CommonMethod.showToast(getString(R.string.no_internet))
    }

    override fun onClick(v: View?)
    {
        when (v!!.id)
        {
            R.id.img_back_btn -> {
                finish()
            }
            R.id.img_profile -> {
                checkPermissionCamera()
            }
            R.id.btnSave -> {
              updateProfileApi()
            }
        }

    }

    private fun updateProfileApi()
    {

        var newfile: File? = null
        var imageFileBody: MultipartBody.Part? = null

        if (image != null && image != "") {
            newfile = File(image)
        }
        if (newfile != null) {
            val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), newfile)
            imageFileBody = MultipartBody.Part.createFormData("image", newfile.name, requestBody)
        }

            val reqname: RequestBody = RequestBody.create(MediaType.parse("text/plain"), edt_fst_name.getText().toString())
            val reqlastName: RequestBody = RequestBody.create(MediaType.parse("text/plain"), edt_last_name.getText().toString())
            val reqCode: RequestBody = RequestBody.create(MediaType.parse("text/plain"), cc.selectedCountryCode)
            val reqPhone: RequestBody = RequestBody.create(MediaType.parse("text/plain"), edt_number.getText().toString())
            val reqAddress: RequestBody = RequestBody.create(MediaType.parse("text/plain"), edtAddress.getText().toString())
        val reqProvider: RequestBody = RequestBody.create(MediaType.parse("text/plain"), "drivers")
        disposable = api!!.updateProfile(reqname,reqlastName,reqCode,reqPhone,reqAddress,reqProvider,imageFileBody)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    CommonMethod.hideProgress()
                    val generalResponse = GeneralResponse(result)
                    try {
                        val jsonObject = generalResponse.getResponse_object();
                        Log.v("jsonObject", "" + jsonObject)
                        if (jsonObject.getString("status").equals("success")) {
                            CommonMethod.showToast(jsonObject.getString("message"))

                        } else {
                            Log.v("jsonObject", "" + jsonObject)
                            CommonMethod.showToast(jsonObject.getString("message"))


                        }

                    } catch (ex: java.lang.Exception) {
                        CommonMethod.hideProgress()

                    }

                },

                { error ->
                    Log.e("ERROR", error.message)
                    CommonMethod.hideProgress()
                    CommonMethod.showToast(error.message)
                }
            )


    }

    private fun getProfile()
    {
        CommonMethod.showProgress(this)

        disposable = api!!.getProfile("drivers")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    CommonMethod.hideProgress()
                    try {
                        val generalResponse = GeneralResponse(result)
                        val jsonObject = generalResponse.getResponse_object();
                        if (jsonObject.getString("status").equals("success")) {
                         //   {"success":true,"code":200,"data":{"first_name":"Sooraj Singh","last_name":null,
                            //   "country_code":null,"mobile":null,"email":"testeresfera@gmail.com","address":null,"image":{}}}

                            //   mListCategory = generalResponse.getDataAsListDecoded(jsonObject,"categories",Category)
                            val jsonObjectData = jsonObject.getJSONObject("result")

                            edt_fst_name.setText(jsonObjectData.getString("first_name"))

                            if(jsonObjectData.getString("last_name")!=null){
                                edt_last_name.setText(jsonObjectData.getString("last_name"))
                            }

                            if(jsonObjectData.getString("email")!=null){
                                edtEmail.setText(jsonObjectData.getString("email"))
                                edtEmail.isEnabled=false
                            }

                            if(jsonObjectData.getString("address")!=null){
                                edtAddress.setText(jsonObjectData.getString("address"))
                            }
                            if(jsonObjectData.getString("country_code")!=null){
                                cc.textView_selectedCountry.setText(jsonObjectData.getString("country_code"))
                            }
                            if(jsonObjectData.getString("mobile")!=null){
                                edt_number.setText(jsonObjectData.getString("mobile"))
                            }



                           val profilePic = jsonObjectData.getString("image")

                            if(jsonObjectData.getString("image")!=null&&!jsonObjectData.getString("image").equals("")){

                                if (!profilePic.equals("{}") ) {
                                    progress_bar.setVisibility(View.VISIBLE)
                                    //Picasso.get().load(profilePic).into(imgProfileShow);
                                    img_profile.setVisibility(View.GONE)
                                    Picasso.get().load(profilePic)
                                        .fit()
                                        .into(
                                            img_profile_show,
                                            object : Callback {
                                                override fun onSuccess() {
                                                    progress_bar.setVisibility(View.GONE)
                                                }

                                                override fun onError(e: java.lang.Exception) {
                                                    progress_bar.setVisibility(View.GONE)
                                                }
                                            })
                                }

                            }


                        } else {
                            CommonMethod.showToast(jsonObject.getString("message"))
                        }

                    } catch (ex: Exception) {
                        CommonMethod.hideProgress()
                    }

                },

                { error ->

                    CommonMethod.hideProgress()
                    CommonMethod.showToast(getString(R.string.server_error))


                });
    }
}
