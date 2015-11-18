package com.emprovise.util.security;

import org.apache.commons.codec.binary.Hex;


public class EncoderUtil {
    static final String URL_ENCODING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";

    public static String toString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        if (bytes != null) {
            for (int index = 0; index < bytes.length; index++) {
                stringBuilder.append(bytes[index] & 0xFF);
                if (index < (bytes.length - 1)) {
                    stringBuilder.append('.');
                }
            }
        }
        return stringBuilder.toString();
    }

    public static String urlEncode(byte[] bytes) {
        int outputSize = (bytes.length * 4 + 2) / 3;
        char[] output = new char[outputSize];

        int bits = 0;
        int partial = 0;
        int src = 0;
        int dst = 0;
        while (src < bytes.length) {
            partial |= bytes[src++] & 0xff;
            bits += 8;
            do {
                bits -= 6;
                output[dst++] = URL_ENCODING.charAt((partial >>> bits) & 0x3f);
            } while (bits >= 6);
            partial <<= 8;
        }

        if (bits > 0) {
            partial >>>= 2;
            output[dst++] = URL_ENCODING.charAt((partial >>> bits) & 0x3f);
        }

        return new String(output);
    }

    public static String toHexDigitString(byte[] bytes) {
        String result;
        if(bytes == null) {
            result = "";
        }
        else {
            result = new String(Hex.encodeHex(bytes)).toUpperCase();
        }
        return result;
    }

}
