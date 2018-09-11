package com.udacity.safebites.entities;

public class SimplifiedNutrient {
    /**
     * Nutrient name.
     */
    private final String name;

    /**
     * Quantity of the nutrient of product A per 100 grams.
     */
    private final String quantityA;

    /**
     * Quantity of the nutrient of product B per 100 grams.
     */
    private final String quantityB;

    /**
     * Unit of measurement of the nutrient.
     */
    private final String unit;

    public SimplifiedNutrient(String name, String quantityA, String quantityB, String unit) {
        this.name = name;
        this.quantityA = quantityA;
        this.quantityB = quantityB;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public String getQuantityA() {
        return quantityA;
    }

    public String getQuantityB() {
        return quantityB;
    }

    public String getUnit() {
        return unit;
    }
}