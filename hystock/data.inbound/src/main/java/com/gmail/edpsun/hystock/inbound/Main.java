package com.gmail.edpsun.hystock.inbound;

import com.gmail.edpsun.hystock.inbound.collect.InboundCollector.Quarter;
import com.gmail.edpsun.hystock.intf.Processor;
import org.apache.commons.cli.*;
import org.apache.commons.jcs3.JCS;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;

public class Main {
    public static InboundContext initCLIOptions(final String[] args) {
        final Option quarter = OptionBuilder.withArgName("quarter like 2013-01").hasArg()
                .withDescription("update to the target quarter").create("q");

        quarter.setRequired(true);
        quarter.setLongOpt("quarter");

        final Option parser = OptionBuilder.withArgName("Parser Q or H").hasArg().withDescription("specify the site/parser")
                .create("p");
        parser.setLongOpt("parser");

        final Option ebkfile = OptionBuilder.withArgName("file").hasArg().withDescription("TDX ebk file").create("ebk");
        ebkfile.setRequired(true);

        final Option threadNumber = OptionBuilder.withArgName("thread number").hasArg().withDescription("Thread Number").create("t");
        threadNumber.setRequired(true);
        threadNumber.setLongOpt("threadNumber");

        final Options options = new Options();
        options.addOption(quarter);
        options.addOption(ebkfile);
        options.addOption(parser);
        options.addOption(threadNumber);

        // create the parser
        final CommandLineParser cparser = new BasicParser();
        CommandLine line = null;
        try {
            // parse the command line arguments
            line = cparser.parse(options, args);
        } catch (final ParseException exp) {
            printUsage(options);
            throw new RuntimeException(exp);
        }

        final InboundContext inboundCtx = new InboundContext();
        if (line.hasOption("q")) {
            inboundCtx.setQuarter(Quarter.valueOf(line.getOptionValue("q")));
        }

        if (line.hasOption("ebk")) {
            inboundCtx.setEbk(line.getOptionValue("ebk"));
        }

        if (line.hasOption("p")) {
            inboundCtx.setParser(line.getOptionValue("p"));
        }

        if (line.hasOption("t")) {
            inboundCtx.setThreadNumber(Integer.valueOf(line.getOptionValue("t")));
        }

        return inboundCtx;
    }

    private static void printUsage(final Options options) {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("HYStock", options);
    }

    public static void main(final String[] args) {
        final InboundContext inboundCtx = initCLIOptions(args);
        System.out.println(inboundCtx.getEbk());

        if (!new File(inboundCtx.getEbk()).exists()) {
            System.out.println("[Error] Stock list does not exist.");
            System.exit(-1);
        }

        try {
            final ApplicationContext ctx = new ClassPathXmlApplicationContext("spring/app-beans.xml");
            final Processor collector = ctx.getBean("inboundCollector", Processor.class);
            collector.process(inboundCtx);
        } finally {
            JCS.shutdown();
        }
    }
}
