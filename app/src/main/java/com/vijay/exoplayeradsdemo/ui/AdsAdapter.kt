package com.vijay.exoplayeradsdemo.ui

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView.ScaleType.CENTER_INSIDE
import android.widget.ImageView.ScaleType.FIT_XY
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vijay.exoplayeradsdemo.data.AdsModel
import com.vijay.exoplayeradsdemo.databinding.AdsListItemBinding

class AdsAdapter(private val listener: OnItemClicked,
private val context: Context) :
  RecyclerView.Adapter<AdsAdapter.AdsViewHolder>() {

  private lateinit var binding: AdsListItemBinding

  inner class AdsViewHolder(private val binding: AdsListItemBinding) : RecyclerView.ViewHolder(
    binding.root) {
    init {
      binding.actionBtn.setOnClickListener {
        listener.onItemClicked(differ.currentList[bindingAdapterPosition])
      }
    }

    fun bindDataToHolder(ad: AdsModel?) {
      binding.apply {
        adIv.scaleType = FIT_XY
        Glide.with(context)
          .load(ad?.imageUrl)
          .override(150, 150)
          .into(adIv)
        titleTv.text = ad?.title
        salePriceTv.text = "Rs. ${ad?.discountPrice}"
        actualPriceTv.text = "RS. ${ad?.actualPrice}"
        actualPriceTv.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        discountTv.text = "(${ad?.discountPercent}% off)"
        actionBtn.text = ad?.buttonText
      }
    }
  }

  private val differCallback = object : DiffUtil.ItemCallback<AdsModel>() {
    override fun areItemsTheSame(
      oldItem: AdsModel,
      newItem: AdsModel,
    ): Boolean =
      oldItem.title == newItem.title

    override fun areContentsTheSame(
      oldItem: AdsModel,
      newItem: AdsModel,
    ): Boolean =
      oldItem == newItem
  }

  val differ = AsyncListDiffer(this, differCallback)
  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int,
  ): AdsViewHolder {
    binding = AdsListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return AdsViewHolder(binding)
  }

  override fun onBindViewHolder(
    holder: AdsViewHolder,
    position: Int,
  ) {
    holder.bindDataToHolder(differ.currentList[position])
  }

  override fun getItemCount() = differ.currentList.size

  interface OnItemClicked{
    fun onItemClicked(ad: AdsModel?)
  }
}