package com.ikhokha.techcheck.db.repo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ikhokha.techcheck.app.Config;
import com.ikhokha.techcheck.db.AppDatabase;
import com.ikhokha.techcheck.db.model.Products;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HomeRepository {

    @SuppressLint("StaticFieldLeak")
    private static HomeRepository ourInstance;

    private final AppDatabase mDatabase;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private Context mContext;
    private final FirebaseAuth mAuth;
    private final DatabaseReference mFirebaseDatabase;

    public static HomeRepository getInstance(Context context) {
        if (ourInstance == null) ourInstance = new HomeRepository(context);
        return ourInstance;
    }

    private HomeRepository(Context context) {
        mContext = context;
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase = AppDatabase.getInstance(context);
        loadAllProducts();
    }

    public void loadAllProducts() {
        if (mAuth.getCurrentUser() != null) {
            mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot dataSnapshot) {

                    executor.execute(() -> mDatabase.productsDao().deleteAllProduct());
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        String productId = String.valueOf(snapshot.getKey());
                        String description = String.valueOf(snapshot.child("description").getValue());
                        String image = Config.IMAGE_URL + snapshot.child("image").getValue();
                        String price = String.valueOf(snapshot.child("price").getValue());

                        executor.execute(() -> mDatabase.productsDao()
                                .insertProduct(new Products(productId, description, image
                                        , TextUtils.isEmpty(price) ? 0.0 : Double.parseDouble(price))));
                    }
                }

                @Override
                public void onCancelled(@NotNull DatabaseError databaseError) {

                }
            });
        }
    }

    public LiveData<Products> getProduct(String productUid) {
        return mDatabase.productsDao().getProduct(productUid);
    }

    public LiveData<List<Products>> getAllProduct() {
        return mDatabase.productsDao().getAllProduct();
    }
}