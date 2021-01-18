package com.ikhokha.techcheck.db.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ikhokha.techcheck.db.model.ShoppingCart
import java.util.*

object Converters {
    @JvmStatic
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @JvmStatic
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @JvmStatic
    @TypeConverter
    fun ShoppingCartToString(list: List<ShoppingCart?>?): String {
        val gson = Gson()
        return gson.toJson(list)
    }

    @JvmStatic
    @TypeConverter
    fun stringToShoppingCartList(data: String?): List<ShoppingCart> {
        return if (data == null) emptyList() else Gson().fromJson(data, object : TypeToken<List<ShoppingCart?>?>() {}.type)
    }
}