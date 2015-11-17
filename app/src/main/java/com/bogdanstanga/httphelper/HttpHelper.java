package com.bogdanstanga.httphelper;

import com.bogdanstanga.httphelper.interfaces.OnRequestListener;

public class HttpHelper {

    private HttpRequest mHttpRequest;

    public static final String POST = "POST";
    public static final String GET = "GET";

    public static HttpHelper create(String url) {
        return new HttpHelper(url);
    }

    private HttpHelper(String url) {
        this.mHttpRequest = new HttpRequest();
        this.mHttpRequest.setUrl(url);
    }

    public HttpHelper setMethod(String method) {
        mHttpRequest.setMethod(method);
        return this;
    }

    public HttpHelper setListener(OnRequestListener onRequestListener) {
        mHttpRequest.setListener(onRequestListener);
        return this;
    }

    public HttpHelper addParameter(String key, String value) {
        mHttpRequest.addParameter(key, value);
        return this;
    }

    public HttpHelper addHeader(String key, String value) {
        mHttpRequest.addHeader(key, value);
        return this;
    }

    public HttpHelper setGZIPDecoding(boolean gzipDecoding) {
        mHttpRequest.setGZIPDecoding(gzipDecoding);
        return this;
    }

    public void execute() throws RuntimeException {
        mHttpRequest.load();
    }

}
