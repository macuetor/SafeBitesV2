package com.udacity.safebites.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Product implements Parcelable {
    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    /**
     * UPC of the product.
     */
    @SerializedName("code")
    @Expose
    private String upc;

    /**
     * Name of the product.
     */
    @SerializedName("product_name")
    @Expose
    private String name;

    /**
     * Image resource of the product.
     */
    @SerializedName("image_small_url")
    @Expose
    private String image_resource;

    /**
     * Nutrients of the product (Object format).
     */
    @SerializedName("nutriments")
    @Expose
    private Object nutrientsObject;

    /**
     * Nutrients of the product.
     */
    private ArrayList<Nutrient> nutrients;

    /**
     * Ingredients of the product.
     */
    @SerializedName("ingredients_text")
    @Expose
    private String ingredients;

    /**
     * Favorite condition of the product.
     */
    private String favorite_condition = "false";

    /**
     * Serving quantity of the product.
     */
    private String serving_quantity;

    public Product() {
    }

    public Product(String upc, String name, String image_resource, ArrayList<Nutrient> nutrients, String ingredients, String serving_quantity) {
        this.upc = upc;
        this.name = name;
        this.image_resource = image_resource;
        this.nutrients = nutrients;
        this.ingredients = ingredients;
        this.favorite_condition = "false";
        this.serving_quantity = serving_quantity;
    }

    public Product(String upc, String name, String image_resource, ArrayList<Nutrient> nutrients, String ingredients, String favorite_condition, String serving_quantity) {
        this.upc = upc;
        this.name = name;
        this.image_resource = image_resource;
        this.nutrients = nutrients;
        this.ingredients = ingredients;
        this.favorite_condition = favorite_condition;
        this.serving_quantity = serving_quantity;
    }

    private Product(Parcel in) {
        upc = in.readString();
        name = in.readString();
        image_resource = in.readString();
        nutrients = in.createTypedArrayList(Nutrient.CREATOR);
        ingredients = in.readString();
        favorite_condition = in.readString();
        serving_quantity = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(upc);
        dest.writeString(name);
        dest.writeString(image_resource);
        dest.writeTypedList(nutrients);
        dest.writeString(ingredients);
        dest.writeString(favorite_condition);
        dest.writeString(serving_quantity);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getUpc() {
        return upc;
    }

    public String getName() {
        return name;
    }

    public String getImage_resource() {
        return image_resource;
    }

    public void setImage_resource(String image_resource) {
        this.image_resource = image_resource;
    }

    public Object getNutrientsObject() {
        return nutrientsObject;
    }

    public ArrayList<Nutrient> getNutrients() {
        return nutrients;
    }

    public void setNutrients(ArrayList<Nutrient> nutrients) {
        this.nutrients = nutrients;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getFavorite_condition() {
        return favorite_condition;
    }

    public void setFavorite_condition(String favorite_condition) {
        this.favorite_condition = favorite_condition;
    }

    public String getServing_quantity() {
        return serving_quantity;
    }

    @Override
    public String toString() {
        return name;
    }
}