package com.hylps.alarm;

import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.hylps.image.ImageAnalyzer;
import com.hylps.image.ImageHelper;

public class ImageFingerPringRetriever {
    private static final String HOST = System.getProperty("MONITOR_HOST", "localhost");
    private static final String INTERVAL = System.getProperty("INTERVAL", "10");
    private static final String SNAPSHOT_URL = "http://" + HOST + ":9000/?action=snapshot";
    private final BlockingQueue<String> queue;
    private ScheduledExecutorService service;
    private int interval = Integer.parseInt(INTERVAL); // in second

    public ImageFingerPringRetriever(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    public void start() {
        System.out.println(" - URL           : " + SNAPSHOT_URL);
        System.out.println(" - Interval      : " + interval);
        ScheduledExecutorService service = Executors.newScheduledThreadPool(2);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    BufferedImage image = ImageHelper.readImageByUrl(SNAPSHOT_URL);
                    String s = ImageAnalyzer.getInstance().produceFingerPrint(image);
                    queue.put(s);
                    System.out.println(" - Get one snapshot: " + s);
                } catch (Throwable e) {
                    System.err.println("Error while running task to get image finger print.");
                }
            }
        };

        service.scheduleAtFixedRate(task, 10, getInterval(), TimeUnit.SECONDS);
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

}
