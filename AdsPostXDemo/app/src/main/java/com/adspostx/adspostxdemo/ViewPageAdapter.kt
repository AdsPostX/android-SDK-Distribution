package com.adspostx.adspostxdemo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ViewPagerAdapter (private var offers: MutableList<Offer>, private var delegate: Activity): RecyclerView.Adapter<ViewPagerAdapter.Pager2ViewHolder>(){

    inner class Pager2ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val offerTitle: TextView = itemView.findViewById(R.id.textOfferTitle)
        val offerImageView: ImageView = itemView.findViewById(R.id.imageOffer)
        val offerDescription: TextView = itemView.findViewById(R.id.textOfferDescription)
        val ctaYes: Button = itemView.findViewById(R.id.buttonCTAYes)
        val ctaNo: Button = itemView.findViewById(R.id.buttonCTANo)
        val pixel: ImageView = itemView.findViewById(R.id.pixel)

        init {
            ctaYes.setOnClickListener {
                val nativeUIActivity = delegate as? NativeUIActivity
                if (nativeUIActivity != null) {
                    nativeUIActivity.getCurrentURL()
                }
            }
            ctaNo.setOnClickListener {
                val nativeUIActivity = delegate as? NativeUIActivity
                if (nativeUIActivity != null) {
                    nativeUIActivity.goToNextOffer()
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Pager2ViewHolder {
    return Pager2ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_page,parent,false))
    }

    override fun onViewAttachedToWindow(holder: Pager2ViewHolder) {
        super.onViewAttachedToWindow(holder)
        val index = holder.layoutPosition

        if(offers[index].pixel != null) {
            Glide.with(holder.itemView).load(offers[index].pixel).into(holder.pixel);
        }
    }

    override fun onBindViewHolder(holder: Pager2ViewHolder, position: Int) {
        holder.offerTitle.text = offers[position].title
        holder.offerDescription.text = offers[position].description
        holder.ctaYes.text = offers[position].cta_yes
        holder.ctaNo.text = offers[position].cta_no

        if(offers[position].image != null) {
            Glide.with(holder.itemView).load(offers[position].image).placeholder(R.drawable.loading_image).into(holder.offerImageView);
        }

    }

    override fun getItemCount(): Int {
        return  offers.size
    }
}