package com.gmail.edpsun.hystock.inbound.collect;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.URL;

@Service("LocalFileReader")
public class LocalFileReader implements DataRetriever {
    @Override
    public String getData(String url) {
        return getData(url, UTF_8);
    }

    @Override
    public String getData(String strUrl, String encoding) {
        try {
            URL url = new URL(strUrl);

            final String content = FileUtils.readFileToString(new File(url.getFile()), encoding);
            return content;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
