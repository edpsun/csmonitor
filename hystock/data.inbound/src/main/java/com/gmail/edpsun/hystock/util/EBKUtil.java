package com.gmail.edpsun.hystock.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class EBKUtil {
    private static final Logger LOGGER = Logger.getLogger(EBKUtil.class);

    public static List<String> getStockList(String path) {
        ArrayList<String> list = new ArrayList<String>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line = null;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.length() == 0 || line.indexOf("999999") > -1 || line.indexOf("399001") > -1) {
                    continue;
                }

                if (line.length() == 7) {
                    list.add(line.substring(1));
                } else if (line.length() == 6) {
                    list.add(line);
                } else {
                    LOGGER.warn("[SKIP] Wrong format of stock id: " + line);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("[Error] Can not read stock list file. Path: " + path);
        }

        return list;
    }
}
