package com.bounteous.bug_bounty_backend.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHasher {
    private static final int KEY_LENGTH = 256;
    private static final int DEFAULT_ITERATIONS = 65536;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA1";

    public static String generateSalt(int length) {
        byte[] salt = new byte[length];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(String password, String salt, int iterations) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), Base64.getDecoder().decode(salt), iterations, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error while hashing a password: " + e.getMessage(), e);
        }
    }

    public static boolean verifyPassword(String password, String salt, int iterations, String expectedHash) {
        String hash = hashPassword(password, salt, iterations);
        return hash.equals(expectedHash);
    }
}
