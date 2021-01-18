package com.ikhokha.techcheck.db

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ikhokha.techcheck.db.converter.Converters
import com.ikhokha.techcheck.db.dao.OrderSummaryDao
import com.ikhokha.techcheck.db.dao.ProductsDao
import com.ikhokha.techcheck.db.dao.ShoppingCartDao
import com.ikhokha.techcheck.db.model.OrderSummary
import com.ikhokha.techcheck.db.model.Products
import com.ikhokha.techcheck.db.model.ShoppingCart

@Database(entities = [Products::class, ShoppingCart::class, OrderSummary::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productsDao(): ProductsDao?
    abstract fun shoppingCartDao(): ShoppingCartDao?
    abstract fun orderSummaryDao(): OrderSummaryDao?

    companion object {
        private var sInstance: AppDatabase? = null

        @VisibleForTesting
        private val DATABASE_NAME = "tech-check-room-version"
        private val LOCK = Any()
        @JvmStatic
        fun getInstance(context: Context): AppDatabase? {
            if (sInstance == null) {
                synchronized(LOCK) {
                    if (sInstance == null) {
                        sInstance = Room.databaseBuilder(context.applicationContext,
                                AppDatabase::class.java, DATABASE_NAME).build()
                    }
                }
            }
            return sInstance
        }
    }
}