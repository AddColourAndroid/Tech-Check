package com.ikhokha.techcheck.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ikhokha.techcheck.db.model.ShoppingCart;

import java.util.List;

@Dao
public interface ShoppingCartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertShoppingCart(ShoppingCart shoppingCart);

    @Query("SELECT * FROM shopping_cart WHERE productUid = :productUid")
    LiveData<ShoppingCart> getShoppingCart(String productUid);

    @Query("SELECT * FROM shopping_cart ORDER BY description ASC")
    LiveData<List<ShoppingCart>> getAllShoppingCart();

    @Query("DELETE FROM shopping_cart WHERE productUid = :productUid")
    void deleteShoppingCart(String productUid);

    @Query("DELETE FROM shopping_cart")
    void deleteAllShoppingCart();
}