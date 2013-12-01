package com.hylps.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTTPDataRetriever {

    public static String getUrl(String url) {
        HttpURLConnection httpConnection = null;

        StringBuilder response = new StringBuilder();
        BufferedReader br = null;
        try {
            URL _url = new URL(url);
            httpConnection = (HttpURLConnection) _url.openConnection();

            InputStream in = httpConnection.getInputStream();
            br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
                response.append('\n');
            }
        } catch (Throwable ex) {
            throw new RuntimeException("Getting data error. URL: " + url, ex);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }
        return response.toString();
    }
}