package com.emprovise.util.security;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class UrlSigner {
    private String keyString;

    public UrlSigner(String keyString) {
        this.keyString = keyString;
    }

    public String getSignedUrl(String urlString) {
        SecretKeySpec secretKey = getSecretKeySpecFromPrivateKey();
        Mac mac = initializeMacWithSecretKeySpec(secretKey);
        String signedUrl = getEncodedSignedUrl(urlString, mac);
        return urlString + "&signature=" + signedUrl;
    }

    private SecretKeySpec getSecretKeySpecFromPrivateKey() {
        byte[] base64DecodedKey = Base64.decodeBase64(this.keyString);
        return new SecretKeySpec(base64DecodedKey, "HmacSHA1");
    }

    private Mac initializeMacWithSecretKeySpec(SecretKeySpec secretKey) {
        Mac mac;
        try {
            mac = Mac.getInstance("HmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Invalid encryption algorithm", e);
        }

        try {
            mac.init(secretKey);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Invalid key for signing", e);
        }
        return mac;
    }

    private String getEncodedSignedUrl(String urlString, Mac mac) {
        URL url = getUrlFromUrlString(urlString);
        byte[] hmacData = mac.doFinal(getByteArrayToSignFromUrl(url));
        return Base64.encodeBase64URLSafeString(hmacData);
    }

    private URL getUrlFromUrlString(String urlString) {
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid url", e);
        }
        return url;
    }

    private byte[] getByteArrayToSignFromUrl(URL url) {
        String path = url.getPath();
        String query = url.getQuery();
        return (path + "?" + query).getBytes(StandardCharsets.UTF_8);
    }
}
