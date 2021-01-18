package com.app.perk99driver.orders

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.perk99driver.R
import com.app.perk99driver.networking.Injector
import com.app.perk99driver.networking.InterfaceApi
import com.app.perk99driver.utils.CommonMethod
import com.app.perk99driver.utils.GeneralResponse
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_my_order.*
import kotlinx.android.synthetic.main.activity_my_order.recycler_order
import kotlinx.android.synthetic.main.activity_my_order.tvNoData
import kotlinx.android.synthetic.main.header_small.*
import kotlinx.android.synthetic.main.my_orders.*
import kotlinx.android.synthetic.main.service_flow_fragment.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList


data class Order(val title: String, val img: Int)
class MyOrderActivity : AppCompatActivity(), View.OnClickListener {



    private  var mList: ArrayList<OrderHistoryModel>? = null
    private  var mHIstoryList: ArrayList<OrderHistoryModel>? = null
    private lateinit var linearLayoutManager: LinearLayoutManager





    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_my_order)
        setContentView(R.layout.my_orders)
        mHIstoryList= ArrayList<OrderHistoryModel>()
        mList= ArrayList<OrderHistoryModel>()


        img_back_btn.setOnClickListener(this)
        tvPreparing.setOnClickListener(this)
         tvReady.setOnClickListener(this)
        linearLayoutManager = LinearLayoutManager(this)
        recycler_order.layoutManager = linearLayoutManager

    }

    override fun onResume()
    {
        super.onResume()
        callOrderHistory()
    }

    fun setAdapter(type:String)
    {
        if(type.equals("active",ignoreCase = true))
        {
            mAcceptedOrderAdapter = OrderAdapter(mList!!,this@MyOrderActivity)
            recycler_order.setAdapter(mAcceptedOrderAdapter)
        }
        else{
            mAcceptedOrderAdapter = OrderAdapter(mHIstoryList!!,this@MyOrderActivity)
            recycler_order.setAdapter(mAcceptedOrderAdapter)
        }

    }


    override fun onClick(v: View?) {
        when (v!!.id)
        {
            R.id.tvPreparing ->
            {

                tvPreparing.setTextColor(resources.getColor(R.color.app_orange))
                tvPreparing.setBackgroundResource(R.drawable.red_background)

                tvReady.setTextColor(resources.getColor(R.color.app_black))
                tvReady.setBackgroundResource(R.drawable.gray_fill_background)

                if (mList!!.size>0)
                {
                    recycler_order!!.setVisibility(View.VISIBLE)
                    tvNoData.setVisibility(View.GONE)
                    setAdapter("active")
                }
                else{
                    recycler_order!!.setVisibility(View.GONE)
                    tvNoData.setVisibility(View.VISIBLE)
                }
            }
            R.id.tvReady ->
            {

                tvReady.setTextColor(resources.getColor(R.color.app_orange))
                tvReady.setBackgroundResource(R.drawable.red_background)

                tvPreparing.setTextColor(resources.getColor(R.color.app_black))
                tvPreparing.setBackgroundResource(R.drawable.gray_fill_background)

                if (mHIstoryList!!.size>0)
                {
                    recycler_order!!.setVisibility(View.VISIBLE)
                    tvNoData.setVisibility(View.GONE)
                    setAdapter("history")
                }
                else{
                    recycler_order!!.setVisibility(View.GONE)
                    tvNoData.setVisibility(View.VISIBLE)
                }
            }

            R.id.img_back_btn ->
            {
                finish()
            }




        }
    }


    /* private fun setTabs()
     {
         tab_layout.addTab(tab_layout.newTab().setText("Active Order"))
         tab_layout.addTab(tab_layout.newTab().setText("Order History"))

         tab_layout.addOnTabSelectedListener(object : OnTabSelectedListener
         {
             override fun onTabSelected(tab: TabLayout.Tab)
             {
                 if(tab.position==0)
                 {
                    // CommonMethod.showToast("I am First")
                 }
                 else
                 {
                    // CommonMethod.showToast("I am Seconf")
                 }
             }
             override fun onTabUnselected(tab: TabLayout.Tab) {}
             override fun onTabReselected(tab: TabLayout.Tab) {}
         })
     }*/

    var disposable: Disposable? = null
    var api: InterfaceApi? = null
    private lateinit var layoutManager: RecyclerView.LayoutManager



    fun callOrderHistory()
    {
        api = Injector.provideApi()

            disposable = api!!.getJobs("drivers")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    CommonMethod.hideProgress()
                    try
                    {
                        val generalResponse = GeneralResponse(result)
                        val jsonObject = generalResponse.getResponse_object();
                        Log.e("MyOrderResponse "," "+jsonObject)
                        Log.e("Statusss "," "+jsonObject.getString("status"))
                        if (jsonObject.getString("status").equals("success"))
                        {
                            Log.e("inside 1 ","11")

                            mList!!.clear()

                            mHIstoryList!!.clear()
//                            mList=getDataAs(jsonObject.getJSONArray("result"))

                            val jsonArray = jsonObject.getJSONArray("result")
                            Log.e("lengthhhhh ",jsonArray.length().toString())
//        String job_id,order_id,pickup,drop,distance,fare,pickup_lat,pickup_long,drop_lat,drop_long,rest_name,delivery_add,status;
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject1 = jsonArray.getJSONObject(i)
                                Log.e("insidrJSON ",jsonObject1.toString())

                                val restaurantsList = OrderHistoryModel()
                                restaurantsList.job_id = jsonObject1.getString("job_id")
                                restaurantsList.order_id = jsonObject1.getString("order_id")
                                restaurantsList.pickup = jsonObject1.getString("pickup")
                                restaurantsList.drop = jsonObject1.getString("drop")
                                restaurantsList.distance = jsonObject1.getString("distance")
                                restaurantsList.fare = jsonObject1.getString("total_amount")
//                                restaurantsList.pickup_lat = jsonObject1.getString("pickup_lat")
//                                restaurantsList.pickup_long = jsonObject1.getString("pickup_long")
//                                restaurantsList.drop_lat = jsonObject1.getString("drop_lat")
                                restaurantsList.rest_name = jsonObject1.getString("rest_name")
                                restaurantsList.delivery_add = jsonObject1.getString("delivery_add")
                                restaurantsList.status = jsonObject1.getString("status")


                                val item = jsonObject1.getJSONArray("details")

                                Log.e("MenuItemsArray ",item.length().toString())
                                val list = ArrayList<MenuItemsModel>()

                                if (item.length() > 0)
                                {
                                    for (j in 0 until item.length())
                                    {
                                        val jsonObject111 = item.getJSONObject(j)
                                        val foodItemModel = MenuItemsModel()
                                        foodItemModel.item_name = jsonObject111.getString("menu_name")
                                        foodItemModel.qty = jsonObject111.getString("qty")
                                        list.add(foodItemModel)
                                    }
                                }

                                restaurantsList.setList(list)



                                //delivered//pickup
                                if (jsonObject1.getString("status").equals("delivered", ignoreCase = true))
                                {

                                    mHIstoryList!!.add(restaurantsList)
                                }
                                else
                                {
                                    mList!!.add(restaurantsList)
                                }
                            }

                            Log.e("sizeList ",mList!!.size.toString())
                            Log.e("history ",mHIstoryList!!.size.toString())

                            if (mList!!.size > 0)
                            {
                                recycler_order!!.setVisibility(View.VISIBLE)
                                tvNoData.setVisibility(View.GONE)
                                setAdapter("active")
                            }
                            else
                            {
                                recycler_order!!.setVisibility(View.GONE)
                                tvNoData.setVisibility(View.VISIBLE)
                            }
                        }
                        else
                        {
                            Log.e("inside elseOutside ","elsee")
                            recycler_order!!.setVisibility(View.GONE)
                            tvNoData.setVisibility(View.VISIBLE)
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
    lateinit var mAcceptedOrderAdapter: OrderAdapter

    private fun getDataAs(jsonArray: JSONArray? = null): ArrayList<OrderHistoryModel>
    {
        val len = jsonArray!!.length()
        val gson = Gson()
        val list = ArrayList<OrderHistoryModel>(len)
        for (i in 0 until jsonArray.length())
        {
            list.add(gson.fromJson<OrderHistoryModel>(jsonArray.getString(i),OrderHistoryModel::class.java))
        }
        return list
    }
}
