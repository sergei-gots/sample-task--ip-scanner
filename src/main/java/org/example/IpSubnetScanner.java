package org.example;

import static java.lang.System.out;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.commons.net.util.SubnetUtils;

public class IpSubnetScanner {
    void scan(SubnetUtils subnetUtils, int threadCount, ReportCreator reportPrinter) {

        if (threadCount < 0 || threadCount > 8 ) {
            throw new IllegalArgumentException("threadCount must be >=1 and <=8. Actual value=" + threadCount);
        }

        SubnetUtils.SubnetInfo subnetInfo = subnetUtils.getInfo();

        String[] addresses = subnetInfo.getAllAddresses();
        out.println("Subnet address=" + subnetInfo.getAddress());
        out.println("Low address=" + subnetInfo.getLowAddress());
        out.println("High address=" + subnetInfo.getHighAddress());
        out.println("Address count="+subnetInfo.getAddressCountLong());

        ThreadPoolExecutor threadPoolExecutor
                = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCount);
        for (String address : addresses) {
            threadPoolExecutor.submit(new IpAddressScanner(address, reportPrinter));
        }

        threadPoolExecutor.shutdown();
        while (!threadPoolExecutor.isTerminated()) {
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
                e.printStackTrace(out);
            }
            System.out.print('.');
        }
        System.out.println("\nScan completed");
    }
}
