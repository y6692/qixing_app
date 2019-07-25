package com.sunshine.blelibrary.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * 作者: Sunshine
 * 时间: 2018/6/30.
 * 邮箱: 44493547@qq.com
 * 描述:
 */
public class EncryptUtils {
    // 加密
    public  static byte[] Encrypt(byte[] sSrc, byte[] sKey) {

        try
        {
            SecretKeySpec skeySpec = new SecretKeySpec(sKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");//"算法/模式/补码方式"
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(sSrc);

            return encrypted;//此处使用BASE64做转码功能，同时能起到2次加密的作用。
        } catch (Exception ex) {
            return null;
        }
    }

    // 解密
    public  static byte[] Decrypt(byte[] sSrc, byte[] sKey) {

        try {
            SecretKeySpec skeySpec = new SecretKeySpec(sKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] dncrypted = cipher.doFinal(sSrc);
            return dncrypted;

        } catch (Exception ex) {
            return null;
        }
    }
}
