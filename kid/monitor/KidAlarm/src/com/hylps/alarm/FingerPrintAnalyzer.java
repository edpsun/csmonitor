package com.hylps.alarm;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hylps.image.ImageAnalyzer;
import com.hylps.util.HTTPDataRetriever;

public class FingerPrintAnalyzer {
    private static final String HOST = System.getProperty("MONITOR_HOST", "localhost");
    private static final String ALARM_URL = "http://" + HOST + ":2000/action?name=set_alarm&val=";
    private static final String FPT_URL = "http://" + HOST + ":2000/action?name=fpt";
    private static final String ALARM_THRESHOLD = System.getProperty("ALARM_THRESHOLD", "4");
    private ScheduledExecutorService scheduledService;
    private Executor service;

    private final BlockingQueue<String> queue;
    private final ArrayList<String> footPrints = new ArrayList<String>();
    private transient int alarmThreshold = Integer.parseInt(ALARM_THRESHOLD);

    private boolean currentStat = false;

    public FingerPrintAnalyzer(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    public void analyze() {
        System.out.println(" - AlarmThreshold: " + alarmThreshold);
        System.out.println(" - ALARM_URL     : " + ALARM_URL);
        System.out.println(" - FPT_URL       : " + FPT_URL);

        startAnalyzeExecutor();
        startFPTExecutor();
    }

    Pattern p = Pattern.compile(".*FPT\":\"(.*)\".*");

    private void startFPTExecutor() {
        scheduledService = Executors.newScheduledThreadPool(1);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    String s = HTTPDataRetriever.getUrl(FPT_URL);
                    Matcher m = p.matcher(s);
                    if (m.find()) {
                        int netFPT = Integer.parseInt(m.group(1));
                        if (alarmThreshold != netFPT) {
                            alarmThreshold = netFPT;
                            System.out.println("Get New FPT:" + netFPT);
                        }
                    }
                } catch (Throwable e) {
                    System.err.println("Error while get FPT: " + e.getMessage());
                }
            }
        };

        scheduledService.scheduleAtFixedRate(task, 20, 20, TimeUnit.SECONDS);
    }

    private void startAnalyzeExecutor() {
        service = Executors.newSingleThreadExecutor();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        footPrints.add(0, queue.take());
                        if (footPrints.size() == 1) {
                            continue;
                        }
                        checkDiff();
                        Thread.currentThread().sleep(10);
                    } catch (Throwable e) {
                        System.err.println("Error while analyzing image finger print." + e.getMessage());
                    }
                }
            }
        };

        service.execute(task);
    }

    private void checkDiff() {
        int p = ImageAnalyzer.getInstance().hammingDistance(footPrints.get(0), footPrints.get(1));

        int p2 = 0;
        if (footPrints.size() >= 3) {
            p2 = ImageAnalyzer.getInstance().hammingDistance(footPrints.get(0), footPrints.get(2));
        }

        System.out.println(" (" + alarmThreshold + ")-> P1:" + p + "  P2:" + p2);

        if (p >= alarmThreshold || p2 >= alarmThreshold) {
            notifyAlarm(true);
        } else {
            notifyAlarm(false);
        }
    }

    private void notifyAlarm(boolean isInAlarm) {
        if (currentStat == isInAlarm) {
            return;
        } else {
            currentStat = isInAlarm;
        }

        if (isInAlarm) {
            System.out.println("=> set alarm");
        } else {
            System.out.println("=> cancel alarm");
        }
        try {
            HTTPDataRetriever.getUrl(ALARM_URL + isInAlarm);
        } catch (Exception e) {
            System.out.println("Send Alarm status failed." + e.getMessage());
            e.printStackTrace();
        }

    }

    public int getAlarmThreshold() {
        return alarmThreshold;
    }

    public void setAlarmThreshold(int alarmThreshold) {
        this.alarmThreshold = alarmThreshold;
    }
}
