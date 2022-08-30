package com.gmail.edpsun.hystock.inbound.collect;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service("HTTPDataRetriever")
public class HTTPDataRetriever implements DataRetriever {
    public static List<String> USER_AGENT_STRINGS = Arrays.asList(
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36",
            "Mozilla/5.0 (X11; Linux x86_64; rv:104.0) Gecko/20100101 Firefox/104.0",
            "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:104.0) Gecko/20100101 Firefox/104.0",
            "Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:104.0) Gecko/20100101 Firefox/104.0",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 12.5; rv:104.0) Gecko/20100101 Firefox/104.0",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:104.0) Gecko/20100101 Firefox/104.0",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36 OPR/90.0.4480.54");
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
            // Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.0.0.1", 8080));
            httpConnection = (HttpURLConnection) _url.openConnection();
            httpConnection.setConnectTimeout(15 * 1000);

            httpConnection.setRequestProperty("User-Agent", getRandom(USER_AGENT_STRINGS));

            if (url.contains("sina")) {
                httpConnection.setRequestProperty("Referer", "https://quotes.sina.cn");
            }

            if (url.contains("jrj")) {
                httpConnection.setRequestProperty("Host", "stock.jrj.com.cn");
            }

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

    private static String getRandom(List<String> list) {
        int rnd = new Random().nextInt(list.size());
        return list.get(rnd);
    }
}
