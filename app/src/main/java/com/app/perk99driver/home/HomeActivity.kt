package com.app.perk99driver.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.perk99driver.R
import com.app.perk99driver.earnings.MyEarnings
import com.app.perk99driver.help.HelpActivity
import com.app.perk99driver.networking.Injector
import com.app.perk99driver.networking.InterfaceApi
import com.app.perk99driver.orders.MyOrderActivity
import com.app.perk99driver.profile.MyProfileActivity
import com.app.perk99driver.ui.LoginActivity
import com.app.perk99driver.utils.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_header.*
import kotlinx.android.synthetic.main.service_flow_fragment.*
import kotlinx.android.synthetic.main.side_menu.*
import org.json.JSONArray

data class Movie(val title: String, val img: Int)
class HomeActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnCameraMoveListener,
    GoogleMap.OnCameraIdleListener, LocationListener, SelectedPosition, View.OnClickListener,
    OnClickedOk {
    //Animation
    var slide_down: Animation? = null
    var slide_up: Animation? = null
    var slide_up_top: Animation? = null
    var slide_up_down: Animation? = null
    var bounce_anim: Animation? = null
    var disposable: Disposable? = null
    val MY_PERMISSIONS_ACCESS_FINE = 2
    var mMap: GoogleMap? = null
    var mapFragment: SupportMapFragment? = null
    var mGoogleApiClient: GoogleApiClient? = null
    var mLocationRequest: LocationRequest? = null
    var mLastLocation: Location? = null
    var mCurrLocationMarker: Marker? = null
    var result: PendingResult<LocationSettingsResult>? = null
    val REQUEST_LOCATION = 199
    var handleCheckStatus: Handler? = null
    var service_status : String = "online"
    var mSourceLat : String = ""
    var mSourceLon : String = ""
    var mDestLat : String = ""
    var mDestLng : String = ""
    var api: InterfaceApi? = null

    private val nicCageMovies = listOf(
        Movie("Home", R.mipmap.ic_home_tab),
        Movie("Help", R.mipmap.ic_support),
        Movie("Orders", R.mipmap.ic_notification_tab),
        Movie("My Profile", R.mipmap.ic_profile),
        Movie("My Earnings", R.mipmap.ic_support),
        Movie("Logout", R.mipmap.ic_logout))

    lateinit var helper:SharedPrefUtil

    //  lnr_pickUp_cancle
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        img_back_btn.setOnClickListener(this)
        iv_navigation.setOnClickListener(this)
        btn_online_offline.setOnClickListener(this)
        helper = SharedPrefUtil.getInstance()

        if (helper.getString(SharedPrefUtil.ONLINE_OFFLINE_STATUS).equals("1",ignoreCase = true))
        {
            btn_online_offline.setText(getString(R.string.go_online))
            txt_online_offline.setText(getText(R.string.you_are_now_offline))
            service_status="offline"
        }
        else
        {
            btn_online_offline.setText(getString(R.string.go_offline))
            txt_online_offline.setText(getText(R.string.you_are_now_online))
            service_status="online"
        }



      btn_accept.setOnClickListener(this)
      btn_ignore.setOnClickListener(this)
      btn_pick_up.setOnClickListener(this)
      btn_cancel_job.setOnClickListener(this)
      txt_view_detail.setOnClickListener(this)
        img_cross.setOnClickListener(this)
      api = Injector.provideApi()
        recycler.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = ListAdapter(nicCageMovies, this@HomeActivity)
        }

        Handler().postDelayed({
            //  findViewById()
            //                cabType();
            //permission to access location
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(this!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Android M Permission check
                // Android M Permission check
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSIONS_ACCESS_FINE
                )

            } else {
                initMap()
                MapsInitializer.initialize(this)
            }
        }, 500)

        setAnimation()
    }

    @SuppressLint("WrongConstant")
    override fun onClick(v: View?)
    {
        when (v!!.id)
        {
            R.id.img_back_btn ->
            {
                mDrawerLayout.openDrawer(Gravity.START)
            }
            R.id.btn_online_offline ->
            {
                if (btn_online_offline.text.equals(getString(R.string.go_online)))
                {
                    btn_online_offline.setText(getString(R.string.go_offline))
                    txt_online_offline.setText(getText(R.string.you_are_now_online))
                    service_status="online"
                    setOnlineAPI(service_status)
                }
                else
                {
                    btn_online_offline.setText(getString(R.string.go_online))
                    txt_online_offline.setText(getText(R.string.you_are_now_offline))
                    service_status="offline"
                    setOfflineAPI()
                }
            }

            R.id.iv_navigation ->
            {
                Log.e("DestLatt ",mDestLat)
                Log.e("DestLngg ",mDestLng)
                val uri =
                    "http://maps.google.com/maps?daddr=" + mDestLat
                        .toString() + "," + mDestLng
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.setPackage("com.google.android.apps.maps")
                startActivity(intent)
            }
            R.id.btn_accept ->
            {

                acceptJobApi()
                lnr_accept_reject.visibility = View.GONE
              /*  lnr_go_pick_up_screen.visibility=View.VISIBLE
                lnr_go_pick_up_screen.startAnimation(slide_up)*/
            }
            R.id.btn_ignore ->
            {
                rejectJobApi()
                lnr_accept_reject.visibility = View.GONE
                lnr_go_pick_up_screen.visibility=View.GONE
                lnr_offline.visibility=View.VISIBLE
            }
            R.id.btn_cancel_job ->
            {
                lnr_accept_reject.visibility = View.GONE
                lnr_go_pick_up_screen.visibility=View.GONE
                lnr_offline.visibility=View.VISIBLE
//                cancelJobAPi()
                rejectJobApi()
            }
            R.id.btn_pick_up ->
            {
                pickUpApi()

               /* lnr_go_pick_up_screen.visibility=View.GONE
                lnr_pick_up.visibility=View.VISIBLE
                lnr_pick_up.startAnimation(slide_up) */

//                lnr_accept_reject.visibility = View.GONE
//                lnr_pick_up_screen.visibility=View.GONE
//                lnr_offline.visibility=View.VISIBLE
            }
            R.id.txt_view_detail ->
            {
                dropClick="1"
                goToDropOffDailog()
            }
            R.id.img_cross ->
            {
            mDrawerLayout!!.closeDrawer(GravityCompat.START)
            }
        }
    }

    var dropClick:String="0"

    private fun setOnlineAPI(serviceStatus: String)
    {
        CommonMethod.showProgress(this)
        disposable = api!!.online("drivers").subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    CommonMethod.hideProgress()
                    try {
                        val generalResponse = GeneralResponse(result)
                        val jsonObject = generalResponse.getResponse_object()
                        if (jsonObject.getString("status").equals("true")) {
                            helper.saveString(SharedPrefUtil.ONLINE_OFFLINE_STATUS, "1")

                            CommonMethod.showToast(jsonObject.getString("message"))

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

    private fun setOfflineAPI()
    {
        CommonMethod.showProgress(this)
        disposable = api!!.offline("drivers").subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    CommonMethod.hideProgress()
                    try {
                        val generalResponse = GeneralResponse(result)
                        val jsonObject = generalResponse.getResponse_object();
                        //  Log.v("jsonObject", "" + jsonObject)
                        if (jsonObject.getString("status").equals("true"))
                        {
                            helper.saveString(SharedPrefUtil.ONLINE_OFFLINE_STATUS, "0")

                            CommonMethod.showToast(jsonObject.getString("message"))
                        }
                        else
                        {
                            CommonMethod.showToast(jsonObject.getString("message"))
                        }
                    }
                    catch (ex: java.lang.Exception)
                    {
                        CommonMethod.hideProgress()
                        // CommonMethod.showToast("Invalid Credentials")
                    }
                },
                {
                    error ->
                    CommonMethod.hideProgress()
                    CommonMethod.showToast(getString(R.string.server_error))
                }
            )
    }

    override fun onResume()
    {
        super.onResume()
        startCheckStatus()
        MyApplication.setActivity(this)
    }

    // method for initializing start check status
    private fun startCheckStatus()
    {
        handleCheckStatus = Handler()
        //  helper = ConnectionHelper(context)
        //check status every 3 sec
        handleCheckStatus!!.postDelayed(object : Runnable
        {
            override fun run()
            {
                if (MyApplication.hasNetwork())
                {
                    updateLocAPi()
                }
                handleCheckStatus!!.postDelayed(this, 3000)
            }
        }, 9000)

    }

    private fun updateLocAPi()
    {
        Log.e("sassa","lat "+mSourceLat +"ss "+mSourceLon)
        disposable = api!!.sendLocation(mSourceLat,mSourceLon,"drivers")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                    CommonMethod.hideProgress()
                    try
                    {
                        val generalResponse = GeneralResponse(result)
                        val jsonObject = generalResponse.getResponse_object();
                        Log.e("UpdateLocationResponse "," "+jsonObject)
                        Log.e("Statusss "," "+jsonObject.getString("status"))
                        if (jsonObject.getString("status").equals("success"))
                        {
                            Log.e("inside 1 ","11")



                            if(jsonObject.has("result"))
                            {
                                Log.e("inside 2 ","22")
                                val o = jsonObject.get("result")
                                if (o is JSONArray)
                                {
                                    lnr_offline.visibility=View.VISIBLE

                                    if (dropClick.equals("1",ignoreCase = true))
                                    {
                                        lnr_go_pick_up_screen.visibility=View.GONE
                                        dropClick="0"
                                    }

                                }
                                else
                                {
                                    val jsonObjectData = jsonObject.getJSONObject("result")
                                    Log.e("Status ",jsonObjectData.getString("status"))
                                    if (jsonObjectData.getString("status").equals("pending"))
                                    {
                                        //todo show incoming request popup.

                                        job_id=jsonObjectData.getString("job_id");
                                        order_id=jsonObjectData.getString("order_id");
                                        lnr_offline.visibility=View.GONE


                                        pickUp=jsonObjectData.getString("pickup")
                                        drop=jsonObjectData.getString("drop")
                                        orderRefNo=jsonObjectData.getString("order_id")
                                        distance=jsonObjectData.getString("distance")
                                        eta=jsonObjectData.getString("eta")
                                        estFare=jsonObjectData.getString("fare")
                                        phone_number=jsonObjectData.getString("phone_number")



                                        showAcceptSceen(jsonObjectData.getString("pickup"),
                                            jsonObjectData.getString("drop"),jsonObjectData.getString("distance"),
                                            jsonObjectData.getString("distance"));

                                    }
                                    else  if (jsonObjectData.getString("status").equals("accepted"))
                                    {
                                        Log.e("insideee ","accepted")
                                        Log.e("visibility ",lnr_go_pick_up_screen.visibility!!.toString())
                                        //show pick up request popup.


                                        try
                                        {
                                            job_id=jsonObjectData.getString("job_id");
                                            order_id=jsonObjectData.getString("order_id");

                                            pickUp=jsonObjectData.getString("pickup")
                                            drop=jsonObjectData.getString("drop")
                                            orderRefNo=jsonObjectData.getString("order_id")
                                            distance=jsonObjectData.getString("distance")
                                            eta=jsonObjectData.getString("eta")
                                            estFare=jsonObjectData.getString("fare")
//                                        phone_number=jsonObjectData.getString("phone_number")

                                            lnr_offline.visibility=View.GONE
                                            lnr_accept_reject.visibility = View.GONE

                                            lnr_go_pick_up_screen.visibility=View.VISIBLE
                                            lnr_go_pick_up_screen.startAnimation(slide_up)
                                            txt_pick_location_pic.setText(jsonObjectData.getString("pickup"))
                                            txt_order_refernce.setText(jsonObjectData.getString("order_id"))
                                            txt_distance_pick.setText(jsonObjectData.getString("distance")+" Mi")
                                            txt_fare.setText("$ "+jsonObjectData.getString("fare"))
                                            txt_est_time.setText(jsonObjectData.getString("eta")+" Mins")

                                        }
                                        catch(ex:Exception)
                                        {
                                            ex.printStackTrace()
                                        }


                                    }
                                    else if (jsonObjectData.getString("status").equals("pickup"))
                                    {
                                        //show pick up request popup.

                                        job_id=jsonObjectData.getString("job_id");
                                        order_id=jsonObjectData.getString("order_id");

                                        pickUp=jsonObjectData.getString("pickup")
                                        drop=jsonObjectData.getString("drop")
                                        orderRefNo=jsonObjectData.getString("order_id")
                                        distance=jsonObjectData.getString("distance")
                                        eta=jsonObjectData.getString("eta")
                                        estFare=jsonObjectData.getString("fare")
                                        phone_number=jsonObjectData.getString("phone_number")
                                        mDestLat=jsonObjectData.getString("drop_lat")
                                        mDestLng=jsonObjectData.getString("drop_long")
                                        iv_navigation.visibility=View.VISIBLE


                                        lnr_offline.visibility=View.GONE
                                        lnr_go_pick_up_screen.visibility=View.GONE
                                        lnr_pick_up.visibility=View.VISIBLE
                                        lnr_pick_up.startAnimation(slide_up)
                                        txt_pick_up.setText("Drop up- "+jsonObjectData.getString("eta")+" Mins to arrival")

                                        ivCall.setOnClickListener {
                                            Log.e("imageClickCall ","call")

                                            val intent = Intent(Intent.ACTION_DIAL)
                                            intent.data =
                                                Uri.parse("tel:" + phone_number)
                                            startActivity(intent)
                                        }

                                        btn_pick_up_door.setOnClickListener {
                                            rejectJobApi()
                                        }
                                    }
                                    else  if (jsonObjectData.getString("status").equals("on_the_way"))
                                    {
                                        //show pick up request popup.

                                        job_id=jsonObjectData.getString("job_id");
                                        order_id=jsonObjectData.getString("order_id");

                                        pickUp=jsonObjectData.getString("pickup")
                                        drop=jsonObjectData.getString("drop")
                                        orderRefNo=jsonObjectData.getString("order_id")
                                        distance=jsonObjectData.getString("distance")
                                        eta=jsonObjectData.getString("eta")
                                        estFare=jsonObjectData.getString("fare")
                                        phone_number=jsonObjectData.getString("phone_number")
                                        mDestLat=jsonObjectData.getString("drop_lat")
                                        mDestLng=jsonObjectData.getString("drop_long")
                                        iv_navigation.visibility=View.VISIBLE


                                        lnr_offline.visibility=View.GONE
                                        // lnr_go_pick_up_screen.visibility=View.GONE
                                        lnr_pick_up.visibility=View.GONE
                                        lnr_pick_up.startAnimation(slide_up)

                                        completeDailog()
                                    }
                                    else
                                    {
                                        lnr_offline.visibility=View.VISIBLE
                                        if (dropClick.equals("1",ignoreCase = true))
                                        {
                                            lnr_go_pick_up_screen.visibility=View.GONE
                                            dropClick="0"
                                        }
                                    }
                                }
                            }
                        }
                        else
                        {
                            Log.e("inside elseOutside ","elsee")
                            lnr_offline.visibility=View.VISIBLE
                        }
                    }
                    catch (ex: java.lang.Exception)
                    {
                        CommonMethod.hideProgress()
                        ex.printStackTrace()
                    }

                },{
                 error ->
                 CommonMethod.hideProgress()
                }
            )
    }

    private fun goTodopOffApi()
    {
        //     Log.e("sassa","lat "+mSourceLat +"ss "+mSourceLon)
        Log.e("droppedOffParams ",order_id)
        disposable = api!!.droppedOff(order_id,"drivers")
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
                            showcallAlert()
                            lnr_pick_up.visibility=View.GONE
                        } else
                        {
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

    private fun dopOffApi()
    {
        //     Log.e("sassa","lat "+mSourceLat +"ss "+mSourceLon)
        Log.e("DropOffParams ",order_id)
        disposable = api!!.go_to_dropoff(order_id,"drivers")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    CommonMethod.hideProgress()
                    try {
                        val generalResponse = GeneralResponse(result)
                        val jsonObject = generalResponse.getResponse_object();
                        Log.v("DropOffDialog ", "" + jsonObject)
                        if (jsonObject.getString("status").equals("success")) {

                            completeDailog()
                        } else {
                            CommonMethod.showToast("Server Error");
                        }

                    } catch (ex: java.lang.Exception) {
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

    private fun pickUpApi()
    {
        //     Log.e("sassa","lat "+mSourceLat +"ss "+mSourceLon)
        Log.e("PickUpParams ",order_id)
        Log.e("providers ","drivers")
        disposable = api!!.pickup_order(order_id,"drivers")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    CommonMethod.hideProgress()
                    try {
                        Log.e("selecttt ","njjhj");
                        val generalResponse = GeneralResponse(result)
                        val jsonObject = generalResponse.getResponse_object();

                        Log.v("acceptjson", "" + jsonObject)

                        if (jsonObject.getString("status").equals("success"))
                        {
                            lnr_go_pick_up_screen.visibility=View.GONE
                            lnr_pick_up.visibility=View.VISIBLE
                            lnr_pick_up.startAnimation(slide_up)
                        }
                        else
                        {
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

    var order_id:String=""
    var job_id:String = ""

    private fun acceptJobApi()
    {
             Log.e("AcceptParams ",job_id)
             Log.e("AcceptParamsOrderId ",order_id)
             Log.e("Providers ","drivers")
        disposable = api!!.acceptJob(job_id,order_id,"drivers")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    CommonMethod.hideProgress()
                    try {
                        val generalResponse = GeneralResponse(result)
                        val jsonObject = generalResponse.getResponse_object();
                        Log.v("acceptjson", "" + jsonObject)

                        if (jsonObject.getString("status").equals("success"))
                        {
                           /* lnr_accept_reject.visibility = View.GONE
                            lnr_go_pick_up_screen.visibility=View.VISIBLE
                            lnr_go_pick_up_screen.startAnimation(slide_up)*/
                        }
                        else
                        {
                            CommonMethod.showToast("Server Error");
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

    private fun rejectJobApi()
    {


        Log.e("RejectParams ",job_id)
        Log.e("OrderId ",order_id)
        Log.e("Provider ","drivers")
        disposable = api!!.rejectJob(job_id,order_id,"drivers")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    CommonMethod.hideProgress()
                    try {
                        val generalResponse = GeneralResponse(result)
                        val jsonObject = generalResponse.getResponse_object();
                        //  Log.v("jsonObject", "" + jsonObject)
                        if (jsonObject.getString("status").equals("success")) {

                            lnr_accept_reject.visibility = View.GONE
                            lnr_go_pick_up_screen.visibility=View.GONE
                            lnr_offline.visibility=View.VISIBLE
                            lnr_pick_up.visibility=View.GONE

                        } else {
                            CommonMethod.showToast("Some Error Occured");
                        }

                    } catch (ex: java.lang.Exception) {
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

    private fun cancelJobAPi()
    {
        Log.e("OrderId ",order_id)
        Log.e("Provider ","drivers")
        disposable = api!!.cancel_order(order_id,"drivers")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    CommonMethod.hideProgress()
                    try {
                        val generalResponse = GeneralResponse(result)
                        val jsonObject = generalResponse.getResponse_object();
                        //  Log.v("jsonObject", "" + jsonObject)
                        if (jsonObject.getString("status").equals("success"))
                        {
                            lnr_accept_reject.visibility = View.GONE
                            lnr_go_pick_up_screen.visibility=View.GONE
                            lnr_offline.visibility=View.VISIBLE
                        }
                        else
                        {
                            CommonMethod.showToast("Some error occured");
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

    var pickUp:String=""
    var drop:String=""
    var orderRefNo:String=""
    var distance:String=""
    var estFare:String=""
    var eta:String=""
    var phone_number:String=""

    fun goToDropOffDailog() {
        val dialog= Dialog(this, R.style.DialogTheme)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setCancelable(false)
//            dialog.getWindow().setLayout(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            );
        // dialog.getWindow().setGravity(Gravity.CENTER);
        dialog!!.setContentView(R.layout.detail_ride_dialog)
       // val  txt_servicetype=dialog!!.findViewById(R.id.txt_servicetype) as TextView
        val btnDrop = dialog!!.findViewById(R.id.btn_drop_off) as Button
        val btnReject = dialog!!.findViewById(R.id.btn_cancel_job) as Button
        val iv_navigationnn = dialog!!.findViewById(R.id.iv_navigationnn) as ImageView

        val txt_pick_location_pic = dialog!!.findViewById(R.id.txt_pick_location_pic) as TextView
        val txt_order_refernce = dialog!!.findViewById(R.id.txt_order_refernce) as TextView
        val txt_distance_pick = dialog!!.findViewById(R.id.txt_distance_pick) as TextView
        val txt_est_fare = dialog!!.findViewById(R.id.txt_est_fare) as TextView
        val btn_cancel_job = dialog!!.findViewById(R.id.btn_cancel_job) as Button
        //  val edt_phone = dialog.findViewById(R.id.edt_phone) as EditText

        txt_pick_location_pic.setText(drop)
        txt_order_refernce.setText(orderRefNo)
        txt_distance_pick.setText(distance+" Mi")
        txt_est_fare.setText("$ "+estFare)
        txt_est_time.setText(eta+" Mins")


        iv_navigationnn.setOnClickListener {
            Log.e("DestLatt ",mDestLat)
            Log.e("DestLngg ",mDestLng)
            val uri =
                "http://maps.google.com/maps?daddr=" + mDestLat
                    .toString() + "," + mDestLng
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            intent.setPackage("com.google.android.apps.maps")
            startActivity(intent)

        }

        btnReject.setOnClickListener({
            dialog!!.dismiss()
        })

        btn_cancel_job.setOnClickListener({
            dialog!!.dismiss()
//            cancelJobAPi()
            rejectJobApi()

        })
        //  val cc= dialog.findViewById(R.id.cc) as CountryCodePicker
        btnDrop.setOnClickListener({
//            completeDailog()
            dopOffApi()
            dialog!!.dismiss()
        })
        dialog!!.show()
    }

    fun completeDailog()
    {
        val dialog= Dialog(this, R.style.DialogTheme)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setCancelable(false)
//            dialog.getWindow().setLayout(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            );
        // dialog.getWindow().setGravity(Gravity.CENTER);
        dialog!!.setContentView(R.layout.end_ride_dialog)
        val btnCompleteRide = dialog!!.findViewById(R.id.btn_complete_ride) as Button
        val txt_pick_location_pic = dialog!!.findViewById(R.id.txt_pick_location_pic) as TextView
        val txt_order_refernce = dialog!!.findViewById(R.id.txt_order_refernce) as TextView
        val txt_pay_method = dialog!!.findViewById(R.id.txt_pay_method) as TextView
        val txt_distance_pick = dialog!!.findViewById(R.id.txt_distance_pick) as TextView
        val txt_est_fare = dialog!!.findViewById(R.id.txt_est_fare) as TextView
        val iv_navigation_option = dialog!!.findViewById(R.id.iv_navigation_option) as ImageView

        txt_pick_location_pic.setText(drop)
        txt_order_refernce.setText(orderRefNo)
        txt_pay_method.setText("Wallet")
        txt_distance_pick.setText(distance+" Mi")
        txt_est_fare.setText("$ "+estFare)

        iv_navigation_option.setOnClickListener {

            Log.e("DestLatt ",mDestLat)
            Log.e("DestLngg ",mDestLng)
            val uri =
                "http://maps.google.com/maps?daddr=" + mDestLat
                    .toString() + "," + mDestLng
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            intent.setPackage("com.google.android.apps.maps")
            startActivity(intent)
        }

        btnCompleteRide.setOnClickListener({
            dialog!!.dismiss()

            goTodopOffApi()

        })
        dialog!!.show()
    }

    fun showcallAlert()
    {
        val dialog = Dialog(this, R.style.DialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.service_booked_popup)
        val lp = WindowManager.LayoutParams()
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT

        dialog.window!!.attributes = lp
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        Handler().postDelayed({
            dialog.dismiss()
            lnr_go_pick_up_screen.visibility=View.GONE
            lnr_pick_up.visibility=View.GONE
            lnr_offline.visibility=View.VISIBLE

            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }, 3000)
        dialog.show()
    }

    private fun showAcceptSceen(pick:String,drop:String,dist:String,fare:String)
    {
        lnr_offline.visibility=View.GONE
        lnr_accept_reject.visibility = View.VISIBLE
        lnr_accept_reject.startAnimation(slide_up)

        txt_pick_location.setText(pick)
        txt_drop_off.setText(drop)
        txt_distance.setText(dist+ " Mi")
        txt_fare.setText("$ "+fare)
    }

    override fun onSelection(position: Int)
    {
        when (position)
        {
            0->{
                mDrawerLayout!!.closeDrawer(GravityCompat.START)
                val intent1 = Intent(this, HomeActivity::class.java)
                startActivity(intent1)
            }
            1 -> {
                mDrawerLayout!!.closeDrawer(GravityCompat.START)
                val intent1 = Intent(this, HelpActivity::class.java)
                startActivity(intent1)

            }
            2 -> {
                mDrawerLayout!!.closeDrawer(GravityCompat.START)
                val intent1 = Intent(this, MyOrderActivity::class.java)
                startActivity(intent1)

            }
            3 -> {
                mDrawerLayout!!.closeDrawer(GravityCompat.START)
                val intent1 = Intent(this, MyProfileActivity::class.java)
                startActivity(intent1)

            }
            4 -> {
                mDrawerLayout!!.closeDrawer(GravityCompat.START)
                val intent1 = Intent(this, MyEarnings::class.java)
                startActivity(intent1)

            }
            5 -> {
                mDrawerLayout!!.closeDrawer(GravityCompat.START)
                CommonMethod.logout(this, this)


            }
        }
    }

    fun initMap()
    {
        if (mMap == null) {
            //    if (isAdded) {
//                val fm = fragmentManager
//                mapFragment = fm!!.findFragmentById(R.id.provider_map) as SupportMapFragment?
//                mapFragment!!.getMapAsync(this)

            mapFragment = supportFragmentManager
                .findFragmentById(R.id.provider_map) as SupportMapFragment?
            mapFragment!!.getMapAsync(this)
            // }
        }
//        if (mMap != null) {
//            setupMap();
//        }
    }

    // on map ready
    override fun onMapReady(googleMap: GoogleMap)
    {
        try {
            val success =
                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json))
            if (!success) {
                // utils.print("Map:Style", "Style parsing failed.")
            } else {
                // utils.print("Map:Style", "Style Applied.")
            }
        } catch (e: Resources.NotFoundException) {
            // utils.print("Map:Style", "Can't find style. Error:")
        }
        //  customDialog.dismiss()
        mMap = googleMap
        setupMap()

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this!!, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
            ) {
                // Location Permission already granted
                buildGoogleApiClient()
                // mMap.setMyLocationEnabled(true);
            } else {
                // Request Location Permission
                //    checkLocationPermission()
            }
        } else {
            buildGoogleApiClient()
            //            mMap.setMyLocationEnabled(true);
        }
    }

    private fun setupMap()
    {
        if (mMap != null)
        {
//         mMap.setMyLocationEnabled(true);
//         mMap.getUiSettings().setMyLocationButtonEnabled(true);
           mMap!!.uiSettings.isCompassEnabled = false
           mMap!!.isBuildingsEnabled = true
           mMap!!.setOnCameraMoveListener(this)
           mMap!!.setOnCameraIdleListener(this)
           mMap!!.uiSettings.isRotateGesturesEnabled = false
           mMap!!.uiSettings.isTiltGesturesEnabled = false
        }
    }

    @Synchronized
    protected fun buildGoogleApiClient()
    {
        mGoogleApiClient = this?.let {
            GoogleApiClient.Builder(it)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build()
        }
        mGoogleApiClient!!.connect()
    }

    override fun onConnected(@Nullable bundle: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.setInterval(1000)
        mLocationRequest!!.setFastestInterval(1000)
        mLocationRequest!!.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
        if (ContextCompat.checkSelfPermission(this!!, Manifest.permission.ACCESS_FINE_LOCATION)
            === PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this)
            // LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this as LocationListener)
        } else
        {
            ActivityCompat.requestPermissions(this!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSIONS_ACCESS_FINE)
        }

        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
            .addLocationRequest(mLocationRequest!!)
        builder.setAlwaysShow(true)

        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build())

        result!!.setResultCallback(object : ResultCallback<LocationSettingsResult?> {
            override fun onResult(result: LocationSettingsResult) {
                val status: Status = result.getStatus()
                when (status.getStatusCode()) {
                    LocationSettingsStatusCodes.SUCCESS -> {
                    }
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                this@HomeActivity,
                                REQUEST_LOCATION
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            // Ignore the error.
                        }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }
        })
    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult)
    {

    }

    override fun onCameraMove()
    {

    }

    override fun onCameraIdle()
    {

    }

    override fun onLocationChanged(location: Location)
    {
        mLastLocation = location
        if (mCurrLocationMarker != null)
        {
            mCurrLocationMarker!!.remove()
        }
        //        double latitute = Double.parseDouble(lat);
//        double longitute = Double.parseDouble(longitut);
        //Place current location marker
        val latLng = LatLng(location.latitude, location.longitude)
        // LatLng latLng = new LatLng(location.getAltitude(), longitute);


        mSourceLat=  mLastLocation!!.latitude.toString()
         mSourceLon=  mLastLocation!!.longitude.toString()
        // updateLoc()


        // locationSource = LatLngM( mLastLocation!!.latitude, mLastLocation!!.longitude)

        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title("Position")
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        mCurrLocationMarker = mMap!!.addMarker(markerOptions)

        //move map camera
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap!!.animateCamera(CameraUpdateFactory.zoomTo(12f))

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
        }
    }

    override fun onYesClicked()
    {
        Log.e("updateonyesclick ","test")
        SharedPrefUtil.getInstance().onLogout();
        val intent=Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent);
    }

    private fun setAnimation()
    {
        slide_down = AnimationUtils.loadAnimation(this, R.anim.slide_down_n)
        slide_up = AnimationUtils.loadAnimation(this, R.anim.slide_up_n)
        slide_up_top = AnimationUtils.loadAnimation(this, R.anim.slide_up_top)
        slide_up_down = AnimationUtils.loadAnimation(this, R.anim.slide_up_down)
        bounce_anim = AnimationUtils.loadAnimation(this, R.anim.bounce_anim)
    }

}