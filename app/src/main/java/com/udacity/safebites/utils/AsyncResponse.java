package com.udacity.safebites.utils;

import com.udacity.safebites.entities.Product;

public interface AsyncResponse {
    void processFinish(Product output);
}
