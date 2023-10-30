package org.example;

import java.util.concurrent.Callable;

public class IpAddressScanner implements Callable<String> {
    final private String ipAddress;
    final private ReportPrinter reportPrinter;
    public IpAddressScanner(String ipAddress, ReportPrinter reportPrinter) {
        this.ipAddress = ipAddress;
        this.reportPrinter = reportPrinter;
    }
    @Override
    public String call() throws Exception {
        String report = "IP-address=" + ipAddress + '\n' +
            (getCertificate() ?
                getDomainList() : "There is no SSL certificate for this IP address\n"
            );
        reportPrinter.write(report);
        return report;
        }


    private boolean getCertificate() {
        return false;
    }
    private String getDomainList() {
        return null;
    }

}
