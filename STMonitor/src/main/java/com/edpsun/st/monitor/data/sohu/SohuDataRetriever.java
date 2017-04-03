package com.edpsun.st.monitor.data.sohu;

import com.edpsun.st.monitor.data.DataRetriever;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class SohuDataRetriever implements DataRetriever {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String HTTP_PROTOCOL = "http";
    private static final String HOST = "q.stock.sohu.com";
    private static final String PATH = "/hisHq";

    @NonNull
    private CloseableHttpClient httpClient;

    @Override
    public String getData(final @NonNull String id, final @NonNull LocalDate startDate, final @NonNull LocalDate endDate) {
        final URI uri;
        try {
            uri = getUri(id, startDate, endDate);
            final HttpGet httpGet = new HttpGet(uri);

            final CloseableHttpResponse response = httpClient.execute(httpGet);
            try {
                return EntityUtils.toString(response.getEntity());
            } finally {
                response.close();
            }
        } catch (final URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private URI getUri(final String code, final LocalDate start, final LocalDate end) throws URISyntaxException {
        final URI uri = new URIBuilder()
                .setScheme(HTTP_PROTOCOL)
                .setHost(HOST)
                .setPath(PATH)
                .setParameter("code", "cn_" + code)
                .setParameter("start", start.format(FORMATTER))
                .setParameter("end", end.format(FORMATTER))
                .setParameter("stat", "1")
                .setParameter("order", "D")
                .setParameter("period", "d")
                .build();
        return uri;
    }
}
