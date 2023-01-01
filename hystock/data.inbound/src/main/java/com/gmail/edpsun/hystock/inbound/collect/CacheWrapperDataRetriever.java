package com.gmail.edpsun.hystock.inbound.collect;

import org.apache.commons.jcs3.JCS;
import org.apache.commons.jcs3.access.CacheAccess;
import org.apache.commons.jcs3.access.exception.CacheException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service("CacheWrapperDataRetriever")
public class CacheWrapperDataRetriever implements DataRetriever {
    public static Logger LOGGER = Logger.getLogger(CacheWrapperDataRetriever.class);
    private static CacheAccess<String, String> cache = null;

    static {
        try {
            cache = JCS.getInstance("stock");
        } catch (final CacheException e) {
            System.out.println(String.format("Problem initializing cache: %s", e.getMessage()));
        }
    }

    private final Random random = new Random();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Resource(name = "HTTPDataRetriever")
    DataRetriever httpDataRetriever;

    @Resource(name = "LocalFileReader")
    DataRetriever localFileReader;

    @Override
    public String getData(final String url) {
        final String key = getKey(url);
        return cache.get(key, () -> {
            LOGGER.info("Retrieve data through HTTP...");
            sleep();
            if (url.startsWith("file:")) {
                return localFileReader.getData(url);
            } else {
                return httpDataRetriever.getData(url);
            }
        });
    }

    @Override
    public String getData(final String url, final String encoding) {
        final String key = getKey(url);
        return cache.get(key, () -> {
            LOGGER.info("Retrieve data through HTTP...");
            sleep();
            if (url.startsWith("file:")) {
                return localFileReader.getData(url, encoding);
            } else {
                return httpDataRetriever.getData(url, encoding);
            }
        });
    }

    private void sleep() {
        //dirty code change to have sleep here.
        try {
            Thread.currentThread().sleep(random.nextInt(1000));
        } catch (final InterruptedException e) {
            // ignore
        }
    }

    private String getKey(final String url) {
        final LocalDateTime now = LocalDateTime.now();
        return String.format("%s-%s", DigestUtils.md5DigestAsHex(url.getBytes()), dtf.format(now));
    }
}
