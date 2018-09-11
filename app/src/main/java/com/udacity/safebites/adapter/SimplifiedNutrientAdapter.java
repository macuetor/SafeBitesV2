package com.udacity.safebites.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.safebites.R;
import com.udacity.safebites.entities.SimplifiedNutrient;

import java.util.List;

public class SimplifiedNutrientAdapter extends Adapter<ViewHolder> {
    /**
     * List of simplified nutrients.
     */
    private final List<SimplifiedNutrient> mSimplifiedNutrients;

    public SimplifiedNutrientAdapter(List<SimplifiedNutrient> products) {
        mSimplifiedNutrients = products;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SimpleNutrientViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comparision, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final SimpleNutrientViewHolder simpleNutrientViewHolder = (SimpleNutrientViewHolder) holder;
        final SimplifiedNutrient currentSimplifiedNutrient = mSimplifiedNutrients.get(position);

        String productAQuantity = currentSimplifiedNutrient.getQuantityA();
        if (productAQuantity.equals("-")) {
            simpleNutrientViewHolder.mProductANutrient.setText("- ".concat(currentSimplifiedNutrient.getUnit()));
        } else {
            simpleNutrientViewHolder.mProductANutrient.setText(productAQuantity.concat(" " + currentSimplifiedNutrient.getUnit()));
        }

        String productBQuantity = currentSimplifiedNutrient.getQuantityB();
        if (productBQuantity.equals("-")) {
            simpleNutrientViewHolder.mProductBNutrient.setText("- ".concat(" " + currentSimplifiedNutrient.getUnit()));
        } else {
            simpleNutrientViewHolder.mProductBNutrient.setText(productBQuantity.concat(" " + currentSimplifiedNutrient.getUnit()));
        }

        simpleNutrientViewHolder.itemView.setTag(currentSimplifiedNutrient);
        simpleNutrientViewHolder.mName.setText(currentSimplifiedNutrient.getName());
    }

    @Override
    public int getItemCount() {
        return mSimplifiedNutrients == null ? 0 : mSimplifiedNutrients.size();
    }

    class SimpleNutrientViewHolder extends ViewHolder {
        final private TextView mName;
        final private TextView mProductANutrient;
        final private TextView mProductBNutrient;

        SimpleNutrientViewHolder(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.text_name);
            mProductANutrient = itemView.findViewById(R.id.text_product_A);
            mProductBNutrient = itemView.findViewById(R.id.text_product_B);
        }
    }
}