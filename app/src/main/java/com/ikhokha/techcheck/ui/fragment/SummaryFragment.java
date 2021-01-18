package com.ikhokha.techcheck.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.ikhokha.techcheck.R;
import com.ikhokha.techcheck.databinding.FragmentSummaryBinding;
import com.ikhokha.techcheck.db.model.OrderSummary;
import com.ikhokha.techcheck.db.model.ShoppingCart;
import com.ikhokha.techcheck.helper.DateHelper;
import com.ikhokha.techcheck.ui.adapter.OrderSummaryCartAdapter;
import com.ikhokha.techcheck.ui.callback.OrderSummaryCallback;
import com.ikhokha.techcheck.ui.viewmodel.OrderSummaryViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SummaryFragment extends Fragment
        implements OrderSummaryCallback {

    private FragmentSummaryBinding mBinding;

    public SummaryFragment() {
    }

    private OrderSummaryCartAdapter mAdapter;
    private final List<OrderSummary> mList = new ArrayList<>();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        OrderSummaryViewModel mViewModel = new ViewModelProvider(this).get(OrderSummaryViewModel.class);
        mViewModel.getOrderSummary().observe(getViewLifecycleOwner(), orderSummaries -> {
            mList.clear();
            if (orderSummaries != null && orderSummaries.size() != 0) {
                mList.addAll(orderSummaries);
                mAdapter.notifyDataSetChanged();
            }
            mBinding.setIsShoppingCart(mList.size() != 0);
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_summary, container, false);
        mBinding.setLifecycleOwner(this);

        initialize();
        return mBinding.getRoot();
    }

    private void initialize() {
        if (getActivity() != null) {

            mAdapter = new OrderSummaryCartAdapter(this);
            mAdapter.setList(mList);

            mBinding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1
                    , StaggeredGridLayoutManager.VERTICAL));
            mBinding.recyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onClick(@NotNull OrderSummary orderSummary) {
        StringBuilder builderShare = new StringBuilder();
        builderShare.append("Order No: ").append(orderSummary.id).append("\n")
                .append("Date: ").append(DateHelper.INSTANCE.dateFormat(orderSummary.orderDate.getTime()))
                .append("\n\n");

        double mTotal = 0;
        for (ShoppingCart shoppingCart : orderSummary.cartList) {
            mTotal += shoppingCart.price;
            builderShare.append(shoppingCart.description).append(": R ")
                    .append(shoppingCart.price).append("\n");
        }
        builderShare.append("\n").append("Total: ").append("R ").append(mTotal);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, builderShare.toString());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }
}