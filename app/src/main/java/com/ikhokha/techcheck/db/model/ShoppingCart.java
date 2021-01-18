package com.ikhokha.techcheck.db.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "shopping_cart")
public class ShoppingCart {

    @NonNull
    @PrimaryKey
    public String productUid;
    public String description;
    public String image;
    public double price;
    public int quantity;

    @Ignore
    public ShoppingCart() {
    }

    public ShoppingCart(@NonNull String productUid, String description, String image, double price, int quantity) {
        this.productUid = productUid;
        this.description = description;
        this.image = image;
        this.price = price;
        this.quantity = quantity;
    }
}