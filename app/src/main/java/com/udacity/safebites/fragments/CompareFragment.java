package com.udacity.safebites.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.udacity.safebites.CaptureActivityPortrait;
import com.udacity.safebites.R;
import com.udacity.safebites.adapter.SimplifiedNutrientAdapter;
import com.udacity.safebites.entities.Nutrient;
import com.udacity.safebites.entities.Product;
import com.udacity.safebites.entities.SimplifiedNutrient;
import com.udacity.safebites.utils.QueryUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.udacity.safebites.MainActivity.RC_SCAN_OPTION_1_FIRST_EXECUTION;
import static com.udacity.safebites.MainActivity.RC_SCAN_OPTION_2;

public class CompareFragment extends Fragment implements View.OnClickListener {
    /**
     *
     */
    private static Spinner mProductASpinner;

    /**
     *
     */
    private static Spinner mProductBSpinner;

    /**
     *
     */
    private static RecyclerView mRecyclerView;
    /**
     *
     */
    private static TextView product_A_name;
    /**
     *
     */
    private static TextView product_B_name;
    /**
     *
     */
    private static RelativeLayout comparison_container;
    /**
     *
     */
    private static Product mProductA;
    /**
     *
     */
    private static Product mProductB;
    /**
     *
     */
    private LinearLayout mProductAContainer;
    /**
     *
     */
    private LinearLayout mProductBContainer;
    /**
     *
     */
    private int mOption;

    public static void fillSpinners(Context context) {
        List<Product> favoriteProducts = QueryUtils.loadFavoriteProducts(context);

        ArrayAdapter<Product> adapterFavoriteProducts = new ArrayAdapter<>(Objects.requireNonNull(context), android.R.layout.simple_spinner_dropdown_item, favoriteProducts);
        adapterFavoriteProducts.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mProductASpinner.setAdapter(adapterFavoriteProducts);
        mProductBSpinner.setAdapter(adapterFavoriteProducts);
    }

    private static List<SimplifiedNutrient> createSimplifiedNutrientObjects(List<Nutrient> productANutrients, List<Nutrient> productBNutrients) {
        List<SimplifiedNutrient> simplifiedNutrients = new ArrayList<>();
        List<Nutrient> unitedNutrients = new ArrayList<>(productANutrients);
        unitedNutrients.addAll(productBNutrients);

        for (int i = 0; i < unitedNutrients.size(); i++) {
            Nutrient firstNutrient = unitedNutrients.get(i);

            if (!exists(simplifiedNutrients, firstNutrient.getName())) {
                SimplifiedNutrient simplifiedNutrient = null;

                for (int j = i + 1; j < unitedNutrients.size(); j++) {
                    Nutrient secondNutrient = unitedNutrients.get(j);
                    if (firstNutrient.getName().equals(secondNutrient.getName())) {
                        simplifiedNutrient = new SimplifiedNutrient(firstNutrient.getName(), firstNutrient.getPer_100g(), secondNutrient.getPer_100g(), firstNutrient.getUnit());
                        simplifiedNutrients.add(simplifiedNutrient);
                    }
                }

                if (simplifiedNutrient == null) {
                    if (i < productANutrients.size()) {
                        simplifiedNutrient = new SimplifiedNutrient(firstNutrient.getName(), firstNutrient.getPer_100g(), "-", firstNutrient.getUnit());
                    } else {
                        simplifiedNutrient = new SimplifiedNutrient(firstNutrient.getName(), "-", firstNutrient.getPer_100g(), firstNutrient.getUnit());
                    }
                    simplifiedNutrients.add(simplifiedNutrient);
                }
            }
        }

        return simplifiedNutrients;
    }

    private static boolean exists(List<SimplifiedNutrient> simplifiedNutrients, String nutrientName) {
        boolean condition = false;
        for (int i = 0; i < simplifiedNutrients.size() && !condition; i++) {
            SimplifiedNutrient currentSimplifiedNutrient = simplifiedNutrients.get(i);
            if (currentSimplifiedNutrient.getName().equals(nutrientName)) {
                condition = true;
            }
        }
        return condition;
    }

    public static void auxiliaryMethodComparisonOption3(Product productB, Context context) {
        if (mProductASpinner.getCount() > 0) {
            Product productA = (Product) mProductASpinner.getSelectedItem();

            fillSimplifiedNutrientsRecyclerView(productA, productB);
        } else {
            Toast.makeText(context, Objects.requireNonNull(context).getResources().getString(R.string.comparison_error), Toast.LENGTH_SHORT).show();
        }
    }

    public static void fillSimplifiedNutrientsRecyclerView(Product productA, Product productB) {
        product_A_name.setText(productA.getName());
        product_B_name.setText(productB.getName());
        mProductA = productA;
        mProductB = productB;

        comparison_container.setVisibility(View.VISIBLE);

        List<SimplifiedNutrient> simplifiedNutrients = createSimplifiedNutrientObjects(productA.getNutrients(), productB.getNutrients());
        SimplifiedNutrientAdapter mSimplifiedNutrientAdapter = new SimplifiedNutrientAdapter(simplifiedNutrients);
        mRecyclerView.setAdapter(mSimplifiedNutrientAdapter);
    }

    public static void startComparison(Activity activity, int requestCode) {
        IntentIntegrator intentIntegrator = new IntentIntegrator(activity);
        intentIntegrator.setRequestCode(requestCode);
        intentIntegrator.setPrompt("Scan a product's barcode");
        intentIntegrator.setCameraId(0);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setCaptureActivity(CaptureActivityPortrait.class);
        intentIntegrator.initiateScan();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compare, container, false);

        comparison_container = view.findViewById(R.id.comparison_container);
        product_A_name = view.findViewById(R.id.product_A_name);
        product_B_name = view.findViewById(R.id.product_B_name);

        List<String> list = new ArrayList<>();
        list.add("Compare two scanned products");
        list.add("Compare two favorite products");
        list.add("Compare a scanned product and a favorite product");

        RadioGroup radioGroup = view.findViewById(R.id.options_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.radio_button_1:
                        mOption = 1;
                        mProductAContainer.setVisibility(View.GONE);
                        mProductBContainer.setVisibility(View.GONE);
                        break;
                    case R.id.radio_button_2:
                        mOption = 2;
                        mProductAContainer.setVisibility(View.VISIBLE);
                        mProductBContainer.setVisibility(View.VISIBLE);
                        break;
                    case R.id.radio_button_3:
                        mOption = 3;
                        mProductAContainer.setVisibility(View.VISIBLE);
                        mProductBContainer.setVisibility(View.GONE);
                        break;
                }
            }
        });

        mProductAContainer = view.findViewById(R.id.product_A_container);
        mProductASpinner = view.findViewById(R.id.product_A_spinner);
        mProductBContainer = view.findViewById(R.id.product_B_container);
        mProductBSpinner = view.findViewById(R.id.product_B_spinner);

        Button scanButton = view.findViewById(R.id.scan_button);
        scanButton.setOnClickListener(this);

        mRecyclerView = view.findViewById(R.id.simplified_nutrients_recycler);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mProductALinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mProductALinearLayoutManager);

        ArrayAdapter<String> adapterOptions = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_item, list);
        adapterOptions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fillSpinners(getContext());
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.scan_button) {
            comparison_container.setVisibility(View.GONE);

            switch (mOption) {
                case 1:
                    startComparison(getActivity(), RC_SCAN_OPTION_1_FIRST_EXECUTION);
                    break;
                case 2:
                    startComparisonOption2();
                    break;
                case 3:
                    startComparison(getActivity(), RC_SCAN_OPTION_2);
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mProductA != null && mProductB != null) {
            fillSimplifiedNutrientsRecyclerView(mProductA, mProductB);
            comparison_container.setVisibility(View.VISIBLE);
        }
    }

    private void startComparisonOption2() {
        if (mProductASpinner.getCount() > 0 && mProductBSpinner.getCount() > 0) {
            Product productA = (Product) mProductASpinner.getSelectedItem();
            Product productB = (Product) mProductBSpinner.getSelectedItem();

            fillSimplifiedNutrientsRecyclerView(productA, productB);
        } else {
            Toast.makeText(getContext(), Objects.requireNonNull(getContext()).getResources().getString(R.string.comparison_error), Toast.LENGTH_SHORT).show();
        }
    }
}