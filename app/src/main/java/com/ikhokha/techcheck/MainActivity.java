package com.ikhokha.techcheck;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.ikhokha.techcheck.app.Config;
import com.ikhokha.techcheck.databinding.ActivityMainBinding;
import com.ikhokha.techcheck.db.model.ShoppingCart;
import com.ikhokha.techcheck.ui.BaseActivity;
import com.ikhokha.techcheck.ui.viewmodel.ShoppingCartViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding mBinding;

    private boolean isScanButtonClicked = false;
    private final List<ShoppingCart> mList = new ArrayList<>();

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

        navView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        final int previousItem = navView.getSelectedItemId();
                        final int nextItem = item.getItemId();
                        if (previousItem != nextItem) {
                            switch (nextItem) {
                                case R.id.navigation_home:
                                    BadgeDrawable badge = navView.getOrCreateBadge(nextItem);
//            badge.setVisible(true);

//            menuItemId = bn.getMenu().getItem(1).getItemId();
//            // A badge with the text "99" will be displayed.
//            badge = bn.getOrCreateBadge(menuItemId);
                                    badge.setVisible(true);
                                    badge.setNumber(99);
                                    break;
                            }
                        }
                        return true;
                    }
                }
        );
//            int menuItemId = navView.getMenu().getItem(0).getItemId();
//            // An icon only badge will be displayed.
//            BadgeDrawable badge = navView.getOrCreateBadge(menuItemId);
////            badge.setVisible(true);
//
////            menuItemId = bn.getMenu().getItem(1).getItemId();
////            // A badge with the text "99" will be displayed.
////            badge = bn.getOrCreateBadge(menuItemId);
//            badge.setVisible(true);
//            badge.setNumber(99);



        initialize();
    }

    private int getSelectedItem(BottomNavigationView bottomNavigationView) {
        Menu menu = bottomNavigationView.getMenu();
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            if (menuItem.isChecked()) {
                return menuItem.getItemId();
            }
        }
        return 0;
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

        ShoppingCartViewModel mViewModel = new ViewModelProvider(this).get(ShoppingCartViewModel.class);
        mViewModel.getAllShoppingCart().observe(this, shoppingCarts -> {
            mList.clear();
            if (shoppingCarts != null && shoppingCarts.size() != 0) mList.addAll(shoppingCarts);
            UpdateUI();
        });
    }

    @SuppressLint("SetTextI18n")
    private void UpdateUI() {
        int mQuantity = 0;
        double mTotalCost = 0;
        for (ShoppingCart cart : mList) {
            mQuantity += cart.quantity;
            mTotalCost += (cart.price * cart.quantity);
        }
    }
}