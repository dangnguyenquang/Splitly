package com.example.splitly.util;

public final class PiiMasker {

    private PiiMasker() {} // prevent instantiation

    /** Mask email: a***@domain.com ; null-safe */
    public static String maskEmail(String email) {
        if (email == null || email.isBlank() || !email.contains("@")) return "***";
        int at = email.indexOf('@');
        if (at <= 1) return "***" + email.substring(at);
        return email.charAt(0) + "***" + email.substring(at);
    }

    /** Mask phone: keep last 3 digits */
    public static String maskPhone(String phone) {
        if (phone == null || phone.isBlank()) return "***";
        String digits = phone.replaceAll("\\D+", "");
        if (digits.length() <= 3) return "***";
        return "***" + digits.substring(digits.length() - 3);
    }
}

