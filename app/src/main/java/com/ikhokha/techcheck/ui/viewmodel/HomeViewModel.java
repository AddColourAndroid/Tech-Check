package com.ikhokha.techcheck.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ikhokha.techcheck.db.model.Products;
import com.ikhokha.techcheck.db.repo.HomeRepository;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private final HomeRepository homeRepository;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        homeRepository = HomeRepository.getInstance(application.getApplicationContext());
    }

    public LiveData<Products> getProduct(String productUid) {
        return homeRepository.getProduct(productUid);
    }

    public LiveData<List<Products>> getAllProduct() {
        return homeRepository.getAllProduct();
    }
}