package com.udacity.safebites.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.udacity.safebites.ProductActivity;
import com.udacity.safebites.R;
import com.udacity.safebites.data.ProductProvider;
import com.udacity.safebites.entities.Product;
import com.udacity.safebites.fragments.CompareFragment;
import com.udacity.safebites.fragments.FavoritesFragment;
import com.udacity.safebites.fragments.SearchFragment;
import com.udacity.safebites.utils.QueryUtils;

import java.util.ArrayList;
import java.util.List;

import static com.udacity.safebites.utils.QueryUtils.EXTRA_PRODUCT;

public class ProductAdapter extends Adapter<ViewHolder> implements View.OnClickListener {
    /**
     *
     */
    private static final int ITEM = 0;

    /**
     *
     */
    private static final int LOADING = 1;

    /**
     *
     */
    private final Context mContext;

    /**
     *
     */
    private final List<Product> mProducts;

    /**
     *
     */
    private ProductViewHolder mProductViewHolder;

    /**
     *
     */
    private boolean isLoadingAdded = false;

    public ProductAdapter(Context context) {
        mContext = context;
        mProducts = new ArrayList<>();
    }

    public ProductAdapter(Context context, List<Product> products) {
        mContext = context;
        mProducts = products;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.item_product, parent, false);
        viewHolder = new ProductViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Product currentProduct = mProducts.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                mProductViewHolder = (ProductViewHolder) holder;

                String image_resource = currentProduct.getImage_resource();
                if (image_resource == null || image_resource.length() == 0 || image_resource.equals("?")) {
                    currentProduct.setImage_resource("?");
                    Glide.with(mContext)
                            .load(R.drawable.no_image_available)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    mProductViewHolder.mProgressBar.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    mProductViewHolder.mProgressBar.setVisibility(View.GONE);
                                    return false;
                                }
                            }).into(mProductViewHolder.mImageResource);
                } else {
                    Glide.with(mContext)
                            .load(currentProduct.getImage_resource())
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    mProductViewHolder.mProgressBar.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    mProductViewHolder.mProgressBar.setVisibility(View.GONE);
                                    return false;
                                }
                            }).into(mProductViewHolder.mImageResource);
                }

                String ingredients = currentProduct.getIngredients();
                if (ingredients == null || ingredients.length() == 0 || ingredients.equals("?")) {
                    currentProduct.setIngredients("\n" + "Ingredients not registered.");
                }

                mProductViewHolder.itemView.setTag(currentProduct);
                mProductViewHolder.itemView.setOnClickListener(this);
                mProductViewHolder.mName.setText(currentProduct.getName());
                mProductViewHolder.mUpc.setText(currentProduct.getUpc());
                mProductViewHolder.mFavoriteCondition.setImageResource(R.drawable.content_save);
                mProductViewHolder.mFavoriteCondition.setContentDescription(mContext.getResources().getString(R.string.save_image_button_description));
                mProductViewHolder.mFavoriteCondition.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currentProduct.getFavorite_condition().equals("false")) {
                            mProductViewHolder.mFavoriteCondition.setImageResource(R.drawable.delete);
                            mProductViewHolder.mFavoriteCondition.setContentDescription(mContext.getResources().getString(R.string.delete_image_button_description));
                            QueryUtils.saveProduct(currentProduct, v.getContext());
                        } else if (currentProduct.getFavorite_condition().equals("true")) {
                            mProductViewHolder.mFavoriteCondition.setImageResource(R.drawable.content_save);
                            mProductViewHolder.mFavoriteCondition.setContentDescription(mContext.getResources().getString(R.string.save_image_button_description));
                            QueryUtils.deleteProduct(currentProduct, v.getContext());
                        }

                        SearchFragment.updateRecyclerViewItem(currentProduct.getUpc());
                        FavoritesFragment.fillFavoriteProductsRecyclerView(v.getContext());
                        CompareFragment.fillSpinners(v.getContext());
                    }
                });

                Cursor c = ProductProvider.queryProduct(mContext);
                assert c != null;
                while (c.moveToNext()) {
                    if (c.getString(1).equals(currentProduct.getUpc())) {
                        currentProduct.setFavorite_condition("true");
                        mProductViewHolder.mFavoriteCondition.setImageResource(R.drawable.delete);
                        mProductViewHolder.mFavoriteCondition.setContentDescription(mContext.getResources().getString(R.string.delete_image_button_description));
                    }
                }
                c.close();
                break;
            case LOADING:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mProducts == null ? 0 : mProducts.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mProducts.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == mProductViewHolder.itemView.getId()) {
            Intent intent = new Intent(mContext, ProductActivity.class);
            intent.putExtra(EXTRA_PRODUCT, (Parcelable) view.getTag());
            mContext.startActivity(intent);
        }
    }

    public Product getItem(int position) {
        return mProducts.get(position);
    }

    public String getItemUpc(int position) {
        return mProducts.get(position).getUpc();
    }

    private void add(Product p) {
        mProducts.add(p);
        notifyItemInserted(mProducts.size() - 1);
    }

    public void update(int position, Product p) {
        mProducts.set(position, p);
    }

    public void addAll(List<Product> moveProducts) {
        for (Product product : moveProducts) {
            add(product);
        }
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Product());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = mProducts.size() - 1;
        Product product = getItem(position);

        if (product != null) {
            mProducts.remove(position);
            notifyItemRemoved(position);
        }
    }

    class ProductViewHolder extends ViewHolder {
        final private TextView mUpc;
        final private TextView mName;
        final private ImageView mImageResource;
        final private ImageButton mFavoriteCondition;
        final private ProgressBar mProgressBar;

        ProductViewHolder(View itemView) {
            super(itemView);
            mUpc = itemView.findViewById(R.id.product_extra_information);
            mName = itemView.findViewById(R.id.product_name);
            mImageResource = itemView.findViewById(R.id.product_image);
            mFavoriteCondition = itemView.findViewById(R.id.favorite_condition_image_button);
            mProgressBar = itemView.findViewById(R.id.image_progress_bar);
        }
    }

    private class LoadingVH extends RecyclerView.ViewHolder {
        LoadingVH(View itemView) {
            super(itemView);
        }
    }
}