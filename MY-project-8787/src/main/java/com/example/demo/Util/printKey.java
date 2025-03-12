package com.example.demo.Util;

import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;

public class printKey {
	
    public static void main(String[] args) throws NoSuchAlgorithmException {
        SecretKey secretKey = KeyUtil.generateAESKey();
        String base64Key = KeyUtil.encodeKeyToBase64(secretKey);
        System.out.println("Base64 Encoded Secret Key: " + base64Key);
    }

}
