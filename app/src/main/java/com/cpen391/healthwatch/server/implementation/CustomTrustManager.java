package com.cpen391.healthwatch.server.implementation;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by william on 2018-04-02.
 * A trust manager to trust the HealthWatch server and the default certificates.
 */
public class CustomTrustManager implements X509TrustManager {
    private X509TrustManager mDefaultTrustManger;
    private X509TrustManager mLocalTrustManager;

    CustomTrustManager(KeyStore localKeyStore) throws NoSuchAlgorithmException, KeyStoreException {
        // init default trust manager using the system defaults
        // init local trust manager using localKeyStore
        TrustManagerFactory defaultTrustManagerFactory = TrustManagerFactory.getInstance("X509");
        defaultTrustManagerFactory.init((KeyStore)null);
        TrustManager[] trustManagers = defaultTrustManagerFactory.getTrustManagers();
        mDefaultTrustManger = (X509TrustManager) trustManagers[0];

        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(localKeyStore);
        mLocalTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        throw new CertificateException("check client trust not implemented in CustomTrustManager");
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            mLocalTrustManager.checkServerTrusted(chain, authType);
        } catch (CertificateException ce) {
            mDefaultTrustManger.checkServerTrusted(chain, authType);
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
