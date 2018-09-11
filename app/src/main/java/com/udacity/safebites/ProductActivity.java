package com.udacity.safebites;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.udacity.safebites.entities.Product;
import com.udacity.safebites.fragments.IngredientsFragment;
import com.udacity.safebites.fragments.NutrientsFragment;
import com.udacity.safebites.fragments.SectionsPageAdapter;

import java.util.Objects;

import static com.udacity.safebites.utils.QueryUtils.EXTRA_PRODUCT;

public class ProductActivity extends AppCompatActivity {
    /**
     *
     */
    private Bundle mArgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Product product = extras.getParcelable(EXTRA_PRODUCT);

            if (product != null) {
                mArgs = new Bundle();
                mArgs.putParcelable(EXTRA_PRODUCT, product);
            }
        }

        ViewPager mViewPager = findViewById(R.id.viewPagerContainer);
        setupViewPager(mViewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());

        NutrientsFragment nutrientsFragments = new NutrientsFragment();
        nutrientsFragments.setArguments(mArgs);

        IngredientsFragment ingredientsFragment = new IngredientsFragment();
        ingredientsFragment.setArguments(mArgs);

        adapter.addFragment(nutrientsFragments, getResources().getString(R.string.tab_text_nutrients));
        adapter.addFragment(ingredientsFragment, getResources().getString(R.string.tab_text_ingredients));
        viewPager.setAdapter(adapter);
    }
}