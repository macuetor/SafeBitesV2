package com.udacity.safebites.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.udacity.safebites.R;
import com.udacity.safebites.adapter.NutrientAdapter;
import com.udacity.safebites.entities.Product;
import com.udacity.safebites.utils.QueryUtils;

import java.util.Objects;

import static com.udacity.safebites.utils.QueryUtils.EXTRA_PRODUCT;

public class NutrientsFragment extends Fragment implements View.OnClickListener {
    /**
     * Save or delete Floating Action Button (FAB).
     */
    private FloatingActionButton mSaveOrDeleteFAB;

    /**
     * Current product.
     */
    private Product mProduct;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nutrients, container, false);

        mSaveOrDeleteFAB = view.findViewById(R.id.save_or_delete_FAB);
        mSaveOrDeleteFAB.setVisibility(View.VISIBLE);
        mSaveOrDeleteFAB.setOnClickListener(this);

        ImageView image_nutrients = view.findViewById(R.id.image_view_nutrients_header);
        TextView name = view.findViewById(R.id.product_name_text_view);
        TextView upc = view.findViewById(R.id.product_upc_text_view);
        TextView header_per_serving = view.findViewById(R.id.header_per_serving);

        Bundle extras = getArguments();
        if (extras != null) {
            mProduct = getArguments().getParcelable(EXTRA_PRODUCT);
            if (mProduct != null) {
                if (mProduct.getFavorite_condition().equals("true")) {
                    convertToDeleteFAB(mSaveOrDeleteFAB);
                } else if (mProduct.getFavorite_condition().equals("false")) {
                    convertToSaveFAB(mSaveOrDeleteFAB);
                }

                String image_resource = mProduct.getImage_resource();
                if (image_resource == null || image_resource.length() == 0 || image_resource.equals("?")) {
                    Glide.with(Objects.requireNonNull(getActivity()))
                            .load(R.drawable.no_image_available)
                            .into(image_nutrients);
                } else {
                    Glide.with(Objects.requireNonNull(getActivity()))
                            .load(mProduct.getImage_resource())
                            .into(image_nutrients);
                }

                name.setText(mProduct.getName());
                upc.setText(mProduct.getUpc());

                String serving_quantity = mProduct.getServing_quantity();
                if (serving_quantity.equals("0")) {
                    header_per_serving.setVisibility(View.GONE);
                } else {
                    header_per_serving.setText(serving_quantity.concat(" g"));
                }

                NutrientAdapter mNutrientAdapter = new NutrientAdapter(mProduct.getNutrients());

                RecyclerView mRecyclerView = view.findViewById(R.id.nutrient_recycler);
                mRecyclerView.setHasFixedSize(true);

                LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(mLinearLayoutManager);
                mRecyclerView.setAdapter(mNutrientAdapter);
            }
        }

        return view;
    }

    private void convertToSaveFAB(FloatingActionButton fab) {
        fab.setImageResource(R.drawable.content_save);
        fab.setContentDescription(getResources().getString(R.string.save_image_button_description));
    }

    private void convertToDeleteFAB(FloatingActionButton fab) {
        fab.setImageResource(R.drawable.delete);
        fab.setContentDescription(getResources().getString(R.string.delete_image_button_description));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.save_or_delete_FAB) {
            if (mProduct.getFavorite_condition().equals("true")) {
                convertToSaveFAB(mSaveOrDeleteFAB);
                QueryUtils.deleteProduct(mProduct, view.getContext());
                mProduct.setFavorite_condition("false");
            } else if (mProduct.getFavorite_condition().equals("false")) {
                convertToDeleteFAB(mSaveOrDeleteFAB);
                QueryUtils.saveProduct(mProduct, view.getContext());
                mProduct.setFavorite_condition("true");
            }

            SearchFragment.updateRecyclerViewItem(mProduct.getUpc());
            FavoritesFragment.fillFavoriteProductsRecyclerView(view.getContext());
            CompareFragment.fillSpinners(view.getContext());
        }
    }
}