package com.bogdanstanga.httphelper;

import android.os.AsyncTask;

import com.bogdanstanga.httphelper.interfaces.OnRequestListener;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.InflaterInputStream;

public class HttpRequest extends AsyncTask<Void, Integer, Object> {

    private String mUrl;
    private String mMethod = HttpHelper.GET;
    private HashMap<String, String> mParameters = new HashMap<>();
    private HashMap<String, String> mHeaders = new HashMap<>();
    private boolean mGZIPDecoding = false;

    private OnRequestListener mOnRequestListener;

    private static final String ENCODING = "UTF-8";

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public void setMethod(String method) {
        this.mMethod = method;
    }

    public void setListener(OnRequestListener onRequestListener) {
        this.mOnRequestListener = onRequestListener;
    }

    public void addParameter(String key, String value) {
        this.mParameters.put(key, value);
    }

    public void addHeader(String key, String header) {
        this.mHeaders.put(key, header);
    }

    public void setGZIPDecoding(boolean gzipDecoding) {
        this.mGZIPDecoding = gzipDecoding;
    }

    public void load() throws RuntimeException {
        if (mUrl == null) {
            throw new RuntimeException("The http request does not have any URL specified.");
        } else {
            this.execute();
        }
    }

    @Override
    protected Object doInBackground(Void... parameters) {
        try {
            if (mMethod.equalsIgnoreCase(HttpHelper.GET)) {
                String params = getRequestParams();
                if (params.length() > 0) {
                    mUrl += "?" + params;
                }
            }
            URL url = new URL(mUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            for (Map.Entry<String, String> entry : mHeaders.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
            conn.setDoInput(true);
            conn.setDoOutput(false);
            if (mMethod.equalsIgnoreCase(HttpHelper.POST)) {
                conn.setConnectTimeout(60 * 1000);
                conn.setRequestMethod(mMethod);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, ENCODING));
                writer.write(getRequestParams());
                writer.flush();
                writer.close();
                os.close();
            }
            conn.connect();
            InputStream in = new BufferedInputStream(conn.getInputStream());
            String stringResult = mGZIPDecoding ? decodeGZIP(in) : IOUtils.toString(in, ENCODING);
            conn.disconnect();
            return stringResult;
        } catch (Exception e) {
            return e;
        }
    }

    @Override
    protected void onPostExecute(Object object) {
        super.onPostExecute(object);
        if (object instanceof Exception) {
            Exception e = (Exception) object;
            e.printStackTrace();
            if (mOnRequestListener != null) {
                mOnRequestListener.onFailure(e);
            }
        } else if (mOnRequestListener != null) {
            mOnRequestListener.onSuccess((String) object);
        }
    }

    private String getRequestParams() throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : mParameters.entrySet()) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }
            result.append(URLEncoder.encode(entry.getKey(), ENCODING));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), ENCODING));
        }
        return result.toString();
    }

    public static String decodeGZIP(InputStream inputStream) throws IOException {
        InflaterInputStream in = new InflaterInputStream(inputStream);
        ByteArrayOutputStream bout = new ByteArrayOutputStream(512);
        int b;
        while ((b = in.read()) != -1) {
            bout.write(b);
        }
        bout.close();
        return bout.toString();
    }

}
