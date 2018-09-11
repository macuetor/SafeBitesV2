package com.udacity.safebites.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.safebites.R;
import com.udacity.safebites.entities.Nutrient;

import java.util.List;

public class NutrientAdapter extends Adapter<ViewHolder> {
    /**
     * List of nutrients.
     */
    private final List<Nutrient> mNutrients;

    public NutrientAdapter(List<Nutrient> nutrients) {
        mNutrients = nutrients;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NutrientViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nutrient, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final NutrientViewHolder nutrientViewHolder = (NutrientViewHolder) holder;
        final Nutrient currentNutrient = mNutrients.get(position);

        nutrientViewHolder.itemView.setTag(currentNutrient);
        nutrientViewHolder.mName.setText(currentNutrient.getName());
        nutrientViewHolder.mPer100g.setText(currentNutrient.getPer_100g().concat(" " + currentNutrient.getUnit()));

        if (currentNutrient.getPer_serving().equals("?")) {
            nutrientViewHolder.mPerServing.setVisibility(View.GONE);
        } else {
            nutrientViewHolder.mPerServing.setText(currentNutrient.getPer_serving().concat(" " + currentNutrient.getUnit()));
        }
    }

    @Override
    public int getItemCount() {
        return mNutrients == null ? 0 : mNutrients.size();
    }

    class NutrientViewHolder extends ViewHolder {
        final private TextView mName;
        final private TextView mPer100g;
        final private TextView mPerServing;

        NutrientViewHolder(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.text_name);
            mPer100g = itemView.findViewById(R.id.text_per_100g);
            mPerServing = itemView.findViewById(R.id.text_per_portion);
        }
    }
}