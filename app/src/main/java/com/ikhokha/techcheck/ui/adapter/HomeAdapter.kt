package com.ikhokha.techcheck.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.firebase.storage.FirebaseStorage
import com.ikhokha.techcheck.R
import com.ikhokha.techcheck.app.GlideApp
import com.ikhokha.techcheck.databinding.RowItemHomeBinding
import com.ikhokha.techcheck.db.model.Products
import com.ikhokha.techcheck.ui.callback.HomeCallback
import com.ikhokha.techcheck.ui.viewholder.HomeViewHolder

class HomeAdapter(private val mHomeCallback: HomeCallback?)
    : RecyclerView.Adapter<HomeViewHolder>() {

    private var mList: List<Products>? = null

    fun setList(list: List<Products>) {
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
                    return newList.productUid == oldList.productUid
                }
            })
            mList = list
            result.dispatchUpdatesTo(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = DataBindingUtil
                .inflate<RowItemHomeBinding>(LayoutInflater.from(parent.context), R.layout.row_item_home,
                        parent, false)
        binding.callback = mHomeCallback
        return HomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.mBinding.data = mList!![position]

        val storage = FirebaseStorage.getInstance()
        val gsReference = storage.getReferenceFromUrl(mList!![position].image)

        GlideApp.with(holder.mBinding.imageViewProduct.context)
                .load(gsReference)
                .transform(CenterInside(), RoundedCorners(24))
                .into(holder.mBinding.imageViewProduct)

        holder.mBinding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return if (mList == null) 0 else mList!!.size
    }
}