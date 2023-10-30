package org.example;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.commons.net.util.SubnetUtils;

public class IpSubnetScanner {
    void scan(SubnetUtils subnetUtils, int threadCount, ReportPrinter reportPrinter) {

        if (threadCount < 0 || threadCount > 8 ) {
            throw new IllegalArgumentException("threadCount must be >=1 and <=8. Actual value=" + threadCount);
        }

        SubnetUtils.SubnetInfo subnetInfo = subnetUtils.getInfo();

        String[] addresses = subnetInfo.getAllAddresses();
        System.out.println("Subnet address=" + subnetInfo.getAddress());
        System.out.println("Low address=" + subnetInfo.getLowAddress());
        System.out.println("High address=" + subnetInfo.getHighAddress());
        System.out.println("Address count="+subnetInfo.getAddressCountLong());

        ThreadPoolExecutor threadPoolExecutor
                = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCount);
        for (String address : addresses) {
            threadPoolExecutor.submit(new IpAddressScanner(address, reportPrinter));
        }
        threadPoolExecutor.shutdown();
        while (!threadPoolExecutor.isTerminated()) {
            System.out.print('.');
        }
        System.out.println("\nScan completed");
    }
}
