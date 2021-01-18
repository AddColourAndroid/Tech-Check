package com.ikhokha.techcheck.db.repo;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.lifecycle.LiveData;

import com.ikhokha.techcheck.db.AppDatabase;
import com.ikhokha.techcheck.db.model.OrderSummary;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class OrderSummaryRepository {

    @SuppressLint("StaticFieldLeak")
    private static OrderSummaryRepository ourInstance;

    private final AppDatabase mDatabase;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private Context mContext;

    public static OrderSummaryRepository getInstance(Context context) {
        if (ourInstance == null) ourInstance = new OrderSummaryRepository(context);
        return ourInstance;
    }

    private OrderSummaryRepository(Context context) {
        mContext = context;
        mDatabase = AppDatabase.getInstance(context);
    }

    public void insertOrderSummary(OrderSummary orderSummary) {
        executor.execute(() -> mDatabase.orderSummaryDao().insertOrderSummary(orderSummary));
    }

    public LiveData<List<OrderSummary>> getOrderSummary() {
        return mDatabase.orderSummaryDao().getOrderSummary();
    }
}