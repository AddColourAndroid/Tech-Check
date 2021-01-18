package com.ikhokha.techcheck.db.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.ikhokha.techcheck.db.converter.Converters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(tableName = "order_summary")
public class OrderSummary {

    @PrimaryKey(autoGenerate = true)
    public int id;
    @TypeConverters(Converters.class)
    public List<ShoppingCart> cartList = new ArrayList<>();
    public Date orderDate;

    @Ignore
    public OrderSummary() {
    }

    public OrderSummary(List<ShoppingCart> cartList, Date orderDate) {
        this.cartList = cartList;
        this.orderDate = orderDate;
    }

    @Ignore
    public OrderSummary(int id, ArrayList<ShoppingCart> cartList, Date orderDate) {
        this.id = id;
        this.cartList = cartList;
        this.orderDate = orderDate;
    }
}