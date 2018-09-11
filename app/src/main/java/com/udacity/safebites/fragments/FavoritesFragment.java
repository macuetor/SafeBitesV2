package com.udacity.safebites.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.udacity.safebites.R;
import com.udacity.safebites.adapter.ProductAdapter;
import com.udacity.safebites.entities.Product;
import com.udacity.safebites.utils.DesignUtils;
import com.udacity.safebites.utils.QueryUtils;
import com.udacity.safebites.widget.CollectionAppWidgetProvider;

import java.util.List;
import java.util.Objects;

public class FavoritesFragment extends Fragment {
    /**
     * Product adapter for the RecyclerView.
     */
    private static ProductAdapter sProductAdapter;

    /**
     * RecyclerView of favorite products.
     */
    private static RecyclerView sFavoriteRecyclerView;

    /**
     * TextView that is displayed when the list of products is empty.
     */
    private static TextView sEmptyTextView;

    /**
     * Toolbar of fragments container activity.
     */
    private Toolbar mToolbar;

    public static List<Product> fillFavoriteProductsRecyclerView(Context context) {
        List<Product> favoriteProducts = QueryUtils.loadFavoriteProducts(context);
        sProductAdapter = new ProductAdapter(context, favoriteProducts);
        sFavoriteRecyclerView.setAdapter(sProductAdapter);
        CollectionAppWidgetProvider.sendRefreshBroadcast(context);

        if (favoriteProducts.isEmpty()) {
            sFavoriteRecyclerView.setVisibility(View.GONE);
            sEmptyTextView.setVisibility(View.VISIBLE);
        } else {
            sEmptyTextView.setVisibility(View.GONE);
            sFavoriteRecyclerView.setVisibility(View.VISIBLE);
        }

        return favoriteProducts;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        setHasOptionsMenu(true);
        mToolbar = Objects.requireNonNull(getActivity()).findViewById(R.id.toolbar);

        sEmptyTextView = view.findViewById(R.id.empty_textView);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());
        sFavoriteRecyclerView = view.findViewById(R.id.favorite_products_recycler);
        sFavoriteRecyclerView.setLayoutManager(mLinearLayoutManager);
        sProductAdapter = new ProductAdapter(getContext(), fillFavoriteProductsRecyclerView(Objects.requireNonNull(getContext())));
        sFavoriteRecyclerView.setAdapter(sProductAdapter);

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mProductsDatabaseReference = mFirebaseDatabase.getReference().child("products");
        List<Product> mFavoriteProducts = FavoritesFragment.fillFavoriteProductsRecyclerView(getContext());
        for (int i = 0; i < mFavoriteProducts.size(); i++) {
            mProductsDatabaseReference.push().setValue(mFavoriteProducts.get(i));
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        sProductAdapter = new ProductAdapter(getContext(), fillFavoriteProductsRecyclerView(Objects.requireNonNull(getContext())));
        sFavoriteRecyclerView.setAdapter(sProductAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        mToolbar.findViewById(R.id.search_edit_text).setVisibility(View.GONE);
        DesignUtils.hideSoftKeyboard(Objects.requireNonNull(getActivity()));
    }
}