package com.udacity.safebites.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProductApi {
    /**
     *
     */
    private static Retrofit sRetrofit = null;

    public static Retrofit getClient() {
        if (sRetrofit == null) {
            sRetrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://world.openfoodfacts.org/cgi/")
                    .build();
        }
        return sRetrofit;
    }
}