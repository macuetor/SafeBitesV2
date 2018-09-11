package com.udacity.safebites.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.safebites.R;
import com.udacity.safebites.data.ProductContract;
import com.udacity.safebites.entities.Product;

import java.io.IOException;
import java.net.URL;

import static com.udacity.safebites.utils.QueryUtils.EXTRA_PRODUCT;
import static com.udacity.safebites.utils.QueryUtils.createNutrientObjects;

class MyWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    /**
     *
     */
    private final Context mContext;

    /**
     *
     */
    private Cursor mCursor;

    MyWidgetRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        if (mCursor != null) {
            mCursor.close();
        }

        final long identityToken = Binder.clearCallingIdentity();
        Uri uri = ProductContract.ProductEntry.CONTENT_URI;
        mCursor = mContext.getContentResolver().query(uri, null, null, null, null);

        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public int getCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION ||
                mCursor == null || !mCursor.moveToPosition(position)) {
            return null;
        }

        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.item_widget);

        String upc = mCursor.getString(1);
        String name = mCursor.getString(2);
        String image_resource = mCursor.getString(3);
        String nutrients = mCursor.getString(4);
        String ingredients = mCursor.getString(5);
        String favorite_condition = mCursor.getString(6);
        String serving_quantity = mCursor.getString(7);

        Product product = new Product(upc, name, image_resource, createNutrientObjects(nutrients), ingredients, favorite_condition, serving_quantity);

        rv.setTextViewText(R.id.product_upc, upc);
        rv.setTextViewText(R.id.product_name, name);

        try {
            boolean validURL = Patterns.WEB_URL.matcher(image_resource).matches();
            if (validURL) {
                URL url = new URL(image_resource);
                Bitmap bitmap = BitmapFactory.decodeStream(url.openStream());
                rv.setImageViewBitmap(R.id.product_image, bitmap);
            } else {
                rv.setImageViewResource(R.id.product_image, R.drawable.no_image_available);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bundle extras = new Bundle();
        extras.putParcelable(EXTRA_PRODUCT, product);

        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        rv.setOnClickFillInIntent(R.id.widgetItemContainer, fillInIntent);

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return mCursor.moveToPosition(position) ? mCursor.getLong(0) : position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}