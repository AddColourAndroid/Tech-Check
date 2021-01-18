package com.ikhokha.techcheck.ui.fragment;

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
import com.ikhokha.techcheck.databinding.FragmentHomeBinding;
import com.ikhokha.techcheck.db.model.Products;
import com.ikhokha.techcheck.ui.BaseActivity;
import com.ikhokha.techcheck.ui.adapter.HomeAdapter;
import com.ikhokha.techcheck.ui.callback.HomeCallback;
import com.ikhokha.techcheck.ui.viewmodel.HomeViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment
        implements HomeCallback {

    private FragmentHomeBinding mBinding;

    public HomeFragment() {
    }

    private HomeAdapter mAdapter;
    private final List<Products> mList = new ArrayList<>();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        HomeViewModel mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        mViewModel.getAllProduct().observe(getViewLifecycleOwner(), productsList -> {
            if (productsList != null && productsList.size() != 0) {
                mList.clear();
                mList.addAll(productsList);
                mAdapter.notifyDataSetChanged();
            }
            mBinding.setIsLoading(mList.size() != 0);
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        mBinding.setLifecycleOwner(this);

        initialize();
        return mBinding.getRoot();
    }

    private void initialize() {
        if (getActivity() != null) {

            mAdapter = new HomeAdapter(this);
            mAdapter.setList(mList);

            mBinding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2
                    , StaggeredGridLayoutManager.VERTICAL));
            mBinding.recyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onClick(@NotNull Products products) {
        if (getActivity() != null)
            ((BaseActivity) getActivity()).getShoppingCart(products.productUid, false);
    }
}