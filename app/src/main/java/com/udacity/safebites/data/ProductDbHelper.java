package com.udacity.safebites.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class ProductDbHelper extends SQLiteOpenHelper {
    /**
     * Database name.
     */
    private static final String DATABASE_NAME = "products.db";

    /**
     * Version of the database.
     */
    private static final int DATABASE_VERSION = 1;

    ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + ProductContract.ProductEntry.TABLE_NAME + " ("
                + ProductContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductContract.ProductEntry.COLUMN_PRODUCT_UPC + " TEXT NOT NULL, "
                + ProductContract.ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE_RESOURCE + " TEXT NOT NULL, "
                + ProductContract.ProductEntry.COLUMN_PRODUCT_NUTRIENTS + " TEXT NOT NULL, "
                + ProductContract.ProductEntry.COLUMN_PRODUCT_INGREDIENTS + " TEXT NOT NULL, "
                + ProductContract.ProductEntry.COLUMN_PRODUCT_FAVORITE_CONDITION + " TEXT NOT NULL, "
                + ProductContract.ProductEntry.COLUMN_PRODUCT_SERVING_QUANTITY + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Products");
        String sqlCreate = "CREATE TABLE Products " +
                "(_id INTEGER PRIMARY KEY, " +
                " upc TEXT, " +
                " name TEXT, " +
                " image_resource TEXT, " +
                " nutrients TEXT, " +
                " ingredients TEXT, " +
                " favorite_condition TEXT, " +
                " serving_quantity TEXT )";
        db.execSQL(sqlCreate);
    }
}
