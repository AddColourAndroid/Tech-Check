package com.ikhokha.techcheck.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ikhokha.techcheck.db.model.ShoppingCart;
import com.ikhokha.techcheck.db.repo.ShoppingCartRepository;

import java.util.List;

public class ShoppingCartViewModel extends AndroidViewModel {

    private final ShoppingCartRepository shoppingCartRepository;

    public ShoppingCartViewModel(@NonNull Application application) {
        super(application);
        shoppingCartRepository = ShoppingCartRepository.getInstance(application.getApplicationContext());
    }

    public void insertShoppingCart(ShoppingCart shoppingCart) {
        shoppingCartRepository.insertShoppingCart(shoppingCart);
    }

    public LiveData<ShoppingCart> getShoppingCart(String productUid) {
        return shoppingCartRepository.getShoppingCart(productUid);
    }

    public LiveData<List<ShoppingCart>> getAllShoppingCart() {
        return shoppingCartRepository.getAllShoppingCart();
    }

    public void deleteShoppingCart(String productUid) {
        shoppingCartRepository.deleteShoppingCart(productUid);
    }

    public void deleteAllShoppingCart() {
        shoppingCartRepository.deleteAllShoppingCart();
    }
}