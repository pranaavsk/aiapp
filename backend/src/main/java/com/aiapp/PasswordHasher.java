package com.aiapp;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordHasher {

    private static final int COST = 12;

    public static  String hash(String plainPassword) {
        return BCrypt.withDefaults().hashToString(COST, plainPassword.toCharArray());
    }

    public static boolean verify(String plainPassword, String storedHash) {
        BCrypt.Result result = BCrypt.verifyer().verify(plainPassword.toCharArray(), storedHash);
        return result.verified;
    }
}
