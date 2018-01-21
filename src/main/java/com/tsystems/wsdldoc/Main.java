package com.tsystems.wsdldoc;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Entry point.
 * CLI for using as "java -jar jar_name params"
 *
 * By: Alexey Matveev
 * Date: 25.08.2016
 * Time: 16:38
 */
public class Main {

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();

//        args = new String[4];
//        String serviceName = "fulfillment";
//        args[0] = "-f=" + serviceName + ".html";
//        args[1] = "-d=gen";
//        args[2] = "-s=http://localhost:10002/ecompany/services/" + serviceName + "?wsdl";
//        args[2] = "-s=http://localhost:10002/tibet/services/" + serviceName + "?wsdl";
//        args[2] = "-s=http://localhost:10002/tibet/b2b/services/" + serviceName + "?wsdl";
//        args[2] = "-s=http://localhost:10002/ecompany/services/bulkExport?wsdl";
//        args[3] = "-s=http://localhost:10002/ecompany/services/getContextData?wsdl";
//        args[4] = "-s=http://localhost:10002/ecompany/services/fulfillment?wsdl";
//        args[5] = "-s=http://localhost:10002/ecompany/services/onlineRead?wsdl";
//        args[6] = "-s=http://localhost:10002/ecompany/services/getTariffRights?wsdl";
//        args[3] = "-t=B2B";

        // parse input parameters
        Options options = new Options();
        options.addOption("d", "destination", true, "the destination folder of documentation (absolute or relative), it will generate index.html and statics in this folder (there should be write access to the folder)");
        options.addOption("f", "filename", true, "the destination file name (default is index.html)");
        options.addOption("h", "help", false, "shows this help output");
        options.addOption("s", "source", true, "one or multiple URLs with source WSDLs location; the schemas in WSDL's should have schemaLocation in order to correctly generate all the types");
        options.addOption("t", "title", true, "the title of the documentation, like \"eCompany\" (WSDL by default)");

        CommandLineParser cmdparser = new DefaultParser();
        CommandLine cmd = cmdparser.parse(options, args);

        if (args.length == 0 || cmd.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "wsdldoc", options );
        } else {
            // current directory by default
            Path destination = Paths.get(".");
            if (!cmd.hasOption("d")) {
                System.out.println("# The documentation will be generated in current directory.");
            } else {
                Path dPath = Paths.get(cmd.getOptionValue("d"));
                if (!Files.exists(dPath)) {
                    Files.createDirectories(dPath);
                }
                destination = dPath;
            }
            // default filename is "index.html"
            String filename = "index.html";
            if (!cmd.hasOption("f")) {
                System.out.println("# The default filename \"index.html\" will be used");
            } else {
                filename = cmd.getOptionValue("f");
            }
            // wsdl locations is a mandatory parameter
            String[] sourceWsdlLocations;
            if (!cmd.hasOption("s")) {
                System.out.println("# You have provided no source WSDL's to process");
                return;
            } else {
                sourceWsdlLocations = cmd.getOptionValues("s");
            }
            // title is "WSDL documentation" by default
            String title = "WSDL";
            if (!cmd.hasOption("t")) {
                System.out.println("# default title \"WSDL documentation\" will be used");
            } else {
                title = cmd.getOptionValue("t");
            }

            File outputFile = new File(destination.toString() + File.separator + filename);

            // generate the documentation
            DocGenerator.generateDoc(sourceWsdlLocations, outputFile, title);

            // copy files in "static" folder
            copyStaticFiles("bootstrap.min.css", destination.toString());
            copyStaticFiles("wsdl_style.css", destination.toString());
            copyStaticFiles("complex-icon.png", destination.toString());
            copyStaticFiles("complex-icon-orange.png", destination.toString());
            copyStaticFiles("simple-icon.png", destination.toString());
            copyStaticFiles("simple-icon-cube.png", destination.toString());
            copyStaticFiles("simple-icon-orange.png", destination.toString());

            System.out.println("--");
            long end = System.currentTimeMillis();
            System.out.println("Documentation generated in " + (end - start) / 1000 + "s");
            System.out.println("The index file location is: " + outputFile.getAbsolutePath());
        }

    }

    /**
     * Copies the static files from jar (IDE) to target bundle.
     * @param fileName
     * @param destination
     * @throws Exception
     */
    public static void copyStaticFiles(String fileName, String destination) throws Exception {
//        String jarFolder = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getPath().replace('\\', '/');
//        System.out.println("Jar folder: " + jarFolder);
        // for IDE usage
        if (Files.exists(Paths.get("src\\main\\resources\\static" + File.separator + fileName))) {
            Files.copy(Paths.get("src\\main\\resources\\static" + File.separator + fileName), new FileOutputStream(new File(destination + File.separator + fileName)));
        } else {
            // for usage as a JAR
//            System.out.println(Main.class.getClassLoader().getResourceAsStream("wsdldoc.ftl")); // ok
//            System.out.println(Main.class.getClassLoader().getResourceAsStream("static/complex-icon.png")); // ok
            try (InputStream stream = Main.class.getClassLoader().getResourceAsStream("static/" + fileName); OutputStream resStreamOut = new FileOutputStream(destination + File.separator + fileName)) {
                if (stream == null) {
                    throw new Exception("Cannot get resource \"" + fileName + "\" from Jar file.");
                }
                int readBytes;
                byte[] buffer = new byte[4096];
                while ((readBytes = stream.read(buffer)) > 0) {
                    resStreamOut.write(buffer, 0, readBytes);
                }
            }
        }
    }



}
