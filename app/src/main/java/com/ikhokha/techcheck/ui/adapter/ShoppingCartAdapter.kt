package com.ikhokha.techcheck.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.firebase.storage.FirebaseStorage
import com.ikhokha.techcheck.R
import com.ikhokha.techcheck.app.GlideApp
import com.ikhokha.techcheck.databinding.RowItemCartBinding
import com.ikhokha.techcheck.db.model.ShoppingCart
import com.ikhokha.techcheck.ui.callback.ShoppingCartCallback
import com.ikhokha.techcheck.ui.viewholder.ShoppingCartViewHolder

class ShoppingCartAdapter(private val mCallback: ShoppingCartCallback?)
    : RecyclerView.Adapter<ShoppingCartViewHolder>() {

    private var mList: List<ShoppingCart>? = null

    fun setList(list: List<ShoppingCart>) {
        if (mList == null) {
            mList = list
            notifyItemRangeInserted(0, list.size)
        } else {
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return mList!!.size
                }

                override fun getNewListSize(): Int {
                    return list.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return mList!![oldItemPosition].productUid == list[newItemPosition].productUid
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val newList = list[newItemPosition]
                    val oldList = mList!![oldItemPosition]
                    return newList.quantity == oldList.quantity && newList.productUid == oldList.productUid
                }
            })
            mList = list
            result.dispatchUpdatesTo(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingCartViewHolder {
        val binding = DataBindingUtil
                .inflate<RowItemCartBinding>(LayoutInflater.from(parent.context), R.layout.row_item_cart,
                        parent, false)
        binding.callback = mCallback
        return ShoppingCartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShoppingCartViewHolder, position: Int) {
        holder.mBinding.data = mList!![position]

        val storage = FirebaseStorage.getInstance()
        val gsReference = storage.getReferenceFromUrl(mList!![position].image)

        GlideApp.with(holder.mBinding.imageViewProduct.context)
                .load(gsReference)
                .transform(CenterInside(), RoundedCorners(24))
                .into(holder.mBinding.imageViewProduct)

        if (position == itemCount - 1) holder.mBinding.lineDivider.visibility = View.GONE

        holder.mBinding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return if (mList == null) 0 else mList!!.size
    }
}