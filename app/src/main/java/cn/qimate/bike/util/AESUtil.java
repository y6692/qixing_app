
package cn.qimate.bike.util;

import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * <p>Title: AES加解密</p>
 *
 * @author levi
 */
public class AESUtil {
    /**
     * 加密
     *
     * @param content 需要加密的内容
     * @param password  加密密码
     * @return
     */
    public static byte[] encrypt(byte[] content, String password) {
        if (password == null) {
            System.out.print("Key为空null");
            return null;
        }
        //判断Key是否为16位
        if (password.length() != 16) {
            Log.e("encrypt===", "Key长度不是16位");
            return null;
        }
        try {
            byte[] raw = password.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(content);
            return encrypted;
        } catch (Exception e) {
            System.out.println("数据加密时发生异常...");
            e.printStackTrace();
        }
        return null;
    }

    /**解密
     * @param content  待解密内容
     * @param password 解密密钥
     * @return
     */
    public static byte[] decrypt(byte[] content, String password) {
        //判断Key是否正确
        if (password == null) {
            System.out.print("Key为空null");
            return null;
        }
        //判断Key是否为16位
        if (password.length() != 16) {
            Log.e("decrypt===", "Key长度不是16位");
            return null;
        }
        try {
            byte[] raw = password.getBytes("ASCII");
            SecretKeySpec skp = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, skp);
            byte[] original = cipher.doFinal(content);
            return original;
        } catch (Exception e) {
            System.out.println("数据解密时发生异常...");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
     * @param src byte[] data
     * @return hex string
     */
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * Convert hex string to byte[]
     * @param hexString the hex string
     * @return byte[]
     */
//    public static byte[] hexStringToBytes(String hexString) {
//        if (hexString == null || hexString.equals("")) {
//            return null;
//        }
//        hexString = hexString.toUpperCase();
//        int length = hexString.length() / 2;
//        char[] hexChars = hexString.toCharArray();
//        byte[] d = new byte[length];
//        for (int i = 0; i < length; i++) {
//            int pos = i * 2;
//            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
//        }
//        return d;
//    }
//
//    public static byte[] charToByte(char c) {
//        byte[] b = new byte[2];
//        b[0] = (byte) ((c & 0xFF00) >> 8);
//        b[1] = (byte) (c & 0xFF);
//        return b;
//    }
}
