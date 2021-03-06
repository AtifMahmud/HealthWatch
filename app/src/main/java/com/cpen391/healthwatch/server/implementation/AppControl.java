package com.cpen391.healthwatch.server.implementation;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.Volley;
import com.cpen391.healthwatch.R;
import com.cpen391.healthwatch.server.abstraction.AppControlInterface;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by william on 2018/3/6.
 *
 */
public class AppControl implements AppControlInterface {
    private static final String TAG = "AppControl";

    private final RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    /**
     * Constructs an application control object that uses SSL to connect to servers.
     * @param context application context.
     */
    public AppControl(Context context) {
        SSLSocketFactory sslSocketFactory = getSSLSocketFactory(context);
        HurlStack hurlStack = getHurlStack(sslSocketFactory);
        mRequestQueue = Volley.newRequestQueue(context, hurlStack);
        mImageLoader = new ImageLoader(mRequestQueue, new ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<>(10);
            @Override
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
        });
    }

    private HurlStack getHurlStack(final SSLSocketFactory sslSocketFactory) {
        if (sslSocketFactory == null) {
            Log.e(TAG, "Unable to use https");
            return null;
        }
        return new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
                try {
                    httpsURLConnection.setSSLSocketFactory(sslSocketFactory);
                    httpsURLConnection.setHostnameVerifier(getHostnameVerifier());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return httpsURLConnection;
            }
        };
    }

    private SSLSocketFactory getSSLSocketFactory(Context context) {
        try(InputStream caInput =
                    context.getResources().openRawResource(R.raw.healthwatch_server_cert)) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate ca = cf.generateCertificate(caInput);

            String keyStoreType = KeyStore.getDefaultType();
            Log.d(TAG,"keyStoreType: " + keyStoreType);
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            X509TrustManager customTrustManager = new CustomTrustManager(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{customTrustManager}, null);
            return sslContext.getSocketFactory();
        } catch (IOException | CertificateException | KeyStoreException |
                NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }

    private HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession sslSession) {
                HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                Log.d(TAG, "hostname: " + hostname);
                return hv.verify(hostname, sslSession);
            }
        };
    }

    @Override
    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);

        mRequestQueue.add(req);
    }

    @Override
    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}
