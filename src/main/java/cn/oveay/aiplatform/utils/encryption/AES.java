package cn.oveay.aiplatform.utils.encryption;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;

/**
 * oveashop
 * AES对称加密算法
 *
 * @author OVAE
 * @version 1.0.0
 * CreateDate: 2019-08-27 13:11:19
 */
public class AES {

    private static Cipher cipher;

    static {
        Security.addProvider(new BouncyCastleProvider());
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    private AES() {}

    /**
     * AES加密
     * @param key 密钥
     * @param iv 偏移密钥
     * @param content 明文
     * @return 密文
     */
    public static String ASEEncoding(byte[] key, byte[] iv, String content) {
        byte[] keyBytes = key;
        int base = 16;
        if (keyBytes.length % base != 0) {
            int groups = keyBytes.length / base + 1;
            byte[] temp = new byte[groups * base];
            Arrays.fill(temp, (byte) 0);
            System.arraycopy(keyBytes, 0, temp, 0, keyBytes.length);
            keyBytes = temp;
        }

        byte[] ivBytes = iv;
        if (ivBytes.length % base != 0) {
            int groups = ivBytes.length / base + 1;
            byte[] temp = new byte[groups * base];
            Arrays.fill(temp, (byte) 0);
            System.arraycopy(ivBytes, 0, temp, 0, ivBytes.length);
            ivBytes = temp;
        }

        try {
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes, "AES"), new IvParameterSpec(ivBytes));
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            return null;
        }
        byte[] data = content.getBytes();
        try {
            return new String(Base64.getEncoder().encode(cipher.doFinal(data)));
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            return null;
        }
    }

    public static String ASEDncoding(byte[] key, byte[] iv, String content) {
        byte[] keyBytes = key;
        int base = 16;
        if (keyBytes.length % base != 0) {
            int groups = keyBytes.length / base + 1;
            byte[] temp = new byte[groups * base];
            Arrays.fill(temp, (byte) 0);
            System.arraycopy(keyBytes, 0, temp, 0, keyBytes.length);
            keyBytes = temp;
        }

        byte[] ivBytes = iv;
        if (ivBytes.length % base != 0) {
            int groups = ivBytes.length / base + 1;
            byte[] temp = new byte[groups * base];
            Arrays.fill(temp, (byte) 0);
            System.arraycopy(ivBytes, 0, temp, 0, ivBytes.length);
            ivBytes = temp;
        }
        try {
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, "AES"), new IvParameterSpec(ivBytes));
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            return null;
        }
        byte[] encrypted1 = Base64.getDecoder().decode(content);
        try {
            return new String(cipher.doFinal(encrypted1), "UTF-8");
        } catch (UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException e) {
            return null;
        }
    }
}
