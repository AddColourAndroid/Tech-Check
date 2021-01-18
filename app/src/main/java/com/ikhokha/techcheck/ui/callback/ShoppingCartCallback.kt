package com.ikhokha.techcheck.ui.callback

import com.ikhokha.techcheck.db.model.ShoppingCart

interface ShoppingCartCallback {
    fun onClick(shoppingCart: ShoppingCart, value: Int)
}