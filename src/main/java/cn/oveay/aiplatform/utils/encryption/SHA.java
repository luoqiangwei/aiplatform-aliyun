package cn.oveay.aiplatform.utils.encryption;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * oveashop
 * sha数据特征提取算法
 *
 * @author OVAE
 * @version 1.0.1
 * CreateDate: 2019-08-27 13:02:00
 */
public class SHA {
    private static final String PREFIX_SALT = "Q　 +8/℉㎡③㈣Ⅻ〖";
    private static final String SUFFIX_SALT = "↔◕я▩⏱✈╊◙∰Жぎ";

    private SHA() {}

    /**
     * 传入文本内容，返回 SHA-256 串
     * @param strText 明文
     * @return 密文
     */
    public static String SHA256Encoding(final String strText) {
        return SHAEncoding(PREFIX_SALT + strText + SUFFIX_SALT, "SHA-256");
    }

    /**
     * 传入文本内容，返回 SHA-512 串
     * @param strText 明文
     * @return 密文
     */
    public static String SHA512Encoding(final String strText) {
        return SHAEncoding(PREFIX_SALT + strText + SUFFIX_SALT, "SHA-512");
    }

    /**
     * 字符串 SHA 加密
     * @param strText 明文
     * @param strType 加密类型
     * @return 密文
     */
    private static String SHAEncoding(final String strText, final String strType) {
        // 返回值
        String strResult = null;

        // 判断是否是有效字符串
        if (strText != null && strText.length() > 0) {
            // SHA 加密开始
            // 创建加密对象 并傳入加密类型
            MessageDigest messageDigest = null;
            try {
                messageDigest = MessageDigest.getInstance(strType);
            } catch (NoSuchAlgorithmException e) {
                return null;
            }
            // 传入要加密的字符串
            messageDigest.update(strText.getBytes());
            // 得到 byte 类型结果
            byte[] byteBuffer = messageDigest.digest();

            // 将 byte 转化为 string
            StringBuilder strHexString = new StringBuilder();
            // 遍历 byte buffer
            for (byte b : byteBuffer) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    strHexString.append('0');
                strHexString.append(hex);
            }
            // 得到返回结果
            strResult = strHexString.toString();
        }

        return strResult;
    }
}
