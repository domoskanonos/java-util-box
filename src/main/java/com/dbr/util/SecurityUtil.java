package com.dbr.util;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SecurityUtil {

    private static final Logger LOGGER = Logger.getLogger(SecurityUtil.class.getSimpleName());
    private static final String DEFAULT_SECRET = "FGKFFIFFFFSSDWDD";
    private static final String ALGORITHMNAME = "AES/GCM/NoPadding";
    private static final int ALGORITHM_NONCE_SIZE = 12;
    private static final int ALGORITHM_TAG_SIZE = 128;
    private static final int ALGORITHM_KEY_SIZE = 128;
    private static final String PBKDF2_NAME = "PBKDF2WithHmacSHA256";
    private static final int PBKDF2_SALT_SIZE = 16;
    private static final int PBKDF2_ITERATIONS = 32767;

    private static SecurityUtil uniqueInstance = null;

    public static synchronized SecurityUtil getUniqueInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new SecurityUtil();
        }
        return uniqueInstance;
    }

    private SecurityUtil() {
    }


    private String secret = DEFAULT_SECRET;

    public void setSystemSecret(String secret) {
        this.secret = secret;
    }

    public static void main(String[] args) {
        String encrypt = SecurityUtil.getUniqueInstance().encrypt("secret");
        LOGGER.log(Level.INFO, encrypt);
        String decrypt = SecurityUtil.getUniqueInstance().decrypt(encrypt);
        LOGGER.log(Level.INFO, decrypt);
    }

    public String decrypt(String s) {
        if (DEFAULT_SECRET.equals(this.secret)) {
            LOGGER.warning("secret is the default secret, please set a individual secret to avoid security risk!");
        }
        return decrypt(s, this.secret);
    }

    public String encrypt(String hallo) {
        if (DEFAULT_SECRET.equals(this.secret)) {
            LOGGER.warning("secret is the default secret, please set a individual secret to avoid security risk!");
        }
        return encrypt(hallo, this.secret);
    }


    private String encrypt(String plaintext, String password) {
        // Generate a 128-bit salt using a CSPRNG.
        SecureRandom rand = new SecureRandom();
        byte[] salt = new byte[PBKDF2_SALT_SIZE];
        rand.nextBytes(salt);

        // Create an instance of PBKDF2 and derive a key.
        PBEKeySpec pwSpec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, ALGORITHM_KEY_SIZE);
        SecretKeyFactory keyFactory = null;
        try {
            keyFactory = SecretKeyFactory.getInstance(PBKDF2_NAME);
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityUtilException(e);
        }
        byte[] key = new byte[0];
        try {
            key = keyFactory.generateSecret(pwSpec).getEncoded();
        } catch (InvalidKeySpecException e) {
            throw new SecurityUtilException(e);
        }

        // Encrypt and prepend salt.
        byte[] ciphertextAndNonce = encryptBytes(plaintext.getBytes(StandardCharsets.UTF_8), key);
        byte[] ciphertextAndNonceAndSalt = new byte[salt.length + ciphertextAndNonce.length];
        System.arraycopy(salt, 0, ciphertextAndNonceAndSalt, 0, salt.length);
        System.arraycopy(ciphertextAndNonce, 0, ciphertextAndNonceAndSalt, salt.length, ciphertextAndNonce.length);

        // Return as base64 string.
        return Base64.getEncoder().encodeToString(ciphertextAndNonceAndSalt);
    }

    private String decrypt(String base64CiphertextAndNonceAndSalt, String password) {
        // Decode the base64.
        byte[] ciphertextAndNonceAndSalt = Base64.getDecoder().decode(base64CiphertextAndNonceAndSalt);

        // Retrieve the salt and ciphertextAndNonce.
        byte[] salt = new byte[PBKDF2_SALT_SIZE];
        byte[] ciphertextAndNonce = new byte[ciphertextAndNonceAndSalt.length - PBKDF2_SALT_SIZE];
        System.arraycopy(ciphertextAndNonceAndSalt, 0, salt, 0, salt.length);
        System.arraycopy(ciphertextAndNonceAndSalt, salt.length, ciphertextAndNonce, 0, ciphertextAndNonce.length);

        // Create an instance of PBKDF2 and derive the key.
        PBEKeySpec pwSpec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, ALGORITHM_KEY_SIZE);
        SecretKeyFactory keyFactory = null;
        try {
            keyFactory = SecretKeyFactory.getInstance(PBKDF2_NAME);
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityUtilException(e);
        }
        byte[] key = new byte[0];
        try {
            key = keyFactory.generateSecret(pwSpec).getEncoded();
        } catch (InvalidKeySpecException e) {
            throw new SecurityUtilException(e);
        }

        // Decrypt and return result.
        return new String(decryptBytes(ciphertextAndNonce, key), StandardCharsets.UTF_8);
    }

    private byte[] encryptBytes(byte[] plaintext, byte[] key) {
        // Generate a 96-bit nonce using a CSPRNG.
        SecureRandom rand = new SecureRandom();
        byte[] nonce = new byte[ALGORITHM_NONCE_SIZE];
        rand.nextBytes(nonce);

        // Create the cipher instance and initialize.
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(ALGORITHMNAME);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new SecurityUtilException(e);
        }
        try {
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(ALGORITHM_TAG_SIZE, nonce));
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new SecurityUtilException(e);
        }

        // Encrypt and prepend nonce.
        byte[] ciphertext = new byte[0];
        try {
            ciphertext = cipher.doFinal(plaintext);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new SecurityUtilException(e);
        }
        byte[] ciphertextAndNonce = new byte[nonce.length + ciphertext.length];
        System.arraycopy(nonce, 0, ciphertextAndNonce, 0, nonce.length);
        System.arraycopy(ciphertext, 0, ciphertextAndNonce, nonce.length, ciphertext.length);

        return ciphertextAndNonce;
    }

    private byte[] decryptBytes(byte[] ciphertextAndNonce, byte[] key) {
        // Retrieve the nonce and ciphertext.
        byte[] nonce = new byte[ALGORITHM_NONCE_SIZE];
        byte[] ciphertext = new byte[ciphertextAndNonce.length - ALGORITHM_NONCE_SIZE];
        System.arraycopy(ciphertextAndNonce, 0, nonce, 0, nonce.length);
        System.arraycopy(ciphertextAndNonce, nonce.length, ciphertext, 0, ciphertext.length);

        // Create the cipher instance and initialize.
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(ALGORITHMNAME);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new SecurityUtilException(e);
        }
        try {
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(ALGORITHM_TAG_SIZE, nonce));
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new SecurityUtilException(e);
        }

        // Decrypt and return result.
        try {
            return cipher.doFinal(ciphertext);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new SecurityUtilException(e);
        }
    }

    public static class SecurityUtilException extends RuntimeException {

        public SecurityUtilException(String message) {
            super(message);
        }

        public SecurityUtilException(Throwable th) {
            super(th);
        }

    }

}
