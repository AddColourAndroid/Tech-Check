package com.ikhokha.techcheck.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ikhokha.techcheck.R;
import com.ikhokha.techcheck.databinding.ContentDialogConfirmBinding;
import com.ikhokha.techcheck.databinding.FragmentCartBinding;
import com.ikhokha.techcheck.databinding.RowItemSummaryBinding;
import com.ikhokha.techcheck.db.model.OrderSummary;
import com.ikhokha.techcheck.db.model.ShoppingCart;
import com.ikhokha.techcheck.helper.DateHelper;
import com.ikhokha.techcheck.ui.adapter.ShoppingCartAdapter;
import com.ikhokha.techcheck.ui.callback.OrderSummaryCallback;
import com.ikhokha.techcheck.ui.callback.ShoppingCartCallback;
import com.ikhokha.techcheck.ui.viewmodel.OrderSummaryViewModel;
import com.ikhokha.techcheck.ui.viewmodel.ShoppingCartViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CartFragment extends Fragment
        implements ShoppingCartCallback, OrderSummaryCallback {

    private FragmentCartBinding mBinding;
    private ShoppingCartViewModel mViewModel;
    private OrderSummaryViewModel mSummaryViewModel;

    public CartFragment() {
    }

    private ShoppingCartAdapter mAdapter;
    private final List<ShoppingCart> mList = new ArrayList<>();

    private boolean isProceed = false;
    private BottomSheetDialog mBottomSheetDialog;
    private RowItemSummaryBinding binding;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(ShoppingCartViewModel.class);
        mViewModel.getAllShoppingCart().observe(getViewLifecycleOwner(), shoppingCarts -> {
            mList.clear();
            if (shoppingCarts != null && shoppingCarts.size() != 0) {
                mList.addAll(shoppingCarts);
                mAdapter.notifyDataSetChanged();
            }
            UpdateUI();
        });

        mSummaryViewModel = new ViewModelProvider(this).get(OrderSummaryViewModel.class);
        mSummaryViewModel.getOrderSummary().observe(getViewLifecycleOwner(), orderSummaries -> {
            if (orderSummaries != null && orderSummaries.size() != 0) {
                if (isProceed) ShowOrderSummaryDialog(orderSummaries);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart, container, false);
        mBinding.setLifecycleOwner(this);

        initialize();
        return mBinding.getRoot();
    }

    private void initialize() {
        if (getActivity() != null) {
            mAdapter = new ShoppingCartAdapter(this);
            mAdapter.setList(mList);

            mBinding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1
                    , StaggeredGridLayoutManager.VERTICAL));
            mBinding.recyclerView.setAdapter(mAdapter);

            mBinding.btnProceed.setOnClickListener(view -> {
                if (mList.size() != 0) {
                    isProceed = true;
                    mSummaryViewModel.insertOrderSummary(new OrderSummary(mList, new Date()));
                    mViewModel.deleteAllShoppingCart();
                }
            });
        }
    }

    @SuppressLint("SetTextI18n")
    private void UpdateUI() {
        if (getActivity() != null) {
            double mTotal = 0;
            for (ShoppingCart cart : mList) mTotal += (cart.price * cart.quantity);
            mBinding.textViewItemCount.setText(mList.size() + " Items");
            mBinding.textViewTotal.setText("R " + mTotal);
            mBinding.setIsShoppingCart(mList.size() != 0);
        }
    }

    @Override
    public void onClick(@NotNull ShoppingCart shoppingCart, int value) {
        if (value == 1) {
            shoppingCart.quantity += 1;
            mViewModel.insertShoppingCart(shoppingCart);
        } else if (value == 2) {
            if (shoppingCart.quantity <= 1) ShowDeleteItemDialog(shoppingCart.productUid);
            else {
                shoppingCart.quantity -= 1;
                mViewModel.insertShoppingCart(shoppingCart);
            }
        } else if (value == 3) ShowDeleteItemDialog(shoppingCart.productUid);
    }

    private void ShowDeleteItemDialog(String productUid) {
        if (getActivity() != null) {
            if (mBottomSheetDialog != null && mBottomSheetDialog.isShowing())
                mBottomSheetDialog.dismiss();

            mBottomSheetDialog = new BottomSheetDialog(getActivity());
            ContentDialogConfirmBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getActivity())
                    , R.layout.content_dialog_confirm, null, false);
            mBottomSheetDialog.setContentView(binding.getRoot());

            binding.btnCancel.setOnClickListener(view -> mBottomSheetDialog.dismiss());
            binding.btnDelete.setOnClickListener(view -> {
                Toast.makeText(getActivity(), getActivity().getString(R.string.txt_item_deleted_successfully), Toast.LENGTH_LONG).show();
                mViewModel.deleteShoppingCart(productUid);
                mBottomSheetDialog.dismiss();
            });
            if (getActivity() != null) if (!getActivity().isFinishing()) mBottomSheetDialog.show();
        }
    }

    @SuppressLint("SetTextI18n")
    private void ShowOrderSummaryDialog(List<OrderSummary> orderSummaries) {
        if (getActivity() != null) {
            isProceed = false;
            if (mBottomSheetDialog != null && mBottomSheetDialog.isShowing())
                mBottomSheetDialog.dismiss();

            mBottomSheetDialog = new BottomSheetDialog(getActivity());
            binding = DataBindingUtil.inflate(LayoutInflater.from(getActivity())
                    , R.layout.row_item_summary, null, false);
            mBottomSheetDialog.setContentView(binding.getRoot());

            OrderSummary summary = orderSummaries.get(orderSummaries.size() - 1);

            binding.textViewOrderNumber.setText("Order No: " + summary.id);
            binding.textViewDate.setText("Date: " + DateHelper.INSTANCE.dateFormat(summary.orderDate.getTime()));

            double mTotal = 0;
            StringBuilder mItem = new StringBuilder();
            StringBuilder mItemPrice = new StringBuilder();

            for (ShoppingCart shoppingCart : summary.cartList) {
                mTotal += (shoppingCart.price * shoppingCart.quantity);
                mItem.append(shoppingCart.description).append("\n");
                mItemPrice.append("R ").append(shoppingCart.price).append("\n");
            }

            binding.setData(summary);
            binding.setCallback(this);
            binding.textViewItem.setText(mItem);
            binding.textViewPrice.setText(mItemPrice);
            binding.textViewTotal.setText("R " + mTotal);

            binding.lineDividerBottom.setVisibility(View.GONE);
            if (getActivity() != null) if (!getActivity().isFinishing()) mBottomSheetDialog.show();
        }
    }

    @Override
    public void onClick(@NotNull OrderSummary orderSummary) {
        StringBuilder builderShare = new StringBuilder();
        builderShare.append(binding.textViewOrderNumber.getText()).append("\n")
                .append(binding.textViewDate.getText()).append("\n\n");
        for (ShoppingCart shoppingCart : orderSummary.cartList) {
            builderShare.append(shoppingCart.description).append(": R ")
                    .append(shoppingCart.price).append("\n");
        }
        builderShare.append("\n").append("Total: ").append(binding.textViewTotal.getText());

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, builderShare.toString());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
        mBottomSheetDialog.dismiss();
    }
}