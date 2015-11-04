package com.emprovise.util.core;

import java.util.UUID;

public class RandomUtil {

    public static String generateUniqueToken() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
