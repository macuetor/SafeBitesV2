package com.udacity.safebites.api;

import com.udacity.safebites.entities.Products;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ProductService {
    @GET("search.pl")
    Call<Products> getProducts(
            @Query("search_terms") String search_terms,
            @Query("search_simple") int search_simple,
            @Query("action") String action,
            @Query("json") int json,
            @Query("page_size") int page_size,
            @Query("page") int page
    );
}