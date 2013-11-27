package com.hylps.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPDataRetriever {
    public static void getUrl(String url) {
        HttpURLConnection httpConnection = null;

        InputStream in = null;
        try {
            URL _url = new URL(url);
            httpConnection = (HttpURLConnection) _url.openConnection();
            in = httpConnection.getInputStream();
        } catch (Exception ex) {
            throw new RuntimeException("Getting data error. URL: " + url, ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }
    }
}