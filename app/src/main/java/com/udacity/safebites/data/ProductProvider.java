package com.udacity.safebites.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Objects;

import static com.udacity.safebites.data.ProductContract.CONTENT_AUTHORITY;
import static com.udacity.safebites.data.ProductContract.PATH_PRODUCTS;
import static com.udacity.safebites.data.ProductContract.ProductEntry.COLUMN_PRODUCT_FAVORITE_CONDITION;
import static com.udacity.safebites.data.ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE_RESOURCE;
import static com.udacity.safebites.data.ProductContract.ProductEntry.COLUMN_PRODUCT_INGREDIENTS;
import static com.udacity.safebites.data.ProductContract.ProductEntry.COLUMN_PRODUCT_NAME;
import static com.udacity.safebites.data.ProductContract.ProductEntry.COLUMN_PRODUCT_NUTRIENTS;
import static com.udacity.safebites.data.ProductContract.ProductEntry.COLUMN_PRODUCT_SERVING_QUANTITY;
import static com.udacity.safebites.data.ProductContract.ProductEntry.COLUMN_PRODUCT_UPC;
import static com.udacity.safebites.data.ProductContract.ProductEntry.CONTENT_ITEM_TYPE;
import static com.udacity.safebites.data.ProductContract.ProductEntry.CONTENT_LIST_TYPE;
import static com.udacity.safebites.data.ProductContract.ProductEntry.CONTENT_URI;
import static com.udacity.safebites.data.ProductContract.ProductEntry.TABLE_NAME;
import static com.udacity.safebites.data.ProductContract.ProductEntry._ID;

public class ProductProvider extends ContentProvider {
    /**
     * Tag for the log messages.
     */
    private static final String LOG_TAG = ProductProvider.class.getSimpleName();

    /**
     * URI matcher code for the Content URI for the products table.
     */
    private static final int PRODUCTS = 100;

    /**
     * URI matcher code for the Content URI for a single product in the products table.
     */
    private static final int PRODUCTS_ID = 101;

    /**
     * URI Matcher object to match a content URI to a corresponding code.
     */
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    /**
     * Projection of the database.
     */
    private static final String[] projection = new String[]{
            _ID,
            COLUMN_PRODUCT_UPC,
            COLUMN_PRODUCT_NAME,
            COLUMN_PRODUCT_IMAGE_RESOURCE,
            COLUMN_PRODUCT_NUTRIENTS,
            COLUMN_PRODUCT_INGREDIENTS,
            COLUMN_PRODUCT_FAVORITE_CONDITION,
            COLUMN_PRODUCT_SERVING_QUANTITY
    };

    static {
        URI_MATCHER.addURI(CONTENT_AUTHORITY, PATH_PRODUCTS, PRODUCTS);
        URI_MATCHER.addURI(CONTENT_AUTHORITY, PATH_PRODUCTS + "/#", PRODUCTS_ID);
    }

    /**
     * Database helper object.
     */
    private ProductDbHelper mDbHelper;

    public static Cursor queryProduct(Context context) {
        return context.getContentResolver().query(CONTENT_URI, projection, null, null, null);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case PRODUCTS:
                cursor = database.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCTS_ID:
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(TABLE_NAME, projection, _ID + "=?", selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = URI_MATCHER.match(uri);
        switch (match) {
            case PRODUCTS:
                return CONTENT_LIST_TYPE;
            case PRODUCTS_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = URI_MATCHER.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = URI_MATCHER.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, values, selection, selectionArgs);
            case PRODUCTS_ID:
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                assert values != null;
                return updateProduct(uri, values, _ID + "=?", selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;
        final int match = URI_MATCHER.match(uri);
        switch (match) {
            case PRODUCTS:
                rowsDeleted = database.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCTS_ID:
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(TABLE_NAME, _ID + "=?", selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}