package com.edpsun.st.monitor.configuration;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;

public class HttpClientProvider {
    @Bean
    public CloseableHttpClient closeableHttpClient() {
        final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        // Increase max total connection to 200
        cm.setMaxTotal(200);

        // Increase default max connection per route to 20
        cm.setDefaultMaxPerRoute(20);

        final CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .build();

        return httpClient;
    }
}
