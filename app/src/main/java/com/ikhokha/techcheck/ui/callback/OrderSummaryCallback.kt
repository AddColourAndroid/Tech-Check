package com.ikhokha.techcheck.ui.callback

import com.ikhokha.techcheck.db.model.OrderSummary

interface OrderSummaryCallback {
    fun onClick(orderSummary: OrderSummary)
}