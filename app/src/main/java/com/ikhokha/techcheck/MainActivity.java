package com.ikhokha.techcheck;

import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.ikhokha.techcheck.app.Config;
import com.ikhokha.techcheck.databinding.ActivityMainBinding;
import com.ikhokha.techcheck.ui.BaseActivity;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding mBinding;

    private boolean isScanButtonClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.setLifecycleOwner(this);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_cart, R.id.navigation_history)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        initialize();
    }

    private void initialize() {
        mBinding.contentToolbar.layoutBack.setOnClickListener(view -> onBackPressed());
        mBinding.contentToolbar.layoutScan.setOnClickListener(view -> {
            if (isPermissionGranted() && !isScanButtonClicked) {
                isScanButtonClicked = true;
                startActivity(new Intent(MainActivity.this, BarcodeActivity.class));
            } else requestPermission();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isScanButtonClicked = false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            mAuth.signInWithEmailAndPassword(Config.EMAIL, Config.PASSWORD)
                    .addOnCompleteListener(this, task -> {
                        if (!task.isSuccessful()) Toast.makeText(MainActivity.this,
                                this.getString(R.string.error_authentication_failed),
                                Toast.LENGTH_SHORT).show();
                    });
        }
    }
}