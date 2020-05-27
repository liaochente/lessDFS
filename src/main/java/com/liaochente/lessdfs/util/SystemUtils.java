package com.liaochente.lessdfs.util;

/**
 * 进制工具类
 */
public class SystemUtils {

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
        StringBuffer sb = new StringBuffer(bArr.length);
        String sTmp;

        for (int i = 0; i < bArr.length; i++) {
            sTmp = Integer.toHexString(0xFF & bArr[i]);
            if (sTmp.length() < 2)
                sb.append(0);
            sb.append(sTmp);
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        //init subDirectorys
        for (int i = 0; i < 256; i++) {
            StringBuffer sb = new StringBuffer();
            String hexStr = Integer.toHexString(i);
            if(hexStr.length() < 2) {
                sb.append(0);
            }
            sb.append(hexStr);

            System.out.print(sb.toString().toUpperCase() + " | ");
        }
    }
}
