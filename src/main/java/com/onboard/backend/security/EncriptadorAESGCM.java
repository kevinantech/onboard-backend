package com.onboard.backend.security;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.SecureRandom;
import java.util.Base64;

public class EncriptadorAESGCM {

    
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH_BIT = 128; 

    
    private static final String SECRET_KEY = EnvManager.getClaveSecreta();

    private static final SecureRandom secureRandom = new SecureRandom();

    public static String encriptar(String dato) throws Exception {
        byte[] iv = new byte[IV_LENGTH];
        secureRandom.nextBytes(iv); 

        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);

        byte[] encrypted = cipher.doFinal(dato.getBytes());


        byte[] encryptedWithIv = new byte[IV_LENGTH + encrypted.length];
        System.arraycopy(iv, 0, encryptedWithIv, 0, IV_LENGTH);
        System.arraycopy(encrypted, 0, encryptedWithIv, IV_LENGTH, encrypted.length);

        return Base64.getEncoder().encodeToString(encryptedWithIv);
    }

    public static String desencriptar(String encryptedData) throws Exception {
        byte[] encryptedIvAndText = Base64.getDecoder().decode(encryptedData);

        byte[] iv = new byte[IV_LENGTH];
        byte[] encryptedBytes = new byte[encryptedIvAndText.length - IV_LENGTH];

        System.arraycopy(encryptedIvAndText, 0, iv, 0, IV_LENGTH);
        System.arraycopy(encryptedIvAndText, IV_LENGTH, encryptedBytes, 0, encryptedBytes.length);

        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);

        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

        byte[] decrypted = cipher.doFinal(encryptedBytes);
        return new String(decrypted);
    }
}
