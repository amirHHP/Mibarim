package com.mibarim.main.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Hamed on 5/17/2016.
 */
public class CheckNetworkConnection {
    public static boolean isNetworkAvailable(Context _context) {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean res=(activeNetworkInfo != null && activeNetworkInfo.isConnected());
        return res;
    }
}
