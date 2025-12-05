package com.onboard.backend.security;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvManager {
    
    private static final Dotenv dotenv = Dotenv.load();

    public static String getClaveSecreta() {
        return dotenv.get("SECRET_KEY_AES");
    }

    public static String getAdminPassword() {
        return dotenv.get("APP_ADMIN_PASSWORD");
    }
    
}
