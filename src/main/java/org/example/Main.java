package org.example;

import org.apache.commons.net.util.SubnetUtils;

import static java.lang.System.out;

public class Main {
    public static void main(String[] args) {

        out.println("IP scanner test task");

        SubnetUtils subnetUtils = new SubnetUtils("51.38.24.0/24");

        ReportCreator reportPrinter = new ReportCreator();
        try {
            reportPrinter.init("scan-report.txt");
            new IpSubnetScanner().scan(subnetUtils, 8, reportPrinter);
        }
        catch(Exception e) {
            e.printStackTrace(out);
        }
        finally {
            reportPrinter.close();
        }
    }
}