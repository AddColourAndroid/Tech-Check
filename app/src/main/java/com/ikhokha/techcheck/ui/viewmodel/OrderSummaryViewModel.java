package com.ikhokha.techcheck.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ikhokha.techcheck.db.model.OrderSummary;
import com.ikhokha.techcheck.db.repo.OrderSummaryRepository;

import java.util.List;

public class OrderSummaryViewModel extends AndroidViewModel {

    private final OrderSummaryRepository orderSummaryRepository;

    public OrderSummaryViewModel(@NonNull Application application) {
        super(application);
        orderSummaryRepository = OrderSummaryRepository.getInstance(application.getApplicationContext());
    }

    public void insertOrderSummary(OrderSummary orderSummary) {
        orderSummaryRepository.insertOrderSummary(orderSummary);
    }

    public LiveData<List<OrderSummary>> getOrderSummary() {
        return orderSummaryRepository.getOrderSummary();
    }
}