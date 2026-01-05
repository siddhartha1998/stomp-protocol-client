package com.example.stomp.utils;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAUtils {

    private static final String RSA_TRANSFORMATION = "RSA/ECB/PKCS1Padding";

    public static PublicKey loadPublicKey(String publicKeyStr) throws Exception {
        byte[] data = Base64.getDecoder().decode(publicKeyStr.getBytes());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePublic(spec);
    }

    public static String encryptMessageWithPublicKey(String plainText, String publicKeyStr) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, loadPublicKey(publicKeyStr));
        return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes()));
    }
}
