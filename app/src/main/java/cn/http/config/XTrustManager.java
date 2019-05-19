package cn.http.config;

import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * Created by lan on 2017/5/24.
 */

public class XTrustManager implements X509TrustManager {

    @Override
    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)  {
    }

    @Override
    public void checkServerTrusted(
            java.security.cert.X509Certificate[] chain,
            String authType) {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        X509Certificate[] x509Certificates = new X509Certificate[0];
        return x509Certificates;
    }
}
