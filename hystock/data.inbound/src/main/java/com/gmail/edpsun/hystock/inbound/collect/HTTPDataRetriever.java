package com.gmail.edpsun.hystock.inbound.collect;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service("HTTPDataRetriever")
public class HTTPDataRetriever implements DataRetriever {
    public static Logger LOGGER = Logger.getLogger(HTTPDataRetriever.class);

    @Override
    public String getData(final String url) {
        return getData(url, GBK);
    }

    @Override
    public String getData(final String url, final String encoding) {
        LOGGER.debug(url);

        HttpURLConnection httpConnection = null;

        final StringBuilder response = new StringBuilder();
        try {
            final URL _url = new URL(url);
            httpConnection = (HttpURLConnection) _url.openConnection();
            httpConnection.setConnectTimeout(15 * 1000);

            httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

            final InputStream in = httpConnection.getInputStream();
            final BufferedReader br = new BufferedReader(new InputStreamReader(in, encoding));

            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
                response.append('\n');
            }

            br.close();
        } catch (final Exception ex) {
            throw new RuntimeException("Getting data error. URL: " + url, ex);
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }

        return response.toString();
    }
}
