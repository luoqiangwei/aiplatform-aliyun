package cn.oveay.aiplatform.utils.encryption;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Properties;

/**
 * oveashop
 * RSA非对称加密工具类
 *
 * @author OVAE
 * @version 1.3.2
 * CreateDate: 2019-08-27 13:39:42
 */
public class RSA {

    private RSA() {}

    private static String publicKeyFilePath;
    private static String privateKeyFilePath;
    private static int keyLong = 1024;

    static {
        // 加载配置文件
        Properties props = new Properties();
        InputStream inputStream = RSA.class.getClassLoader().getResourceAsStream("application.properties");
        if (inputStream != null) {
            try {
                props.load(inputStream);
            } catch (IOException e) {
                throw new RuntimeException("配置文件读取失败");
            }
            if (props.getProperty("rsa_public_key") != null) {
                publicKeyFilePath = props.getProperty("rsa_public_key");
            }
            if (props.getProperty("rsa_private_key") != null) {
                privateKeyFilePath = props.getProperty("rsa_private_key");
            }
            if (props.getProperty("rsa_key_long") != null) {
                keyLong = Integer.parseInt(props.getProperty("rsa_key_long"));
            }
        }
        if (publicKeyFilePath == null || privateKeyFilePath == null) {
            publicKeyFilePath = "rsa_public_key";
            privateKeyFilePath = "rsa_private_key";
            createRSAKey(keyLong);
        }
    }

    /**
     * 密钥产生器
     * @param keyLong 密钥位长
     */
    public static void createRSAKey(int keyLong) {
        //创建密钥对生成器，指定加密和解密算法为RSA
        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //指定密钥长度，初始化密钥对生成器
        kpg.initialize(keyLong);
        //生成密钥对
        KeyPair kp = kpg.genKeyPair();

        //获取公钥
        PublicKey pbKey = kp.getPublic();
        //获取私钥
        PrivateKey prKey = kp.getPrivate();

        try {
            //保存公钥到文件
            ObjectOutputStream publicKeyFileCreate = new ObjectOutputStream(new FileOutputStream(publicKeyFilePath));
            publicKeyFileCreate.writeObject(pbKey);
            publicKeyFileCreate.writeObject(null);
            publicKeyFileCreate.close();
            //保存私钥到文件
            ObjectOutputStream privateKeyFileCreate = new ObjectOutputStream(new FileOutputStream(privateKeyFilePath));
            privateKeyFileCreate.writeObject(prKey);
            privateKeyFileCreate.writeObject(null);
            privateKeyFileCreate.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 密钥产生器
     * @param keyLong 密钥位长
     * @param publicKeyFilePath 密钥公钥存储路径
     * @param privateKeyFilePath 密钥私钥存储路径
     */
    public static void createRSAKey(int keyLong, String publicKeyFilePath, String privateKeyFilePath) {
        RSA.publicKeyFilePath = publicKeyFilePath;
        RSA.privateKeyFilePath = privateKeyFilePath;
        createRSAKey(keyLong);
    }

    /**
     * 加密方法
     * @param text 明文
     * @return 转为16进制的密文
     */
    public static String enCoding(String text){
        // 首部加入0，防止数据变成负数，负数加解密时会出问题
        text = "0" + text;
        try {
            //从文件中读取公钥
            FileInputStream f = new FileInputStream(publicKeyFilePath);
            ObjectInputStream b = new ObjectInputStream(f);
            RSAPublicKey pbk = (RSAPublicKey) b.readObject();

            //RSA算法是使用整数进行加密的，再RSA公钥中包含有两个整数信息：e和n。对于明文数字m，计算密文的公式是m的e次方再与n求模。
            BigInteger e = pbk.getPublicExponent();
            BigInteger n = pbk.getModulus();

            //获取明文的大整数
            byte[] ptext = text.getBytes("UTF8");
            BigInteger m = new BigInteger(ptext);

            //加密明文
            BigInteger c = m.modPow(e, n);
            b.close();
            f.close();
            //返回c
            return c.toString(16);
        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密方法
     * @param text 密文
     * @return 转为String的明文
     */
    public static String deCoding(String text){
        BigInteger c = new BigInteger(text, 16);
        try{
            //获取私钥
            FileInputStream f = new FileInputStream(privateKeyFilePath);
            ObjectInputStream b = new ObjectInputStream(f);
            RSAPrivateKey prk = (RSAPrivateKey) b.readObject();

            //获取私钥的参数d，n
            BigInteger d = prk.getPrivateExponent();
            BigInteger n = prk.getModulus();

            //解密明文
            BigInteger m = c.modPow(d, n);

            //计算明文对应的字符串并输出
            byte[] mt = m.toByteArray();
            //System.out.println("PlainText is ");

            StringBuilder sb = new StringBuilder(new String(mt, "UTF-8"));
            b.close();
            f.close();
            // 去除添加在首部的0
            return sb.deleteCharAt(0).toString();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String getPublicKeyFilePath() {
        return publicKeyFilePath;
    }

    public static void setPublicKeyFilePath(String publicKeyFilePath) {
        RSA.publicKeyFilePath = publicKeyFilePath;
    }

    public static String getPrivateKeyFilePath() {
        return privateKeyFilePath;
    }

    public static void setPrivateKeyFilePath(String privateKeyFilePath) {
        RSA.privateKeyFilePath = privateKeyFilePath;
    }

    public static int getKeyLong() {
        return keyLong;
    }

    public static void setKeyLong(int keyLong) {
        RSA.keyLong = keyLong;
    }
}
