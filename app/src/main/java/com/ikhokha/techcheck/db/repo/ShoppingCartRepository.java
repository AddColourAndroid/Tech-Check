package com.ikhokha.techcheck.db.repo;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.lifecycle.LiveData;

import com.ikhokha.techcheck.db.AppDatabase;
import com.ikhokha.techcheck.db.model.ShoppingCart;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ShoppingCartRepository {

    @SuppressLint("StaticFieldLeak")
    private static ShoppingCartRepository ourInstance;

    private final AppDatabase mDatabase;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private Context mContext;

    public static ShoppingCartRepository getInstance(Context context) {
        if (ourInstance == null) ourInstance = new ShoppingCartRepository(context);
        return ourInstance;
    }

    private ShoppingCartRepository(Context context) {
        mContext = context;
        mDatabase = AppDatabase.getInstance(context);
    }

    public void insertShoppingCart(ShoppingCart shoppingCart) {
        executor.execute(() -> mDatabase.shoppingCartDao().insertShoppingCart(shoppingCart));
    }

    public LiveData<ShoppingCart> getShoppingCart(String productUid) {
        return mDatabase.shoppingCartDao().getShoppingCart(productUid);
    }

    public LiveData<List<ShoppingCart>> getAllShoppingCart() {
        return mDatabase.shoppingCartDao().getAllShoppingCart();
    }

    public void deleteShoppingCart(String productUid) {
        executor.execute(() -> mDatabase.shoppingCartDao().deleteShoppingCart(productUid));
    }

    public void deleteAllShoppingCart() {
        executor.execute(() -> mDatabase.shoppingCartDao().deleteAllShoppingCart());
    }
}