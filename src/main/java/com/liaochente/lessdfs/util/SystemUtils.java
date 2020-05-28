package com.liaochente.lessdfs.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 进制工具类
 */
public class SystemUtils {

    private final static Logger LOG = LoggerFactory.getLogger(SystemUtils.class);

    public final static byte[] md5Bytes(byte[] bytes) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(bytes);
            return messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            LOG.error("初始化MD5失败", e);
        }
        return null;
    }

    public final static String md5String(byte[] bytes) {
        bytes = md5Bytes(bytes);
        return bytesToHexString(bytes);
    }

    /**
     * 字节数组转16进制字符串
     *
     * @param bArr
     * @return
     */
    public final static String bytesToHexString(byte[] bArr) {
        if (bArr == null) {
            return null;
        }
        StringBuffer hexBuffer = new StringBuffer(bArr.length);
        String hexStr;

        for (int i = 0; i < bArr.length; i++) {
            hexStr = Integer.toHexString(0xFF & bArr[i]);
            if (hexStr.length() < 2) {
                hexBuffer.append(0);
            }
            hexBuffer.append(hexStr);
        }

        return hexBuffer.toString();
    }

    public static void main(String[] args) {
        //init subDirectorys
        for (int i = 0; i < 256; i++) {
            StringBuffer sb = new StringBuffer();
            String hexStr = Integer.toHexString(i);
            if (hexStr.length() < 2) {
                sb.append(0);
            }
            sb.append(hexStr);

            System.out.print(sb.toString().toUpperCase() + " | ");
        }
    }
}
