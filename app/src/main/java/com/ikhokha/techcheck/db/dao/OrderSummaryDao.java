package com.ikhokha.techcheck.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ikhokha.techcheck.db.model.OrderSummary;

import java.util.List;

@Dao
public interface OrderSummaryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrderSummary(OrderSummary orderSummary);

    @Query("SELECT * FROM order_summary")
    LiveData<List<OrderSummary>> getOrderSummary();
}