package com.ikhokha.techcheck.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ikhokha.techcheck.R
import com.ikhokha.techcheck.databinding.RowItemSummaryBinding
import com.ikhokha.techcheck.db.model.OrderSummary
import com.ikhokha.techcheck.helper.DateHelper.dateFormat
import com.ikhokha.techcheck.ui.callback.OrderSummaryCallback
import com.ikhokha.techcheck.ui.viewholder.OrderSummaryViewHolder

class OrderSummaryCartAdapter(private val mCallback: OrderSummaryCallback?)
    : RecyclerView.Adapter<OrderSummaryViewHolder>() {

    private var mList: List<OrderSummary>? = null

    fun setList(list: List<OrderSummary>) {
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
                    return mList!![oldItemPosition].id == list[newItemPosition].id
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val newList = list[newItemPosition]
                    val oldList = mList!![oldItemPosition]
                    return newList.id == oldList.id
                }
            })
            mList = list
            result.dispatchUpdatesTo(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderSummaryViewHolder {
        val binding = DataBindingUtil
                .inflate<RowItemSummaryBinding>(LayoutInflater.from(parent.context), R.layout.row_item_summary,
                        parent, false)
        binding.callback = mCallback
        return OrderSummaryViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderSummaryViewHolder, position: Int) {
        holder.mBinding.data = mList!![position]

        val summary: OrderSummary = mList!![position]

        holder.mBinding.textViewOrderNumber.text = "Order No: " + summary.id
        holder.mBinding.textViewDate.text = "Date: " + dateFormat(summary.orderDate.time)

        var mTotal = 0.0
        val mItem = StringBuilder()
        val mItemPrice = StringBuilder()

        var count = 1
        for (shoppingCart in summary.cartList) {
            if (count <= 3) {
                mTotal += shoppingCart.price
                mItem.append(shoppingCart.description).append("\n")
                mItemPrice.append("R ").append(shoppingCart.price).append("\n")

                if (count == 3) mItem.append("...").append("\n")
            }
            count++
        }

        holder.mBinding.textViewItem.text = mItem
        holder.mBinding.textViewPrice.text = mItemPrice
        holder.mBinding.textViewTotal.text = "R $mTotal"

        if (position == itemCount - 1) holder.mBinding.lineDividerBottom.visibility = View.GONE

        holder.mBinding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return if (mList == null) 0 else mList!!.size
    }
}