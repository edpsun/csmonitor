package com.gmail.edpsun.hystock.inbound;

import java.io.File;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.gmail.edpsun.hystock.inbound.collect.InboundCollector;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("[Error] Please specify stock list.");
            System.exit(-1);
        }
        String path = args[0];
        System.out.println(path);

        if (!new File(path).exists()) {
            System.out.println("[Error] Stock list does not exist.");
            System.exit(-1);
        }

        ApplicationContext ctx = new ClassPathXmlApplicationContext("spring/app-beans.xml");
        InboundCollector collector = ctx.getBean("inboundCollector", InboundCollector.class);
        collector.collect(path);
    }
}
