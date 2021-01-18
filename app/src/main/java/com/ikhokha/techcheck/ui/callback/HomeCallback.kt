package com.ikhokha.techcheck.ui.callback

import com.ikhokha.techcheck.db.model.Products

interface HomeCallback {
    fun onClick(products: Products)
}