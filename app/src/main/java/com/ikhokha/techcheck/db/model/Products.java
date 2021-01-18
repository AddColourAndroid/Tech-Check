package com.ikhokha.techcheck.db.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "products")
public class Products {

    @NonNull
    @PrimaryKey
    public String productUid;
    public String description;
    public String image;
    public double price;

    @Ignore
    public Products() {
    }

    public Products(@NotNull String productUid, String description, String image, double price) {
        this.productUid = productUid;
        this.description = description;
        this.image = image;
        this.price = price;
    }
}