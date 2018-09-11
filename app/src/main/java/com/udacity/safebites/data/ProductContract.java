package com.udacity.safebites.data;

import android.net.Uri;
import android.provider.BaseColumns;

import static android.content.ContentResolver.CURSOR_DIR_BASE_TYPE;
import static android.content.ContentResolver.CURSOR_ITEM_BASE_TYPE;

/**
 * API Contract for the SafeBites app.
 */
public class ProductContract {
    /**
     * Content Authority of the SafeBites app.
     */
    static final String CONTENT_AUTHORITY = "com.example.android.products";

    /**
     * Possible path products.
     **/
    static final String PATH_PRODUCTS = "products";

    /**
     * Base Content URI to contact the Content Provider.
     */
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private ProductContract() {
    }

    public static final class ProductEntry implements BaseColumns {
        /**
         * The content URI to access the product data in the Content Provider.
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        /**
         * Name of database table for products.
         */
        public static final String TABLE_NAME = "products";

        /**
         * Unique ID number for the product.
         * <p>
         * Type: INTEGER
         */
        public static final String _ID = BaseColumns._ID;

        /**
         * UPC of the product.
         * <p>
         * Type: TEXT
         */
        public static final String COLUMN_PRODUCT_UPC = "upc";

        /**
         * Name of the product.
         * <p>
         * Type: TEXT
         */
        public static final String COLUMN_PRODUCT_NAME = "name";

        /**
         * Image resource of the product.
         * <p>
         * Type: TEXT
         */
        public static final String COLUMN_PRODUCT_IMAGE_RESOURCE = "image_resource";

        /**
         * Nutrients of the product.
         * <p>
         * Type: TEXT
         */
        public static final String COLUMN_PRODUCT_NUTRIENTS = "nutrients";

        /**
         * Ingredients of the product.
         * <p>
         * Type: TEXT
         */
        public static final String COLUMN_PRODUCT_INGREDIENTS = "ingredients";

        /**
         * Favorite condition of the product.
         * <p>
         * Type: TEXT
         */
        public static final String COLUMN_PRODUCT_FAVORITE_CONDITION = "favorite_condition";

        /**
         * Serving quantity of the product.
         * <p>
         * Type: TEXT
         */
        public static final String COLUMN_PRODUCT_SERVING_QUANTITY = "serving_quantity";

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of products.
         */
        static final String CONTENT_LIST_TYPE = CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single product.
         */
        static final String CONTENT_ITEM_TYPE = CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
    }
}