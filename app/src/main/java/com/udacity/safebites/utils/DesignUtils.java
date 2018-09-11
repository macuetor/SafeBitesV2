package com.udacity.safebites.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class DesignUtils {
    public static void hideSoftKeyboard(Activity activity) {
        if (activity.getCurrentFocus() == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}