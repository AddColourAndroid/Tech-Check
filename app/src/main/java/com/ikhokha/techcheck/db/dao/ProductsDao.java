package com.ikhokha.techcheck.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ikhokha.techcheck.db.model.Products;

import java.util.List;

@Dao
public interface ProductsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProduct(Products productsList);

    @Query("SELECT * FROM products WHERE productUid = :productUid")
    LiveData<Products> getProduct(String productUid);

    @Query("SELECT * FROM products")
    LiveData<List<Products>> getAllProduct();

    @Query("DELETE FROM products")
    void deleteAllProduct();
}