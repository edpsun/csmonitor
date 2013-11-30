package com.hylps.alarm;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.hylps.image.ImageAnalyzer;
import com.hylps.util.HTTPDataRetriever;

public class FingerPrintAnalyzer {
    private static final String HOST = System.getProperty("MONITOR_HOST", "localhost");
    private static final String ALARM_URL = "http://" + HOST + ":2000/action?name=set_alarm&val=";
    private static final String ALARM_THRESHOLD = System.getProperty("ALARM_THRESHOLD", "4");

    private final BlockingQueue<String> queue;
    private final ArrayList<String> footPrints = new ArrayList<String>();
    private int alarmThreshold = Integer.parseInt(ALARM_THRESHOLD);

    private boolean currentStat = false;

    public FingerPrintAnalyzer(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    public void analyze() {
        System.out.println(" - AlarmThreshold: " + alarmThreshold);
        System.out.println(" - ALARM_URL     : " + ALARM_URL);
        Executor service = Executors.newSingleThreadExecutor();
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
                        System.err.println("Error while analyzing image finger print.");
                    }
                }
            }
        };

        service.execute(task);
    }

    private void checkDiff() {
        int p = ImageAnalyzer.getInstance().hammingDistance(footPrints.get(0), footPrints.get(1));
        
        int p2 = 0;
        if(footPrints.size() >= 3){
            p2 =  ImageAnalyzer.getInstance().hammingDistance(footPrints.get(0), footPrints.get(2));
        }
        
        System.out.println(" -> P1:" + p + "  P2:" + p2);

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
            System.out.println("Send Alarm status failed.");
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
