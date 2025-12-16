package br.com.impacta.bootcamp.commons.util;

import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class RSAUtil {

    private static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCgGFLCBhw5XtpO3SpufmbusnY7SPyyu5GjG875NYq8tJaMKUAzwyLKhQ9AK3Y7qbF2h+lHirRicd5zzQZaHEt7fRmvIp5oyAZk/+1EkS9enYtSgYO3EwD7iPRzNBwNtkr9ZFOJDpFZF5/CDgYxgg4RIV8HY2RPtPrgwhnCEYGNPQIDAQAB";
    private static String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKAYUsIGHDle2k7dKm5+Zu6ydjtI/LK7kaMbzvk1iry0lowpQDPDIsqFD0ArdjupsXaH6UeKtGJx3nPNBlocS3t9Ga8inmjIBmT/7USRL16di1KBg7cTAPuI9HM0HA22Sv1kU4kOkVkXn8IOBjGCDhEhXwdjZE+0+uDCGcIRgY09AgMBAAECgYAfL/3XPFd9OaQvntgoREmLEIsFy5cmvnGFo+IzHCIV1txJG7QdIVOCvl8VMiJLRyiCHcHtJPE1CFe8mWE2ZrKxYbjNQ3Ep1FOAZNWA1waCtrJ3cCXgpVGe0kfZjdvZm+pb/Ij7Piaa78pcrEEgJ6PmIkZjnjidMh/KT25ZNcBThQJBANpI17cc0Vwuy3LfWM8l/YucBQYk4gWqKPHGj2qGVDz8jxcePc+s1tw0DbsfuvizgT1Ig4kuUcQDUArRX4pYD+MCQQC7waSA+RgabPhy7dhB0a5I8/jU1bPsOnycYRXIO7keHKT8FGS/sA0+GgZIZa7OaSTpZCMmn45HVHnkhrZ2XjhfAkBBboZjGFtVk51esL6RUaT//WOwJMwvwIMHfmqtFtkXcul9l44Vu4yoc14OUoOcW0qJUTxEmcLey9Npert139HbAkEAhg0YbXjrS5q3vB6ygzO4sp917kdxwVx0sZXjXmW9opEi/lN1JyMEIW2lRLVMnFSXpQb4zc+nXqgiqxW/Wj2lEwJBANjwbtpSAYGGbY6hNvCDpbzn/X5ZXSyGKX8tJ0oTEPS223qKu35zJPeJeMdbgsTQ335qjaCAWB5Q9dGDFEnNG7E=";

    public static PublicKey getPublicKey(String base64PublicKey){
        PublicKey publicKey = null;
        try{
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    public static PrivateKey getPrivateKey(String base64PrivateKey){
        PrivateKey privateKey = null;
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64PrivateKey.getBytes()));
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    public static byte[] encrypt(String data, String publicKey)  {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
            return cipher.doFinal(data.getBytes());

        } catch ( Exception e) {
            return null;
        }
    }

    public  String decrypt(byte[] data, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(data));
    }

    public  String decrypt(String data, String base64PrivateKey)  {
        try {
            return decrypt(Base64.getDecoder().decode(data.getBytes()), getPrivateKey(base64PrivateKey));
        } catch (Exception e) {
            return "";
        }

    }

}
