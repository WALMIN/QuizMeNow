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

class HomeListAdapter(var homeList: List<Category>, var onClickListener: OnHomeItemClickListener) : RecyclerView.Adapter<HomeListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_item, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(homeList[position], onClickListener)

    }

    override fun getItemCount() = homeList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(item: Category, onClick: OnHomeItemClickListener) {
            val priceView = itemView.findViewById<TextView>(R.id.priceView)
            priceView.visibility = View.GONE

            val quizCardView = itemView.findViewById<CardView>(R.id.quizCard)
            val quizTitleView = itemView.findViewById<TextView>(R.id.quizTitle)

            // Title
            quizTitleView.text = item.title

            // Color
            if(item.color == ""){
                quizCardView.setCardBackgroundColor(itemView.context.getColor(R.color.colorPrimaryDark))

            }else{
                quizCardView.setCardBackgroundColor(Color.parseColor("#" + item.color))

            }

            // Locked
            if(item.locked){
                Glide.with(itemView.context)
                    .load(R.drawable.lock)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.error)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .into(itemView.findViewById(R.id.quizThumbnail))

                priceView.text = item.price
                priceView.visibility = View.VISIBLE

            // Show icon
            }else{
                Glide.with(itemView.context)
                    .load(Tools.getImage(itemView.context,
                        item.title
                            .replace(itemView.context.getString(R.string.goOnline), "wifi")
                            .toLowerCase(Locale.ROOT)
                            .replace(" ", "_")
                            .replace("&", "n")))
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.error)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .into(itemView.findViewById(R.id.quizThumbnail))

            }

            // OnClick
            itemView.setOnClickListener{
                onClick.onHomeItemClick(item, adapterPosition)

            }

        }

    }

}

interface OnHomeItemClickListener{
    fun onHomeItemClick(item: Category, position: Int)

}