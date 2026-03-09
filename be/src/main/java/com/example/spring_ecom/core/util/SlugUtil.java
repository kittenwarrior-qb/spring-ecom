package com.example.spring_ecom.core.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class SlugUtil {
    
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern EDGESDHASHES = Pattern.compile("(^-|-$)");
    
    private SlugUtil() {
        throw new IllegalStateException("Utility class");
    }
    
    /**
     * Generate slug from text
     * Example: "Sách Hay Về Lập Trình" -> "sach-hay-ve-lap-trinh"
     */
    public static String toSlug(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }
        
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        slug = EDGESDHASHES.matcher(slug).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }
    
    /**
     * Generate unique slug with suffix if needed
     * Example: "sach-hay" -> "sach-hay-1", "sach-hay-2"
     */
    public static String toSlugWithSuffix(String input, int suffix) {
        String baseSlug = toSlug(input);
        return suffix > 0 ? baseSlug + "-" + suffix : baseSlug;
    }
}
