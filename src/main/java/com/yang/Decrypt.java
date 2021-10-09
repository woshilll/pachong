package com.yang;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

/**
 * desc
 *
 * @author stmj
 * @version 1.0.0
 * @date 2021/9/24 12:28
 */
public class Decrypt {
    private static Cipher cipher;
    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }
    public static byte[] decrypt(String key, String iv, byte[] bytes) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"), new IvParameterSpec(iv.getBytes()));
        return cipher.doFinal(bytes);
    }

}
