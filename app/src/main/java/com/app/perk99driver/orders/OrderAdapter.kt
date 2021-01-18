package com.app.perk99driver.orders

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.perk99driver.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class OrderAdapter(val mList: List<OrderHistoryModel>,val context: Context) : RecyclerView.Adapter<OrderAdapter.OrderHistoryViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder
    {
        val inflater = LayoutInflater.from(parent.context)
        return OrderHistoryViewHolder(inflater, parent,context,mList)
    }

    override fun getItemCount(): Int
    {
        return mList.size
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int)
    {
        val child = mList[position]
        holder.tvTotalLabel!!.visibility=View.VISIBLE
        holder.tvTotal!!.visibility=View.VISIBLE
        holder.tvTotal!!.setText("$ "+child.fare)
        holder.tvName!!.setText(child.rest_name)
        holder.tvStatus!!.setText(child.status)
        holder.id!!.setText("Order Id: "+child.order_id)
        holder.tvPickAddress!!.setText(child.pickup)
        holder.tvDeliveeryAddress!!.setText(child.drop)
        holder.tvDistance!!.setText(child.distance+" Mi")

        holder.recyclerView.setLayoutManager(LinearLayoutManager(
            holder.recyclerView.context,
            LinearLayoutManager.VERTICAL,
            false))
        val cheldadapter = AcceptRequestItemAdapter(child.list)
        holder.recyclerView.adapter = cheldadapter
    }

    class OrderHistoryViewHolder(inflator:LayoutInflater, parent:ViewGroup, var context: Context, val mList: List<OrderHistoryModel>) :
        RecyclerView.ViewHolder(inflator.inflate(
            R.layout.order_row, parent, false))
    {
        var id: TextView? = null
        var tvName: TextView? = null
        var tvStatus: TextView? = null
        var recycler: RecyclerView? = null
        var tvTotal: TextView? = null
        var tvTotalLabel: TextView? = null
        var tvPickAddress: TextView? = null
        var tvDeliveeryAddress: TextView? = null
        var tvDistance: TextView? = null
         var recyclerView:RecyclerView


        init
        {
            id = itemView.findViewById(R.id.id)
            tvName = itemView.findViewById(R.id.tvName)
            tvStatus = itemView.findViewById(R.id.tvStatus)
            recycler = itemView.findViewById(R.id.recycler)
            tvTotal = itemView.findViewById(R.id.tvTotal)
            tvTotalLabel = itemView.findViewById(R.id.tvTotalLabel)
            tvPickAddress = itemView.findViewById(R.id.tvPickAddress)
            tvDeliveeryAddress = itemView.findViewById(R.id.tvDeliveeryAddress)
            tvDistance = itemView.findViewById(R.id.tvDistance)
            recyclerView = itemView.findViewById(R.id.recycler)

        }
    }
}


