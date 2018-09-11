package com.udacity.safebites.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.safebites.R;
import com.udacity.safebites.entities.Product;

import static com.udacity.safebites.utils.QueryUtils.EXTRA_PRODUCT;

public class IngredientsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ingredients, container, false);

        Bundle extras = getArguments();
        if (extras != null) {
            Product product = extras.getParcelable(EXTRA_PRODUCT);

            TextView ingredientsTextView = view.findViewById(R.id.ingredients_textView);
            assert product != null;
            ingredientsTextView.setText(product.getIngredients());
        }

        return view;
    }
}
