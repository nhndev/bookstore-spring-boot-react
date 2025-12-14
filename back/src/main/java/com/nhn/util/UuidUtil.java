package com.nhn.util;

import java.util.UUID;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UuidUtil {
    public static boolean equals(final UUID a, final UUID b) {
        if (ObjectUtils.anyNull(a, b)) {
            return false;
        }
        return StringUtils.equals(a.toString(), b.toString());
    }

    public static boolean notEquals(final UUID a, final UUID b) {
        return !equals(a, b);
    }
}
