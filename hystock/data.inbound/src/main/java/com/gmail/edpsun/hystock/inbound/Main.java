package com.gmail.edpsun.hystock.inbound;

import java.io.File;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.gmail.edpsun.hystock.inbound.collect.InboundCollector;
import com.gmail.edpsun.hystock.inbound.collect.InboundCollector.Quarter;
import com.gmail.edpsun.hystock.intf.Processor;

public class Main {
    public static InboundContext initCLIOptions(String[] args) {
        Option quarter = OptionBuilder.withArgName("quarter like 2013-01").hasArg()
                .withDescription("update to the target quarter").create("q");

        quarter.setRequired(true);
        quarter.setLongOpt("quarter");

        Option ebkfile = OptionBuilder.withArgName("file").hasArg().withDescription("TDX ebk file").create("ebk");
        ebkfile.setRequired(true);

        Options options = new Options();
        options.addOption(quarter);
        options.addOption(ebkfile);

        // create the parser
        CommandLineParser parser = new BasicParser();
        CommandLine line = null;
        try {
            // parse the command line arguments
            line = parser.parse(options, args);
        } catch (ParseException exp) {
            printUsage(options);
            throw new RuntimeException(exp);
        }

        InboundContext inboundCtx = new InboundContext();
        if (line.hasOption("q")) {
            inboundCtx.setQuarter(Quarter.valueOf(line.getOptionValue("q")));
        }

        if (line.hasOption("ebk")) {
            inboundCtx.setEbk(line.getOptionValue("ebk"));
        }
        return inboundCtx;
    }

    private static void printUsage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("HYStock", options);
    }

    public static void main(String[] args) {
        InboundContext inboundCtx = initCLIOptions(args);
        System.out.println(inboundCtx.getEbk());

        if (!new File(inboundCtx.getEbk()).exists()) {
            System.out.println("[Error] Stock list does not exist.");
            System.exit(-1);
        }

        ApplicationContext ctx = new ClassPathXmlApplicationContext("spring/app-beans.xml");
        Processor collector = ctx.getBean("inboundCollector", Processor.class);
        collector.process(inboundCtx);
    }
}
