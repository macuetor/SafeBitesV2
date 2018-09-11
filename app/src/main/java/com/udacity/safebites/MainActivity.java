package com.udacity.safebites;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.udacity.safebites.entities.Product;
import com.udacity.safebites.fragments.CompareFragment;
import com.udacity.safebites.fragments.FavoritesFragment;
import com.udacity.safebites.fragments.ScanFragment;
import com.udacity.safebites.fragments.SearchFragment;
import com.udacity.safebites.fragments.SectionsPageAdapter;
import com.udacity.safebites.utils.AsyncResponse;
import com.udacity.safebites.utils.JsonTask;

import java.util.Arrays;
import java.util.Objects;

import static com.google.zxing.integration.android.IntentIntegrator.parseActivityResult;
import static com.udacity.safebites.utils.QueryUtils.EXTRA_PRODUCT;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AsyncResponse {
    /**
     *
     */
    public static final int RC_SCAN_OPTION_1_FIRST_EXECUTION = 11;

    /**
     *
     */
    public static final int RC_SCAN_OPTION_2 = 2;

    /**
     * Header URL of the API for a specific product.
     */
    private static final String HEADER_SPECIFIC_PRODUCT_URL = "https://world.openfoodfacts.org/api/v0/product/";

    /**
     * Tail URL of the API for a specific product.
     */
    private static final String TAIL_SPECIFIC_PRODUCT_URL = ".json";

    /**
     *
     */
    private final int RC_SIGN_IN = 0;

    /**
     *
     */
    private final int RC_SCAN = 0x0000b90f;

    /**
     *
     */
    private final int RC_SCAN_OPTION_1_SECOND_EXECUTION = 12;

    /**
     *
     */
    private FirebaseAuth mAuth;

    /**
     *
     */
    private AuthStateListener mAuthStateListener;

    /**
     *
     */
    private ViewPager mViewPager;

    /**
     *
     */
    private TabLayout mTabLayout;

    /**
     *
     */
    private ProgressBar mProgressBar;

    /**
     *
     */
    private TextView mProgressBarTextView;

    /**
     *
     */
    private int request_code;

    /**
     *
     */
    private Product productA;

    /**
     *
     */
    private String mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mViewPager = findViewById(R.id.viewPagerContainer);
        setupViewPager(mViewPager);
        mViewPager.setCurrentItem(1);

        mTabLayout = findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0:
                        IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                        intentIntegrator.setRequestCode(RC_SCAN);
                        intentIntegrator.setPrompt("Scan a product's barcode");
                        intentIntegrator.setCameraId(0);
                        intentIntegrator.setOrientationLocked(true);
                        intentIntegrator.setBeepEnabled(true);
                        intentIntegrator.setCaptureActivity(CaptureActivityPortrait.class);
                        intentIntegrator.initiateScan();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setupCameraTabItem();

        mProgressBar = findViewById(R.id.main_progress_bar);
        mProgressBarTextView = findViewById(R.id.text_progress_bar);

        //Initialize Firebase components:
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    mUser = user.getUid();
                    // User is signed in
                    drawer.setVisibility(View.VISIBLE);
                } else {
                    // User is signed out
                    drawer.setVisibility(View.GONE);
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setLogo(R.drawable.food_icon)
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.FacebookBuilder().build(),
                                            new AuthUI.IdpConfig.TwitterBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        request_code = requestCode;

        switch (request_code) {
            case RC_SIGN_IN:
                if (resultCode == RESULT_CANCELED) {
                    finish();
                }
                break;
            case RC_SCAN:
            case RC_SCAN_OPTION_1_FIRST_EXECUTION:
            case RC_SCAN_OPTION_1_SECOND_EXECUTION:
            case RC_SCAN_OPTION_2:
                IntentResult scanResult = parseActivityResult(IntentIntegrator.REQUEST_CODE, resultCode, data);
                if (resultCode == RESULT_CANCELED) {
                    mViewPager.setCurrentItem(1);
                    Toast.makeText(this, R.string.scan_canceled, Toast.LENGTH_SHORT).show();
                } else if (scanResult != null) {
                    startScan(scanResult.getContents(), requestCode);
                }
                break;
        }
    }

    @Override
    public void processFinish(Product output) {
        if (output != null) {
            switch (request_code) {
                case RC_SCAN:
                    mViewPager.setCurrentItem(1);

                    Intent intent = new Intent(this, ProductActivity.class);
                    intent.putExtra(EXTRA_PRODUCT, output);
                    startActivity(intent);
                    break;
                case RC_SCAN_OPTION_1_FIRST_EXECUTION:
                    productA = output;
                    CompareFragment.startComparison(MainActivity.this, RC_SCAN_OPTION_1_SECOND_EXECUTION);
                    break;
                case RC_SCAN_OPTION_1_SECOND_EXECUTION:
                    CompareFragment.fillSimplifiedNutrientsRecyclerView(productA, output);
                    break;
                case RC_SCAN_OPTION_2:
                    mViewPager.setCurrentItem(1);
                    CompareFragment.auxiliaryMethodComparisonOption3(output, getApplicationContext());
                    break;
            }
        } else {
            mViewPager.setCurrentItem(1);
            Toast.makeText(this, R.string.product_not_found, Toast.LENGTH_SHORT).show();
        }

        mProgressBar.setVisibility(View.GONE);
        mProgressBarTextView.setVisibility(View.GONE);
        mProgressBarTextView.setText(R.string.searching_progress_bar);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.nav_log_out:
                AuthUI.getInstance().signOut(this);
                break;
            case R.id.nav_settings:
                break;
            case R.id.nav_share:
                String message = getResources().getString(R.string.email_content) + "\n" +
                        getResources().getString(R.string.email_url); // App name in Play Store.
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, message);
                intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.default_app_name));
                startActivity(Intent.createChooser(intent, getResources().getString(R.string.chooser_message)));
                break;
            case R.id.nav_information:
                startActivity(new Intent(this, InformationActivity.class));
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new ScanFragment(), "");
        adapter.addFragment(new CompareFragment(), getResources().getString(R.string.tab_text_compare));
        adapter.addFragment(new SearchFragment(), getResources().getString(R.string.tab_text_search));
        adapter.addFragment(new FavoritesFragment(), getResources().getString(R.string.tab_text_favorites));
        viewPager.setAdapter(adapter);
    }

    private void setupCameraTabItem() {
        // Enter the icon for the Camera TabItem:
        Objects.requireNonNull(mTabLayout.getTabAt(0)).setIcon(R.drawable.ic_barcode_scan_white_24dp);

        // Adjust the size for the Camera TabItem:
        LinearLayout layout = ((LinearLayout) ((LinearLayout) mTabLayout.getChildAt(0)).getChildAt(0));
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layout.getLayoutParams();
        layoutParams.weight = 0.5f;
        layout.setLayoutParams(layoutParams);
    }

    private void startScan(String scan_result, int requestCode) {
        final String queryUrl = HEADER_SPECIFIC_PRODUCT_URL + scan_result + TAIL_SPECIFIC_PRODUCT_URL;

        if (requestCode == RC_SCAN) {
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBarTextView.setVisibility(View.VISIBLE);
            mProgressBarTextView.setText(R.string.scanning_progress_bar);
        } else if (requestCode == RC_SCAN_OPTION_1_FIRST_EXECUTION) {
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBarTextView.setVisibility(View.VISIBLE);
            mProgressBarTextView.setText(R.string.loading_progress_bar);
        }

        final JsonTask jsonTask = new JsonTask(this);
        jsonTask.delegate = this;
        jsonTask.execute(queryUrl);

        /*Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                jsonTask.execute(queryUrl);
            }
        }, 5000);*/
    }
}