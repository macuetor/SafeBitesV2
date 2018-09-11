package com.udacity.safebites.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.udacity.safebites.R;
import com.udacity.safebites.data.ProductProvider;
import com.udacity.safebites.entities.Nutrient;
import com.udacity.safebites.entities.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.udacity.safebites.data.ProductContract.ProductEntry.COLUMN_PRODUCT_FAVORITE_CONDITION;
import static com.udacity.safebites.data.ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE_RESOURCE;
import static com.udacity.safebites.data.ProductContract.ProductEntry.COLUMN_PRODUCT_INGREDIENTS;
import static com.udacity.safebites.data.ProductContract.ProductEntry.COLUMN_PRODUCT_NAME;
import static com.udacity.safebites.data.ProductContract.ProductEntry.COLUMN_PRODUCT_NUTRIENTS;
import static com.udacity.safebites.data.ProductContract.ProductEntry.COLUMN_PRODUCT_SERVING_QUANTITY;
import static com.udacity.safebites.data.ProductContract.ProductEntry.COLUMN_PRODUCT_UPC;
import static com.udacity.safebites.data.ProductContract.ProductEntry.CONTENT_URI;

public class QueryUtils {
    /**
     *
     */
    public static final String EXTRA_PRODUCT = "com.udacity.safebites.extras.EXTRA_PRODUCT";

    private QueryUtils() {
    }

    static Product extractJSONNutrients(JSONObject currentProduct, JSONObject JSONNutrients) {
        Product product = null;
        try {
            String[] product_elements = {"code", "product_name", "image_small_url", "ingredients_text", "serving_quantity"};
            ArrayList<String> product_elements_result = new ArrayList<>();

            for (String product_element : product_elements) {
                String element_name = "?";
                if (currentProduct.has(product_element)) {
                    element_name = currentProduct.getString(product_element);
                }
                product_elements_result.add(element_name);
            }

            ArrayList<Nutrient> product_nutrients = new ArrayList<>();

            createNutrients(product_nutrients, JSONNutrients);
            product = new Product(
                    product_elements_result.get(0),
                    product_elements_result.get(1),
                    product_elements_result.get(2),
                    product_nutrients,
                    product_elements_result.get(3),
                    product_elements_result.get(4));
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        return product;
    }

    public static void createNutrients(ArrayList<Nutrient> nutrients, JSONObject JSONNutrients) {
        try {
            String[] nutrients_name = {"Energy", "Total fat", "Saturated fat",
                    "Dietary fiber", "Total carbohydrate", "Sugars",
                    "Protein", "Salt", "Calcium", "Sodium"};

            String[] JSONElements_100g = {"energy_value", "fat_100g", "saturated-fat_100g",
                    "fiber_100g", "carbohydrates_100g", "sugars_100g",
                    "proteins_100g", "salt_100g", "calcium_100g", "sodium_100g"};

            String[] JSONElements_portion = {"energy_serving",
                    "fat_serving", "saturated-fat_serving", "fiber_serving",
                    "carbohydrates_serving", "sugars_serving", "proteins_serving",
                    "salt_serving", "calcium_serving", "sodium_serving"};

            String[] JSONElements_unit = {"kcal", "fat_unit", "saturated-fat_unit", "fiber_unit",
                    "carbohydrates_unit", "sugars_unit", "proteins_unit", "salt_unit",
                    "calcium_unit", "sodium_unit"};

            int loop_length = nutrients_name.length;
            for (int i = 0; i < loop_length; i++) {
                String current_nutrient_per100g;
                if (JSONNutrients.has(JSONElements_100g[i])) {
                    current_nutrient_per100g = JSONNutrients.getString(JSONElements_100g[i]);
                    if (current_nutrient_per100g.length() == 0) {
                        continue;
                    } else {
                        if (current_nutrient_per100g.contains(".")) {
                            String decimals = current_nutrient_per100g.substring(current_nutrient_per100g.indexOf("."));
                            if (decimals.length() > 2) {
                                double current_nutrient_value = Double.parseDouble(current_nutrient_per100g);
                                current_nutrient_per100g = String.valueOf((double) Math.round(current_nutrient_value * 100) / 100);
                            }
                        }
                    }
                } else {
                    continue;
                }

                String current_nutrient_per_portion;
                if (JSONNutrients.has(JSONElements_portion[i])) {
                    current_nutrient_per_portion = JSONNutrients.getString(JSONElements_portion[i]);
                    if (current_nutrient_per_portion.length() == 0) {
                        current_nutrient_per_portion = "?";
                    } else {
                        if (current_nutrient_per_portion.contains(".")) {
                            String decimals = current_nutrient_per_portion.substring(current_nutrient_per_portion.indexOf("."));
                            if (decimals.length() > 2) {
                                double current_nutrient_value = Double.parseDouble(current_nutrient_per_portion);
                                current_nutrient_per_portion = String.valueOf((double) Math.round(current_nutrient_value * 100) / 100);
                            }
                        }
                    }
                } else {
                    current_nutrient_per_portion = "?";
                }

                String current_nutrient_unit;
                if (JSONNutrients.has(JSONElements_unit[i])) {
                    current_nutrient_unit = JSONNutrients.getString(JSONElements_unit[i]);
                    if (current_nutrient_unit.length() == 0) {
                        if (i == 0) {
                            current_nutrient_unit = "kcal";
                        } else {
                            current_nutrient_unit = "g";
                        }
                    }
                } else {
                    if (i == 0) {
                        current_nutrient_unit = "kcal";
                    } else {
                        current_nutrient_unit = "g";
                    }
                }

                nutrients.add(new Nutrient(nutrients_name[i], current_nutrient_per100g, current_nutrient_per_portion, current_nutrient_unit));
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    public static void saveProduct(Product product, Context context) {
        String upc = product.getUpc(), name = product.getName(), image_resource = product.getImage_resource(), ingredient_list = product.getIngredients(), serving_quantity = product.getServing_quantity();
        ArrayList<Nutrient> nutrients = product.getNutrients();
        String nutrients_string = new Gson().toJson(nutrients);

        if (TextUtils.isEmpty(upc) && TextUtils.isEmpty(name)
                && TextUtils.isEmpty(image_resource) && (nutrients == null || nutrients.size() == 0) && TextUtils.isEmpty(ingredient_list)) {
            return;
        }

        String[] projection = new String[]{
                "upc", "name", "image_resource", "nutrients", "ingredients", "favorite_condition", "serving_quantity"
        };

        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_UPC, upc);
        values.put(COLUMN_PRODUCT_NAME, name);
        values.put(COLUMN_PRODUCT_IMAGE_RESOURCE, image_resource);
        values.put(COLUMN_PRODUCT_NUTRIENTS, nutrients_string);
        values.put(COLUMN_PRODUCT_INGREDIENTS, ingredient_list);
        values.put(COLUMN_PRODUCT_FAVORITE_CONDITION, "true");
        values.put(COLUMN_PRODUCT_SERVING_QUANTITY, serving_quantity);

        Cursor cursor = context.getContentResolver().query(CONTENT_URI, projection, null, null, null);
        boolean condition = false;
        int count = 0;
        assert cursor != null;
        if (cursor.moveToFirst()) {
            do {
                if (upc.equals(cursor.getString(0))) {
                    condition = true;
                } else {
                    count++;
                }
            } while (cursor.moveToNext() && !condition);

            if (cursor.getCount() == count) {
                Uri newUri = context.getContentResolver().insert(CONTENT_URI, values);

                if (newUri == null) {
                    Toast.makeText(context, R.string.product_save_error, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, R.string.product_save_success, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, R.string.product_already_favorite, Toast.LENGTH_SHORT).show();
            }
        } else {
            Uri newUri = context.getContentResolver().insert(CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(context, R.string.product_save_error, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, R.string.product_save_success, Toast.LENGTH_SHORT).show();
            }
        }
        cursor.close();
    }

    public static void deleteProduct(Product product, Context context) {
        String product_upc = product.getUpc();

        String[] projection = new String[]{
                "upc", "name", "image_resource", "nutrients", "ingredients", "favorite_condition", "serving_quantity"
        };

        Cursor cursor = context.getContentResolver().query(CONTENT_URI, projection, null, null, null);
        boolean condition = false;
        int count = 0;
        assert cursor != null;
        if (cursor.moveToFirst()) {
            do {
                if (product_upc.equals(cursor.getString(0))) {
                    condition = true;
                } else {
                    count++;
                }
            } while (cursor.moveToNext() && !condition);

            if (cursor.getCount() != count) {
                int rowsDeleted = context.getContentResolver().delete(CONTENT_URI, COLUMN_PRODUCT_UPC + " = ?", new String[]{product_upc});
                if (rowsDeleted == 0) {
                    Toast.makeText(context, R.string.marking_favorite_product_error, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, R.string.product_unfavorite, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, R.string.favorite_product_not_marked, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, R.string.favorite_product_not_marked, Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }

    public static List<Product> loadFavoriteProducts(Context context) {
        Cursor c = ProductProvider.queryProduct(context);
        ArrayList<Product> products = new ArrayList<>();
        while (c.moveToNext()) {
            Product product = new Product(c.getString(1), c.getString(2),
                    c.getString(3), createNutrientObjects(c.getString(4)), c.getString(5),
                    c.getString(6), c.getString(7));
            products.add(product);
        }
        c.close();

        return products;
    }

    public static ArrayList<Nutrient> createNutrientObjects(String nutrientsJSON) {
        ArrayList<Nutrient> nutrients = new ArrayList<>();

        try {
            JSONArray JSONArrayNutrients = new JSONArray(nutrientsJSON);
            String nutriment_name, nutriment_per_100g, nutriment_per_portion, nutriment_unit;
            for (int i = 0; i < JSONArrayNutrients.length(); i++) {
                JSONObject JSONNutrient = JSONArrayNutrients.getJSONObject(i);
                nutriment_name = JSONNutrient.getString("name");
                nutriment_per_100g = JSONNutrient.getString("per_100g");
                nutriment_per_portion = JSONNutrient.getString("per_serving");
                nutriment_unit = JSONNutrient.getString("unit");
                Nutrient nutrient = new Nutrient(nutriment_name, nutriment_per_100g, nutriment_per_portion, nutriment_unit);
                nutrients.add(nutrient);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return nutrients;
    }
}