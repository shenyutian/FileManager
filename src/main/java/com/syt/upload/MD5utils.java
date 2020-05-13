package com.syt.upload;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import sun.misc.BASE64Encoder;

import java.security.MessageDigest;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MD5utils {

    public final static String md5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String hmac_sha1(String datas) {
        // 秘钥
        String key = "352189DE1238998FAB11C14255488FAFE556556BEAC311BFAA765BC";
        String reString = "";

        try {
            byte[] data = key.getBytes("UTF-8");
            // 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
            SecretKey secretKey = new SecretKeySpec(data, "HmacSHA1");
            // 生成一个指定 Mac 算法 的 Mac 对象
            Mac mac = Mac.getInstance("HmacSHA1");
            // 用给定密钥初始化 Mac 对象
            mac.init(secretKey);

            byte[] text = datas.getBytes("UTF-8");
            // 完成 Mac 操作
            byte[] text1 = mac.doFinal(text);
            return new BASE64Encoder().encode(text1);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("异常 = " + e.getMessage());
        }

        return reString;
    }

    public static void main(String[] args) {
        String s = "{\n" +
                "\t\"userName\": \"syt\",\n" +
                "\t\"userPassword\": \"123456\"\n" +
                "}";
        System.out.println(md5(hmac_sha1(s)));
        System.out.println(64 >>2 & 0xf);
    }
}
