package com.app.perk99driver.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.perk99driver.R


class ListAdapter(private val list: List<Movie>, var mListerner: SelectedPosition) : RecyclerView.Adapter<MovieViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder
    {
        val inflater = LayoutInflater.from(parent.context)
        return MovieViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int)
    {
        val movie: Movie = list[position]

        holder.itemView.setOnClickListener({

            mListerner.onSelection(position)
        })
        holder.bind(movie)
    }

    override fun getItemCount(): Int = list.size

}

class MovieViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item, parent, false)) {
    private var mTitleView: TextView? = null
    private var img_title: ImageView? = null


    init {
        mTitleView = itemView.findViewById(R.id.txt_item)
        img_title = itemView.findViewById(R.id.img_title)
        mTitleView!!.visibility= View.VISIBLE
    }

    fun bind(movie: Movie) {
        mTitleView?.text = movie.title
        img_title!!.setImageResource(movie.img)
    }

}


 public interface SelectedPosition

 {

     fun onSelection(position: Int)
 }