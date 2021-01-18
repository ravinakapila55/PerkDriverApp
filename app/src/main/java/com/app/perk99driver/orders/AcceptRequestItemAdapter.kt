package com.app.perk99driver.orders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.perk99driver.R

import kotlinx.android.synthetic.main.request_item_row.view.*

class AcceptRequestItemAdapter(val mList: List<MenuItemsModel>) : RecyclerView.Adapter<AcceptRequestItemAdapter.ViewHolder>()
{
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val textView: TextView = itemView.tvMenu1
        // val txt_brand=itemView.findViewById(R.id.txt_brand)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.request_item_row, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int
    {
        return mList.size
    }

    override fun onBindViewHolder(holder: AcceptRequestItemAdapter.ViewHolder, position: Int)
    {
        val child = mList[position]
        // holder.imageView.setImageResource(child.image)
        val str1 = child.qty
        val str2 = "X"
        val str3 = child.item_name
        // string interpolation
        val str4 = "$str1 $str2 $str3"
        holder.textView?.setText(str4)
    }
}
