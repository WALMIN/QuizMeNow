package com.walmin.android.quizmenow

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import java.util.*
import kotlin.collections.ArrayList

class HomeListAdapter(homeList: ArrayList<HomeItemData>, var onClickListener: OnHomeItemClickListener) : RecyclerView.Adapter<HomeListAdapter.ViewHolder>() {

    var homeList: ArrayList<HomeItemData> = homeList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_item, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(homeList[position], onClickListener)

    }

    override fun getItemCount() = homeList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(item: HomeItemData, onClick: OnHomeItemClickListener) {
            val quizCardView = itemView.findViewById<CardView>(R.id.quizCard)
            val quizTitleView = itemView.findViewById<TextView>(R.id.quizTitle)

            quizTitleView.text = item.title

            if(item.color == ""){
                quizCardView.setCardBackgroundColor(itemView.context.getColor(R.color.colorPrimaryDark))

            }else{
                quizCardView.setCardBackgroundColor(Color.parseColor("#" + item.color))

            }

            if(item.value.toInt() >= 9) {
                Glide.with(itemView.context)
                    .load(itemView.context.getString(R.string.thumbnailURL,
                            item.title.toLowerCase(Locale.ROOT)
                                .replace(" ", "_")
                        )
                    )
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.error)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .into(itemView.findViewById(R.id.quizThumbnail))

            }else{
                Glide.with(itemView.context)
                    .load(Tools.getImage(itemView.context,
                        item.title
                            .replace(itemView.context.getString(R.string.goOnline), "wifi")
                            .toLowerCase(Locale.ROOT)
                            .replace(" ", "_")))
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.error)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .into(itemView.findViewById(R.id.quizThumbnail))

            }

            itemView.setOnClickListener{
                onClick.onHomeItemClick(item)

            }

        }

    }

}

interface OnHomeItemClickListener{
    fun onHomeItemClick(item: HomeItemData)

}