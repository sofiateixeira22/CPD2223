package com.cpd.shared;

import java.util.Random;

public class Util {
    public static String sevenRandom() {
        final String ALPHA_NUMERIC_STRING = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final int length = 7;
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(ALPHA_NUMERIC_STRING.length());
            char randomChar = ALPHA_NUMERIC_STRING.charAt(index);
            sb.append(randomChar);
        }

        return sb.toString();
    }
}
