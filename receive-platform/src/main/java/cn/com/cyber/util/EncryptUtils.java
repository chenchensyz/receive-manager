//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.com.cyber.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class EncryptUtils {
    private static final char[] HEXDIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final String DEFAULT_CHARSET;
    public static final String ALGORITHM_BASE64 = "BASE64";
    public static final String ALGORITHM_DES = "DES";
    public static final String ALGORITHM_MD5 = "MD5";
    public static final String ALGORITHM_SHA1 = "SHA-1";
    public static final String ALGORITHM_SHA256 = "SHA-256";
    public static final String ALGORITHM_SHA384 = "SHA-384";
    public static final String ALGORITHM_SHA512 = "SHA-512";

    public EncryptUtils() {
    }

    public static String byte2hex(byte[] b) {
        StringBuffer hexStr = new StringBuffer();

        for(int i = 0; i < b.length; ++i) {
            int n = b[i];
            if (n < 0) {
                n += 256;
            }

            int d1 = n / 16;
            int d2 = n % 16;
            hexStr.append(HEXDIGITS[d1]).append(HEXDIGITS[d2]);
        }

        return hexStr.toString();
    }

    public static byte[] hex2byte(String str) {
        byte[] strBytes = str.getBytes();
        int strlen = strBytes.length;
        byte[] out = new byte[strlen / 2];

        for(int i = 0; i < strlen; i += 2) {
            out[i / 2] = (byte)Integer.parseInt(new String(strBytes, i, 2), 16);
        }

        return out;
    }

    public static byte[] MD5Encode(byte[] origin) {
        try {
            return messageDigestDncode(origin, "MD5");
        } catch (NoSuchAlgorithmException var2) {
            throw new RuntimeException(var2);
        }
    }

    public static String MD5Encode(String origin) {
        try {
            return messageDigestDncode(origin, "MD5");
        } catch (NoSuchAlgorithmException var2) {
            throw new RuntimeException(var2);
        }
    }

    public static String MD5Encode(String origin, String charset) throws UnsupportedEncodingException {
        try {
            return messageDigestDncode(origin, charset, "MD5");
        } catch (NoSuchAlgorithmException var3) {
            throw new RuntimeException(var3);
        }
    }

    public static byte[] SHA1Encode(byte[] origin) {
        try {
            return messageDigestDncode(origin, "SHA-1");
        } catch (NoSuchAlgorithmException var2) {
            throw new RuntimeException(var2);
        }
    }

    public static String SHA1Encode(String origin) {
        try {
            return messageDigestDncode(origin, "SHA-1");
        } catch (NoSuchAlgorithmException var2) {
            throw new RuntimeException(var2);
        }
    }

    public static String SHA1Encode(String origin, String charset) throws UnsupportedEncodingException {
        try {
            return messageDigestDncode(origin, charset, "SHA-1");
        } catch (NoSuchAlgorithmException var3) {
            throw new RuntimeException(var3);
        }
    }

    public static byte[] SHA256Encode(byte[] origin) {
        try {
            return messageDigestDncode(origin, "SHA-256");
        } catch (NoSuchAlgorithmException var2) {
            throw new RuntimeException(var2);
        }
    }

    public static String SHA256Encode(String origin) {
        try {
            return messageDigestDncode(origin, "SHA-256");
        } catch (NoSuchAlgorithmException var2) {
            throw new RuntimeException(var2);
        }
    }

    public static String SHA256Encode(String origin, String charset) throws UnsupportedEncodingException {
        try {
            return messageDigestDncode(origin, charset, "SHA-256");
        } catch (NoSuchAlgorithmException var3) {
            throw new RuntimeException(var3);
        }
    }

    public static byte[] SHA384Encode(byte[] origin) {
        try {
            return messageDigestDncode(origin, "SHA-384");
        } catch (NoSuchAlgorithmException var2) {
            throw new RuntimeException(var2);
        }
    }

    public static String SHA384Encode(String origin) {
        try {
            return messageDigestDncode(origin, "SHA-384");
        } catch (NoSuchAlgorithmException var2) {
            throw new RuntimeException(var2);
        }
    }

    public static String SHA384Encode(String origin, String charset) throws UnsupportedEncodingException {
        try {
            return messageDigestDncode(origin, charset, "SHA-384");
        } catch (NoSuchAlgorithmException var3) {
            throw new RuntimeException(var3);
        }
    }

    public static byte[] SHA512Encode(byte[] origin) {
        try {
            return messageDigestDncode(origin, "SHA-512");
        } catch (NoSuchAlgorithmException var2) {
            throw new RuntimeException(var2);
        }
    }

    public static String SHA512Encode(String origin) {
        try {
            return messageDigestDncode(origin, "SHA-512");
        } catch (NoSuchAlgorithmException var2) {
            throw new RuntimeException(var2);
        }
    }

    public static String SHA512Encode(String origin, String charset) throws UnsupportedEncodingException {
        try {
            return messageDigestDncode(origin, charset, "SHA-512");
        } catch (NoSuchAlgorithmException var3) {
            throw new RuntimeException(var3);
        }
    }

    public static byte[] messageDigestDncode(byte[] origin, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance(algorithm);
        sha1.reset();
        return sha1.digest(origin);
    }

    public static String messageDigestDncode(String origin, String charset, String algorithm) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return byte2hex(messageDigestDncode(origin.getBytes(charset), algorithm));
    }

    public static String messageDigestDncode(String origin, String algorithm) throws NoSuchAlgorithmException {
        String result = origin;

        try {
            result = byte2hex(messageDigestDncode(origin.getBytes(DEFAULT_CHARSET), algorithm));
        } catch (UnsupportedEncodingException var4) {
        }

        return result;
    }

    public static byte[] DESEncrypt(byte[] origin, byte[] key) throws Exception {
        Key keySpec = DESKey(key);
        return cipherEncrypt(origin, keySpec, "DES");
    }

    public static String DESEncrypt(String origin, String key) throws Exception {
        byte[] result = DESEncrypt(origin.getBytes(DEFAULT_CHARSET), key.getBytes(DEFAULT_CHARSET));
        return byte2hex(result);
    }

    public static String DESEncrypt(String origin, String key, String charset) throws UnsupportedEncodingException, Exception {
        byte[] result = DESEncrypt(origin.getBytes(charset), key.getBytes(charset));
        return byte2hex(result);
    }

    public static byte[] DESDecrypt(byte[] origin, byte[] key) throws Exception {
        Key keySpec = DESKey(key);
        return cipherDecrypt(origin, keySpec, "DES");
    }

    public static String DESDecrypt(String origin, String key) throws Exception {
        byte[] result = DESDecrypt(hex2byte(origin), key.getBytes(DEFAULT_CHARSET));
        return new String(result);
    }

    public static String DESDecrypt(String origin, String key, String charset) throws UnsupportedEncodingException, Exception {
        byte[] result = DESDecrypt(hex2byte(origin), key.getBytes(charset));
        return new String(result);
    }

    public static byte[] cipherEncrypt(byte[] origin, Key key, String algorithm) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(1, key);
        return cipher.doFinal(origin);
    }

    public static byte[] cipherDecrypt(byte[] origin, Key key, String algorithm) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(2, key);
        return cipher.doFinal(origin);
    }

    private static Key DESKey(byte[] key) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec keySpec = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        return keyFactory.generateSecret(keySpec);
    }

    static {
        DEFAULT_CHARSET = "UTF-8";
    }
}
