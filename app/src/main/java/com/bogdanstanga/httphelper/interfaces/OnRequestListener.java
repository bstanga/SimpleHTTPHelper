package com.bogdanstanga.httphelper.interfaces;

/**
 * Created by Bogdan on 11/15/2015.
 */
public interface OnRequestListener {
    void onSuccess(String response);

    void onFailure(Exception e);
}
