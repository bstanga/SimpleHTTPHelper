package com.bogdanstanga.httphelper.interfaces;

public interface OnRequestListener {
    void onSuccess(String response);

    void onFailure(Exception e);
}
