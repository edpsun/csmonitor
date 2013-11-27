package com.hylps.alarm;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class KidAlarm {

    public static void main(String[] args) {
        BlockingQueue<String> q = new ArrayBlockingQueue<String>(100);
        ImageFingerPringRetriever retriever = new ImageFingerPringRetriever(q);
        // retriever.setInterval(5);
        retriever.start();

        FingerPrintAnalyzer analyzer = new FingerPrintAnalyzer(q);
        analyzer.analyze();

        try {
            new ServerSocket(33333);
        } catch (IOException e) {
            System.out.println("Please do NOT start dup job.");
            System.exit(-1);
        }
    }
}
