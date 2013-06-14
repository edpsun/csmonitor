package com.gmail.edpsun.hystock.select;

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

import com.gmail.edpsun.hystock.inbound.InboundContext;
import com.gmail.edpsun.hystock.inbound.collect.InboundCollector;
import com.gmail.edpsun.hystock.inbound.collect.InboundCollector.Quarter;
import com.gmail.edpsun.hystock.intf.Processor;

public class Main {

    public static InboundContext initCLIOptions(String[] args) {
        Option all = new Option("a", "keep all stocks");

        Option schema = OptionBuilder.withArgName("schema_name").hasArg()
                .withDescription("schema name used to idenfied a filtering").create("s");

        schema.setRequired(true);
        schema.setLongOpt("schema");

        Option ebkfile = OptionBuilder.withArgName("file").hasArg().withDescription("TDX ebk file").create("ebk");
        ebkfile.setRequired(true);

        Options options = new Options();
        options.addOption(schema);
        options.addOption(ebkfile);
        options.addOption(all);

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
        if (line.hasOption("s")) {
            inboundCtx.setSchema(line.getOptionValue("s"));
        }

        if (line.hasOption("ebk")) {
            inboundCtx.setEbk(line.getOptionValue("ebk"));
        }

        if (line.hasOption("a")) {
            inboundCtx.setKeepAll(true);
        } else {
            inboundCtx.setKeepAll(false);
        }
        return inboundCtx;
    }

    private static void printUsage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("HYStock", options);
    }

    public static void main(String[] args) {
        InboundContext inboundCtx = initCLIOptions(args);
        System.out.println("=====================================================");
        System.out.println("  EBK      : " + inboundCtx.getEbk());
        System.out.println("  Schema   : " + inboundCtx.getSchema());
        System.out.println("  Keep all : " + inboundCtx.getKeepAll());
        System.out.println("=====================================================");

        if (!new File(inboundCtx.getEbk()).exists()) {
            System.out.println("[Error] Stock list does not exist.");
            System.exit(-1);
        }

        File work = new File(inboundCtx.getSchema());
        if (work.exists()) {
            System.out.println("[Error] work dir already exists: " + inboundCtx.getSchema());
            System.exit(-1);
        } else {
            System.out.println(work.getAbsolutePath());
            work.mkdirs();
        }

        ApplicationContext ctx = new ClassPathXmlApplicationContext("spring/app-beans.xml");
        Processor selector = ctx.getBean("selector", Processor.class);
        selector.process(inboundCtx);
    }

}
