package com.hcmute.prse_be.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {

    private static final Pattern cacheKeyPattern = Pattern.compile("^[A-Za-z0-9@\\-_\\.\\:]{1,100}$");
    private static final Pattern urlPattern = Pattern.compile("(http|https)\\://[A-Za-z0-9\\.\\-]+(/[A-Za-z0-9\\?\\&\\=;\\+!\'\\(\\)\\*\\-\\._~%]*)*");
    private static final Pattern numberPattern = Pattern.compile("^[-]?\\d+([\\.,]\\d+)?$");
    private static final Pattern userNamePattern = Pattern.compile("^[_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*$");

    public ValidationUtils() {
    }

    public static boolean checkCacheKey(String key) {
        return key != null && key.length() > 0 && key.length() <= 250?cacheKeyPattern.matcher(key).find():false;
    }

    public static boolean isUrl(String data) {
        if(data != null && data.length() != 0) {
            Matcher match = urlPattern.matcher(data);
            return match.find();
        } else {
            return false;
        }
    }

    public static boolean isNumber(Object data) {
        return data == null?false:(data instanceof String?isNumber(((String)data).trim()):true);
    }

    public static boolean isNumber(String data) {
        return data != null && data.length() != 0?numberPattern.matcher(data).matches():false;
    }

    public static boolean isUserName(String data) {
        Matcher match = userNamePattern.matcher(data);
        return match.find();
    }

    public static boolean haveUnicode(String data) {
        return StringUtils.haveUnicode(data);
    }

    public static boolean haveTag(String data) {
        return StringUtils.haveTag(data);
    }

    public static boolean haveScriptTag(String data) {
        return StringUtils.haveScriptTag(data);
    }
}
