package com.umbrella.training.mvvm.view;

import android.util.Base64;
import android.util.Log;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptUtil {
    /** LOG TAG */
    private static final String TAG = "SwanAppEncryptUtils";
    private static final boolean DEBUG = true;
    /** File buffer stream size */
    public static final int FILE_STREAM_BUFFER_SIZE = 8192;
    /** 字符编码 */
    private static final String CHARSET = "utf-8";

    /** 动态生成的AES key 的长度 */
    private static final int AES_KEY_LENGTH = 256;

    // ———————— Android平台上 MessageDigest 支持的一些加密算法  ———————————————————

    /** 加密算法 - 非对称加密 - RSA/ECB/PKCS1Padding */
    public static final String ALGORITHM_RSA_ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding";
    /** 加密算法 - 对称加密 - AES/CBC/PKCS7Padding */
    public static final String AES_CBC_PKCS7_PADDING = "AES/CBC/PKCS7Padding";
    /** 加密算法 - 非对称加密 - RSA */
    public static final String ENCRYPT_RSA = "RSA";
    /** 加密算法 - 对称加密 - AES */
    public static final String ENCRYPT_AES = "AES";

    // ———————— Android平台上 MessageDigest 支持的一些散列算法,sha-224有版本限制,未列出 ——————

    /** 散列算法 - MD5 */
    public static final String HASH_MD5 = "MD5";
    /** 散列算法 - SHA-1 */
    public static final String HASH_SHA1 = "SHA-1";
    /** 散列算法 - SHA-256 */
    public static final String HASH_SHA256 = "SHA-256";
    /** 散列算法 - SHA-384 */
    public static final String HASH_SHA384 = "SHA-384";
    /** 散列算法 - SHA-512 */
    public static final String HASH_SHA512 = "SHA-512";

    /**
     * 散列算法的名称
     */
    @StringDef({HASH_MD5, HASH_SHA1, HASH_SHA256, HASH_SHA384, HASH_SHA512})
    @Retention(RetentionPolicy.SOURCE)
    public @interface HashAlgorithm {
    }

    /**
     * 把二进制byte数组生成 type对应算法的 32位 十六进制字符串，单个字节小于0xf，高位补0。
     *
     * @param hashAlgorithm 具体的散列算法
     * @param bytes         输入
     * @param upperCase     true：大写， false 小写字符串
     * @return 把二进制byte数组生成 type算法的 32位 十六进制字符串，单个字节小于0xf，高位补0。
     */
    public static String hash(@HashAlgorithm String hashAlgorithm, byte[] bytes, boolean upperCase)
            throws NoSuchAlgorithmException {
        MessageDigest algorithm = MessageDigest.getInstance(hashAlgorithm);
        algorithm.reset();
        algorithm.update(bytes);
        return toHexString(algorithm.digest(), "", upperCase);
    }

    /**
     * 把二进制byte数组生成十六进制字符串，单个字节小于0xf，高位补0。
     *
     * @param bytes     输入
     * @param separator 分割线
     * @param upperCase true：大写， false 小写字符串
     * @return 把二进制byte数组生成十六进制字符串，单个字节小于0xf，高位补0。
     */
    private static String toHexString(byte[] bytes, String separator, boolean upperCase) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String str = Integer.toHexString(0xFF & b);
            if (upperCase) {
                str = str.toUpperCase();
            }
            if (str.length() == 1) {
                hexString.append("0");
            }
            hexString.append(str).append(separator);
        }
        return hexString.toString();
    }

    /**
     * RSA 加密
     *
     * @param publicKey rsa 加密公钥文本, 不包含 -----BEGIN PUBLIC KEY-----
     * @param content   待加密内容
     * @return 加密结果
     */
    @CheckResult
    @NonNull
    public static String rsaEncrypt(@NonNull String publicKey, @NonNull String content) {
        try {
            return rsaEncrypt(publicKey, content.getBytes(CHARSET));
        } catch (Exception e) {
            if (DEBUG) {
                Log.e(TAG, "#rsaEncrypt(String, String) error", e);
            }
        }
        return "";
    }

    /**
     * RSA 加密
     *
     * @param publicKey rsa 加密公钥文本, 不包含 -----BEGIN PUBLIC KEY-----
     * @param content   待加密内容
     * @return 加密结果
     */
    @CheckResult
    @NonNull
    public static String rsaEncrypt(@NonNull String publicKey, @NonNull byte[] content) {
        try {
            byte[] encrypted = rsaEncrypt(publicKey.getBytes(CHARSET), content);
            if (encrypted != null) {
                return Base64.encodeToString(encrypted, Base64.NO_WRAP);
            }
        } catch (Exception e) {
            if (DEBUG) {
                Log.e(TAG, "#rsaEncrypt(String, byte[]) error", e);
            }
        }
        return "";
    }

    /**
     * RSA 加密
     *
     * @param publicKey rsa 加密公钥文本, 不包含 -----BEGIN PUBLIC KEY-----
     * @param content   待加密内容
     * @return 加密结果，可能为空
     */
    @CheckResult
    @Nullable
    public static byte[] rsaEncrypt(@NonNull byte[] publicKey, @NonNull byte[] content) {
        try {
            byte[] buffer = Base64.decode(publicKey, Base64.DEFAULT);
            KeyFactory keyFactory = KeyFactory.getInstance(ENCRYPT_RSA);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            PublicKey generatePublic = keyFactory.generatePublic(keySpec);
            Cipher cipher = Cipher.getInstance(ALGORITHM_RSA_ECB_PKCS1_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, generatePublic);
            return cipher.doFinal(content);
        } catch (Exception e) {
            if (DEBUG) {
                Log.e(TAG, "#rsaEncrypt(byte[], byte[]) error", e);
            }
        }
        return null;
    }

    /**
     * AES 加密
     *
     * @param key       加密key
     * @param content   待加密内容
     * @param algorithm 加密模式, 如 AES/CTR/NoPadding
     * @param ivText    偏移量
     * @return 加密结果
     */
    @CheckResult
    @NonNull
    public static String aesEncrypt(@NonNull String key, @NonNull String content,
                                    @NonNull String algorithm, @NonNull String ivText) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(CHARSET), ENCRYPT_AES);
            IvParameterSpec iv = new IvParameterSpec(ivText.getBytes(CHARSET));
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
            byte[] encrypted = cipher.doFinal(content.getBytes(CHARSET));

            return Base64.encodeToString(encrypted, Base64.NO_WRAP);
        } catch (Exception e) {
            if (DEBUG) {
                Log.e(TAG, "aesEncrypt", e);
            }
        }
        return "";
    }


    public static String aesDecrypt(String key, String text, String algorithm, String ivText) {
        byte[] data = Base64.decode(text, Base64.NO_WRAP);
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(CHARSET), ENCRYPT_AES);
            IvParameterSpec iv = new IvParameterSpec(ivText.getBytes(CHARSET));
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
            byte[] original = cipher.doFinal(data);
            return new String(original, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * AES 加密
     *
     * @param key       加密key
     * @param content   待加密内容
     * @param algorithm 加密模式, 如 AES/CTR/NoPadding
     * @param ivText    偏移量
     * @return 加密结果
     */
    @Nullable
    public static byte[] aesEncrypt(@NonNull byte[] key, @NonNull byte[] content,
                                    @NonNull String algorithm, @NonNull String ivText) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            SecretKeySpec keySpec = new SecretKeySpec(key, ENCRYPT_AES);
            IvParameterSpec iv = new IvParameterSpec(ivText.getBytes(CHARSET));
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
            return cipher.doFinal(content);
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 获取动态生成的AES key
     *
     * @return 动态生成的 AES
     */
    @NonNull
    public static byte[] genRandomAesKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ENCRYPT_AES);
            keyGenerator.init(AES_KEY_LENGTH);
            SecretKey secretKey = keyGenerator.generateKey();
            return secretKey.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            return UUID.randomUUID().toString().getBytes();
        }
    }
}
