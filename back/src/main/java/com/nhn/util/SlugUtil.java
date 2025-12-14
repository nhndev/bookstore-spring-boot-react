package com.nhn.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SlugUtil {
    private static final Pattern NONLATIN     = Pattern.compile("[^\\w-]");

    private static final Pattern WHITESPACE   = Pattern.compile("[\\s]");

    private static final Pattern EDGESDHASHES = Pattern.compile("(^-|-$)");

    public String toSlug(String input) {
        input = input.trim().toLowerCase(Locale.ENGLISH);
        input = input.replaceAll("đ", "d");
        final String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        final String       normalized   = Normalizer.normalize(nowhitespace,
                                                         Normalizer.Form.NFD);
        String       slug         = NONLATIN.matcher(normalized).replaceAll("");
        slug = slug.replaceAll("\\-+", "-");
        slug = EDGESDHASHES.matcher(slug).replaceAll("");
        return slug;
    }
}
