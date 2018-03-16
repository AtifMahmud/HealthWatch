package com.cpen391.healthwatch.user;

import android.content.Context;
import android.util.Base64;

import com.cpen391.healthwatch.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import io.jsonwebtoken.Jwts;

/**
 * Created by william on 2018/1/2.
 *
 */
public class UserSession implements UserSessionInterface {

    private String mToken;
    private String mUsername;

    @Override
    public boolean isLoggedIn() {
        return mToken != null && !mToken.isEmpty();
    }

    @Override
    public String getUserToken() {
        return mToken;
    }

    @Override
    public void setUserToken(Context context, String token) {
        try {
            mUsername = Jwts.parser().setSigningKey(getPublicKey(context))
                    .parseClaimsJws(token).getBody().getSubject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mToken = token;
    }

    @Override
    public String getUsername() {
        return mUsername;
    }

    private PublicKey getPublicKey(Context context) throws Exception {
        InputStream inputStream = context.getResources().openRawResource(R.raw.jws_key);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        List<String> lines = new ArrayList<>();

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }

        // removes the first and last lines of the file (comments)
        if (lines.size() > 1 && lines.get(0).startsWith("-----") && lines.get(lines.size()-1).startsWith("-----")) {
            lines.remove(0);
            lines.remove(lines.size()-1);
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String keyLine: lines) {
            stringBuilder.append(keyLine);
        }
        String keyStr = stringBuilder.toString();
        // converts the String to a PublicKey instance
        byte[] keyBytes = Base64.decode(keyStr.getBytes("utf-8"), Base64.DEFAULT);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }
}
