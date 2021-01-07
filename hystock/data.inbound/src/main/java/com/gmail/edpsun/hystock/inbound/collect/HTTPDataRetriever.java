package com.gmail.edpsun.hystock.inbound.collect;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class HTTPDataRetriever implements DataRetriever {
    public static Logger LOGGER = Logger.getLogger(HTTPDataRetriever.class);

    public String getData(String url) {
        LOGGER.debug(url);

        HttpURLConnection httpConnection = null;

        StringBuilder response = new StringBuilder();
        try {
            URL _url = new URL(url);
            httpConnection = (HttpURLConnection) _url.openConnection();
            httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

            InputStream in = httpConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "GB2312"));

            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
                response.append('\n');
            }

            br.close();
        } catch (Exception ex) {
            throw new RuntimeException("Getting data error. URL: " + url, ex);
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }

        return response.toString();
    }
}