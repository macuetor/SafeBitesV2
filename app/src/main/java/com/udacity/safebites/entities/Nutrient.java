package com.udacity.safebites.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Nutrient implements Parcelable {
    public static final Creator<Nutrient> CREATOR = new Creator<Nutrient>() {
        @Override
        public Nutrient createFromParcel(Parcel in) {
            return new Nutrient(in);
        }

        @Override
        public Nutrient[] newArray(int size) {
            return new Nutrient[size];
        }
    };

    /**
     * Nutrient name.
     */
    private final String name;

    /**
     * Quantity of the nutrient per 100 grams.
     */
    private final String per_100g;

    /**
     * Quantity of the nutrient per serving.
     */
    private final String per_serving;

    /**
     * Unit of measurement of the nutrient.
     */
    private final String unit;

    public Nutrient(String name, String per_100g, String per_serving, String unit) {
        this.name = name;
        this.per_100g = per_100g;
        this.per_serving = per_serving;
        this.unit = unit;
    }

    private Nutrient(Parcel in) {
        name = in.readString();
        per_100g = in.readString();
        per_serving = in.readString();
        unit = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(per_100g);
        dest.writeString(per_serving);
        dest.writeString(unit);
    }

    public String getName() {
        return name;
    }

    public String getPer_100g() {
        return per_100g;
    }

    public String getPer_serving() {
        return per_serving;
    }

    public String getUnit() {
        return unit;
    }
}