package com.clinic.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    // Hash a plaintext password with salt
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // Compare plaintext password with hashed one
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
