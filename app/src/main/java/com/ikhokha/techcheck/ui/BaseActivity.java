package com.ikhokha.techcheck.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ikhokha.techcheck.R;
import com.ikhokha.techcheck.app.GlideApp;
import com.ikhokha.techcheck.databinding.ContentDialogAddBinding;
import com.ikhokha.techcheck.db.model.Products;
import com.ikhokha.techcheck.db.model.ShoppingCart;
import com.ikhokha.techcheck.ui.callback.ShoppingCartCallback;
import com.ikhokha.techcheck.ui.viewmodel.HomeViewModel;
import com.ikhokha.techcheck.ui.viewmodel.ShoppingCartViewModel;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.jetbrains.annotations.NotNull;

public abstract class BaseActivity extends AppCompatActivity
        implements ShoppingCartCallback {

    private static final int SETTINGS_REQUEST_CODE = 101;

    private HomeViewModel mViewModel;
    private ShoppingCartViewModel mCartViewModel;

    private int mQuantity, mScanQuantity;
    private BottomSheetDialog mBottomSheetDialog;

    private boolean isShoppingCartAdded = false;
    private ContentDialogAddBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermission();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        mCartViewModel = new ViewModelProvider(this).get(ShoppingCartViewModel.class);
    }

    public void getShoppingCart(String productUid, boolean isProductScan) {
        isShoppingCartAdded = true;
        mCartViewModel.getShoppingCart(productUid).observe(this, shoppingCart -> {
            if (isShoppingCartAdded) {
                mQuantity = 1;
                mScanQuantity = 0;
                if (isProductScan && shoppingCart != null) mScanQuantity = shoppingCart.quantity;
                else mQuantity = shoppingCart != null ? shoppingCart.quantity : 1;
                mViewModel.getProduct(productUid).observe(this, products -> {
                    if (isShoppingCartAdded && products != null) ShowShoppingCartDialog(products);
                });
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void ShowShoppingCartDialog(Products products) {
        if (mBottomSheetDialog != null && mBottomSheetDialog.isShowing())
            mBottomSheetDialog.dismiss();

        ShoppingCart shoppingCart = new ShoppingCart(products.productUid
                , products.description, products.image, products.price, mQuantity);

        mBottomSheetDialog = new BottomSheetDialog(this);
        binding = DataBindingUtil.inflate(LayoutInflater.from(this)
                , R.layout.content_dialog_add, null, false);
        mBottomSheetDialog.setContentView(binding.getRoot());

        binding.contentCart.imageViewDelete.setVisibility(View.GONE);
        binding.contentCart.lineDivider.setVisibility(View.GONE);
        binding.contentCart.setData(shoppingCart);

        binding.contentCart.setData(new ShoppingCart(products.productUid
                , products.description, products.image, products.price, mQuantity));
        binding.contentCart.setCallback(this);

        StorageReference storage = FirebaseStorage.getInstance().getReferenceFromUrl(products.image);
        GlideApp.with(this)
                .load(storage)
                .apply(new RequestOptions().transform(new CenterInside()
                        , new RoundedCorners(54)))
                .into(binding.contentCart.imageViewProduct);

        binding.btnAdd.setOnClickListener(view -> {
            isShoppingCartAdded = false;
            Toast.makeText(BaseActivity.this, getString(R.string.txt_item_added), Toast.LENGTH_SHORT).show();
            mCartViewModel.insertShoppingCart(new ShoppingCart(products.productUid
                    , products.description, products.image, products.price, (mQuantity + mScanQuantity)));
            mBottomSheetDialog.dismiss();
        });

        if (!isFinishing()) mBottomSheetDialog.show();
    }

    private void UpdateQuantityUI() {
        binding.contentCart.textViewQuantity.setText(String.valueOf(mQuantity));
    }

    @Override
    public void onClick(@NotNull ShoppingCart shoppingCart, int value) {
        if (value == 1) {
            if (mQuantity > 14)
                Toast.makeText(this, getResources().getString(R.string.txt_more_than)
                        , Toast.LENGTH_SHORT).show();
            else mQuantity++;
        } else if (value == 2) {
            if (mQuantity >= 2) mQuantity--;
            else
                Toast.makeText(this, getResources().getString(R.string.txt_less_than)
                        , Toast.LENGTH_SHORT).show();
        }
        shoppingCart.quantity = mQuantity;
        binding.contentCart.setData(shoppingCart);

        UpdateQuantityUI();
    }

    public boolean isPermissionGranted() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        // permission is granted, open the camera
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) showSettingsDialog();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
        builder.setTitle(R.string.txt_need_permission);
        builder.setMessage(R.string.txt_need_permission_message);
        builder.setPositiveButton(R.string.btn_go_to_settings, (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(R.string.btn_cancel, (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, SETTINGS_REQUEST_CODE);
    }
}