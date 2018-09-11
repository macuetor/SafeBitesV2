package com.udacity.safebites.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.udacity.safebites.R;
import com.udacity.safebites.adapter.ProductAdapter;
import com.udacity.safebites.api.ProductApi;
import com.udacity.safebites.api.ProductService;
import com.udacity.safebites.entities.Nutrient;
import com.udacity.safebites.entities.Product;
import com.udacity.safebites.entities.Products;
import com.udacity.safebites.utils.DesignUtils;
import com.udacity.safebites.utils.PaginationScrollListener;
import com.udacity.safebites.utils.QueryUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {
    /**
     * Number of the initial page.
     */
    private static final int PAGE_START = 1;

    /**
     * Product adapter for the RecyclerView.
     */
    private static ProductAdapter sProductAdapter;

    /**
     * RecyclerView of products.
     */
    private RecyclerView mRecyclerView;

    /**
     * TextView that is displayed when the list of products is empty.
     */
    private TextView mEmptyTextView;

    /**
     * Progress bar for loading products.
     */
    private ProgressBar mProgressBar;

    /**
     *
     */
    private TextView mProgressBarTextView;

    /**
     * EditText used to perform a search.
     */
    private EditText mSearchEditText;

    /**
     * Number of the current page.
     */
    private int mCurrentPage = PAGE_START;

    /**
     * Total number of pages.
     */
    private int mTotalPages;

    /**
     * Last page condition.
     */
    private boolean mIsLastPage = false;

    /**
     * Load condition.
     */
    private boolean mIsLoading = false;

    /**
     * Product service.
     */
    private ProductService mProductService;

    /**
     *
     */
    private MenuItem mSearchMenuItem;

    /**
     *
     */
    private MenuItem mCancelMenuItem;

    public static void updateRecyclerViewItem(String upc) {
        for (int i = 0; i < sProductAdapter.getItemCount(); i++) {
            if (sProductAdapter.getItemUpc(i) != null && sProductAdapter.getItemUpc(i).equals(upc)) {
                Product product = sProductAdapter.getItem(i);

                if (product.getFavorite_condition().equals("true")) {
                    product.setFavorite_condition("false");
                } else {
                    product.setFavorite_condition("true");
                }
                sProductAdapter.update(i, sProductAdapter.getItem(i));
            }
        }

        sProductAdapter.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        setHasOptionsMenu(true);
        Toolbar toolbar = Objects.requireNonNull(getActivity()).findViewById(R.id.toolbar);
        mSearchEditText = toolbar.findViewById(R.id.search_edit_text);
        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    DesignUtils.hideSoftKeyboard(getActivity());
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBarTextView.setVisibility(View.VISIBLE);
                    reloadData();
                    loadFirstPage();
                    return true;
                }
                return false;
            }
        });

        sProductAdapter = new ProductAdapter(getActivity());
        mProgressBar = Objects.requireNonNull(getActivity()).findViewById(R.id.main_progress_bar);
        mProgressBarTextView = getActivity().findViewById(R.id.text_progress_bar);
        mEmptyTextView = view.findViewById(R.id.empty_textView);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView = view.findViewById(R.id.product_recycler);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(sProductAdapter);
        mRecyclerView.addOnScrollListener(new PaginationScrollListener(mLinearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                mIsLoading = true;
                mCurrentPage += 1;
                loadNextPage();
            }

            @Override
            public boolean isLastPage() {
                return mIsLastPage;
            }

            @Override
            public boolean isLoading() {
                return mIsLoading;
            }
        });

        mProductService = ProductApi.getClient().create(ProductService.class);
        return view;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (sProductAdapter.getItemCount() > 0) {
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBarTextView.setVisibility(View.VISIBLE);
            reloadData();
            loadFirstPage();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);

        mSearchMenuItem = menu.findItem(R.id.action_search);
        mCancelMenuItem = menu.findItem(R.id.action_cancel);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            mSearchMenuItem.setVisible(false);
            mCancelMenuItem.setVisible(true);
            mSearchEditText.setVisibility(View.VISIBLE);
            mSearchEditText.requestFocus();

            InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.showSoftInput(mSearchEditText, InputMethodManager.SHOW_IMPLICIT);
        } else if (id == R.id.action_cancel) {
            mSearchMenuItem.setVisible(true);
            mCancelMenuItem.setVisible(false);
            mSearchEditText.setVisibility(View.GONE);
            DesignUtils.hideSoftKeyboard(Objects.requireNonNull(getActivity()));
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadFirstPage() {
        // Reload adapter:
        sProductAdapter = new ProductAdapter(getActivity());
        mRecyclerView.setAdapter(sProductAdapter);

        if (mSearchEditText != null && mSearchEditText.getText().length() != 0 && isConnected()) {
            callProductsApi().enqueue(new Callback<Products>() {
                @Override
                public void onResponse(@NonNull Call<Products> call, @NonNull Response<Products> response) {
                    List<Product> products = fetchResults(response);
                    if (products.isEmpty()) {
                        mProgressBar.setVisibility(View.GONE);
                        mProgressBarTextView.setVisibility(View.GONE);

                        mEmptyTextView.setText(getResources().getString(R.string.product_not_found));
                        mEmptyTextView.setVisibility(View.VISIBLE);
                    } else {
                        mEmptyTextView.setVisibility(View.GONE);
                        createJSONNutrients(products);

                        sProductAdapter.addAll(products);

                        if (mCurrentPage < mTotalPages) sProductAdapter.addLoadingFooter();
                        else mIsLastPage = true;

                        mProgressBar.setVisibility(View.GONE);
                        mProgressBarTextView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Products> call, @NonNull Throwable t) {
                    t.printStackTrace();
                }
            });
        } else {
            mProgressBar.setVisibility(View.GONE);
            mProgressBarTextView.setVisibility(View.GONE);
            Toast.makeText(getActivity(), getResources().getString(R.string.search_request), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadNextPage() {
        if (isConnected()) {
            callProductsApi().enqueue(new Callback<Products>() {
                @Override
                public void onResponse(@NonNull Call<Products> call, @NonNull Response<Products> response) {
                    sProductAdapter.removeLoadingFooter();
                    mIsLoading = false;

                    List<Product> products = fetchResults(response);
                    createJSONNutrients(products);

                    sProductAdapter.addAll(products);

                    if (mCurrentPage != mTotalPages) sProductAdapter.addLoadingFooter();
                    else mIsLastPage = true;
                }

                @Override
                public void onFailure(@NonNull Call<Products> call, @NonNull Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    private void reloadData() {
        mCurrentPage = PAGE_START;
        mIsLoading = false;
        mIsLastPage = false;
    }

    private List<Product> fetchResults(Response<Products> response) {
        Products products = response.body();
        assert products != null;
        mTotalPages = products.getCount() / products.getPageSize() + 1;
        return products.getProducts();
    }

    private Call<Products> callProductsApi() {
        return mProductService.getProducts(
                mSearchEditText.getText().toString(),
                1,
                "process",
                1,
                10,
                mCurrentPage
        );
    }

    private void createJSONNutrients(List<Product> products) {
        for (int i = 0; i < products.size(); i++) {
            Object nutrientsObject = products.get(i).getNutrientsObject();

            Gson gson = new Gson();
            String nutrientsString = gson.toJson(nutrientsObject);

            ArrayList<Nutrient> product_nutrients = new ArrayList<>();
            try {
                JSONObject JSONNutrients = new JSONObject(nutrientsString);

                QueryUtils.createNutrients(product_nutrients, JSONNutrients);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }

            products.get(i).setNutrients(product_nutrients);
        }
    }

    private boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) Objects.requireNonNull(getContext()).getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connMgr != null;
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            mEmptyTextView.setVisibility(View.GONE);
        } else {
            mEmptyTextView.setText(R.string.no_internet_connection);
            mEmptyTextView.setVisibility(View.VISIBLE);
        }
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}