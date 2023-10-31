package org.example;

import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;


import java.net.URI;

import java.util.concurrent.Callable;

import static java.lang.System.out;

public class IpAddressScanner implements Callable<String> {
    private static final String GOOGLE_COM_IP ="216.58.211.238";
    private static final String GOOGLE_COM ="google.com";

    private static final String SPECIFIED_IP="51.38.24.1";
    final private String ipAddress;
    final private ReportCreator reportPrinter;
    public IpAddressScanner(String ipAddress, ReportCreator reportPrinter) {
        this.ipAddress = ipAddress;
        this.reportPrinter = reportPrinter;
    }
    @Override
    public String call() throws Exception {
        if(!ipAddress.endsWith(".1")) { return null;}
        
        String report = "IP-address=" + ipAddress + '\n' +
            (getCertificate() ?
                getDomainList() : "There is no SSL certificate for this IP address\n"
            );
        reportPrinter.write(report);
        return report;
        }


    private boolean getCertificate() {
        out.println("getCertificate() INVOKED");
        try {
            URI uri = new URIBuilder()
                    .setScheme("https")
                    .setHost(GOOGLE_COM)
                    .build();
            Certificate[] peerCertificates = obtainSSLInfo(uri);
            printCertificates(peerCertificates);
        } catch (Exception e) {
            e.printStackTrace(out);
        }


        return false;
    }

    private String getDomainList() {
        return null;
    }

    public static final String PEER_CERTIFICATES = "PEER_CERTIFICATES";

    private Certificate[] obtainSSLInfo(URI uri) throws IOException {

        // create http response certificate interceptor
        HttpResponseInterceptor certificateInterceptor = (httpResponse, context) -> {
            ManagedHttpClientConnection routedConnection
                    = (ManagedHttpClientConnection) context
                    .getAttribute(HttpCoreContext.HTTP_CONNECTION);
            SSLSession sslSession = routedConnection.getSSLSession();
            if (sslSession != null) {

                // get the server certificates from the {@Link SSLSession}
                Certificate[] certificates = sslSession.getPeerCertificates();

                // add the certificates to the context, where we can later grab it from
                context.setAttribute(PEER_CERTIFICATES, certificates);
            }
        };

        // create closable http client and assign the certificate interceptor

        try (CloseableHttpClient httpClient = HttpClients
                .custom()
                .addInterceptorLast(certificateInterceptor)
                .build()) {

            // make HTTP GET request to resource server
            HttpGet httpget = new HttpGet(uri);
            out.println("Executing request " + httpget.getRequestLine());

            // create http context where the certificate will be added
            HttpContext context = new BasicHttpContext();
            httpClient.execute(httpget, context);

            // obtain the server certificates from the context
            return (Certificate[]) context.getAttribute(PEER_CERTIFICATES);
        }
    }
    public void printCertificates(Certificate[] peerCertificates) {
            if(peerCertificates.length == 0) {
                out.println("There is no certificate for this IP");
                return;
            }
            // loop over certificates and print meta-data
            for (Certificate certificate : peerCertificates) {
                X509Certificate real = (X509Certificate) certificate;
                out.println("----------------------------------------");
                out.println("Type: " + real.getType());
                out.println("Signing Algorithm: " + real.getSigAlgName());
                out.println("IssuerDN Principal: " + real.getIssuerX500Principal());
                out.println("SubjectDN Principal: " + real.getSubjectX500Principal());
                out.println("Not After: " + DateUtils.formatDate(real.getNotAfter(), "dd-MM-yyyy"));
                out.println("Not Before: " + DateUtils.formatDate(real.getNotBefore(), "dd-MM-yyyy"));
            }
    }

}


