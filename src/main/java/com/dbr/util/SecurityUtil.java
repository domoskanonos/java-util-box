package com.dbr.util;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SecurityUtil {

    private static Logger logger = Logger.getLogger(SecurityUtil.class.getSimpleName());

    private static SecretKeySpec secretKey;

    private static String transformation = "AES/ECB/PKCS5Padding";

    private SecurityUtil() {
    }

    public static void setKey(String myKey) {
        MessageDigest sha = null;
        byte[] key = myKey.getBytes(StandardCharsets.UTF_8);
        try {
            sha = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException(e);
        }
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16);
        secretKey = new SecretKeySpec(key, "AES");
    }

    public static String encrypt(String decrypted) {
        logger.log(Level.INFO, "encrypt data: {0}", decrypted);
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(transformation);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new SecurityException(e);
        }
        try {
            Objects.requireNonNull(cipher).init(Cipher.ENCRYPT_MODE, secretKey);
        } catch (InvalidKeyException e) {
            throw new SecurityException(e);
        }
        try {
            return Base64.getEncoder().encodeToString(cipher.doFinal(decrypted.getBytes(StandardCharsets.UTF_8)));
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new SecurityException(e);
        }
    }

    public static String decrypt(String encrypted) {
        logger.log(Level.INFO, "decrypt data: {0}", encrypted);
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(transformation);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new SecurityUtilException(e);
        }
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
        } catch (InvalidKeyException e) {
            throw new SecurityUtilException(e);
        }
        try {
            return new String(cipher.doFinal(Base64.getDecoder().decode(encrypted)));
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new SecurityUtilException(e);
        }
    }


    public static class SecurityUtilException extends RuntimeException {
        public SecurityUtilException(Throwable th) {
            super(th);
        }
    }

}
