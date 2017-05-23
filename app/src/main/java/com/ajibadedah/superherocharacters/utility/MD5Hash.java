package com.ajibadedah.superherocharacters.utility;

import android.content.Context;

import com.ajibadedah.superherocharacters.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by ajibade on 5/22/17.
 *
 */

public class MD5Hash {

    private static Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    private long timeStamp;
    private String hashSignature;
    private static String apiKey;

    private MD5Hash(Context context){
        apiKey = context.getResources().getString(R.string.public_apikey);
        String privateKey = context.getResources().getString(R.string.private_apikey);

        this.timeStamp = calendar.getTimeInMillis() / 1000L;
        this.hashSignature = MD5Hash.md5(String.valueOf(this.timeStamp) +
                privateKey +
                apiKey);
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getHashSignature() {
        return hashSignature;
    }

    public String getApiKey() {
        return apiKey;
    }

    /**
     * Returnes a new instance of a request signature.
     * @return String hash
     */
    public static MD5Hash create(Context context){
        return  new MD5Hash(context);
    }

    private static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
