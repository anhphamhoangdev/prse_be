package com.hcmute.prse_be.util;

import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class StringUtils {

    public StringUtils() {
    }

    public static String removeNonPrintableCharactor(String data) {
        return data.replaceAll("\\p{C}", "");
    }

    public static String urlEncode(String url) {
        if (isEmpty(url)) {
            return "";
        } else {
            String result = "";

            try {
                result = URLEncoder.encode(url, "UTF-8");
            } catch (UnsupportedEncodingException var3) {
                ;
            }

            return result;
        }
    }

    public static String urlDecode(String url) {
        if (isEmpty(url)) {
            return "";
        } else {
            String result = "";

            try {
                result = URLDecoder.decode(url, "UTF-8");
            } catch (UnsupportedEncodingException var3) {
                ;
            }

            return result;
        }
    }

    private static char getRandomChar() {
        Random rd = new Random();
        Integer number = Integer.valueOf(rd.nextInt("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".length()));
        return "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".charAt(number.intValue());
    }

    public static String getRandomString(Integer length) {
        String result = "";

        for (int i = 0; i < length.intValue(); ++i) {
            result = result + getRandomChar();
        }

        return result;
    }

    public static String getGenerateUniqueString() {
        UUID randomUUID = UUID.randomUUID();
        return randomUUID.toString().replaceAll("_", "");
    }

    public static String removeScriptTag(String data) {
        return data != null && data.length() > 0 ? data.replaceAll("<\\s*script[^>]*>.*?</\\s*script[^>]*>", "") : "";
    }

    public static boolean haveScriptTag(String data) {
        return data == null ? false : data.length() != removeScriptTag(data).length();
    }

    public static String removeAllTag(String data) {
        return data != null && data.length() > 0 ? data.replaceAll("<(.|\n)*?>", "") : "";
    }

    public static boolean haveTag(String data) {
        return data == null ? false : data.length() != removeAllTag(data).length();
    }

    public static String removeUnicode(String data) {
        return data != null && data.length() > 0 ? data.replaceAll("[^\\p{ASCII}]", "") : "";
    }

    public static String killUnicode(String data) {
        if (data != null && data.length() > 0) {
            data = data.replaceAll("Đ", "D");
            data = data.replaceAll("đ", "d");
            return Normalizer.normalize(data, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        } else {
            return "";
        }
    }

    public static boolean haveUnicode(String data) {
        return data == null ? false : data.length() != removeUnicode(data).length();
    }

    public static String normalize(String data) {
        return data != null && data.length() > 0 ? Normalizer.normalize(data, Normalizer.Form.NFKC) : "";
    }

    public static String slug(String data) {
        data = killUnicode(data).trim();
        if (data.length() > 0) {
            data = data.replaceAll("[^a-zA-Z0-9- ]*", "");
            data = data.replaceAll("[ ]{1,}", "-");
        }

        return data;
    }

    public static String slug(String data, int length) {
        String slugString = slug(data);
        return slug(data).substring(0, slugString.length() > length ? length : slugString.length());
    }

    public static String digitFormat(long value) {
        DecimalFormat formatter = new DecimalFormat("#,##0");
        return formatter.format(value);
    }

    public static String doMD5(String source) {
        MessageDigest digest = null;

        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException var3) {
            throw new RuntimeException(var3);
        }

        digest.update(source.getBytes());
        byte[] hash = digest.digest();
        return byteArrayToHexString(hash);
    }

    public static String byteToHexString(byte aByte) {
        String hex = Integer.toHexString(255 & aByte);
        return (hex.length() == 1 ? "0" : "") + hex;
    }

    public static String byteArrayToHexString(byte[] hash) {
        StringBuilder hexString = new StringBuilder();

        for (int i = 0; i < hash.length; ++i) {
            hexString.append(byteToHexString(hash[i]));
        }

        return hexString.toString();
    }

    public static String stripStart(String str, String stripChars) {
        int strLen;
        if (str != null && (strLen = str.length()) != 0) {
            int start = 0;
            if (stripChars == null) {
                while (start != strLen && Character.isWhitespace(str.charAt(start))) {
                    ++start;
                }
            }

            if (stripChars.length() == 0) {
                return str;
            } else {
                while (start != strLen && stripChars.indexOf(str.charAt(start)) != -1) {
                    ++start;
                }

                return str.substring(start);
            }
        } else {
            return str;
        }
    }

    public static String stripEnd(String str, String stripChars) {
        int end;
        if (str != null && (end = str.length()) != 0) {
            if (stripChars == null) {
                while (end != 0 && Character.isWhitespace(str.charAt(end - 1))) {
                    --end;
                }
            }

            if (stripChars.length() == 0) {
                return str;
            } else {
                while (end != 0 && stripChars.indexOf(str.charAt(end - 1)) != -1) {
                    --end;
                }

                return str.substring(0, end);
            }
        } else {
            return str;
        }
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static String strip(String str, String stripChars) {
        if (isEmpty(str)) {
            return str;
        } else {
            str = stripStart(str, stripChars);
            return stripEnd(str, stripChars);
        }
    }

    public static String join(Object[] array, String separator) {
        return array == null ? null : join(array, separator, 0, array.length);
    }

    public static String join(Object[] array, String separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        } else {
            if (separator == null) {
                separator = "";
            }

            int bufSize = endIndex - startIndex;
            if (bufSize <= 0) {
                return "";
            } else {
                if (endIndex > array.length) {
                    endIndex = array.length;
                }

                StringBuilder buf = new StringBuilder();

                for (int i = startIndex; i < endIndex; ++i) {
                    if (i > startIndex) {
                        buf.append(separator);
                    }

                    if (array[i] != null) {
                        if (array[i].toString().contains(separator)) {
                            buf.append("\"");
                            buf.append(array[i]);
                            buf.append("\"");
                        } else {
                            buf.append(array[i]);
                        }
                    }
                }

                return buf.toString();
            }
        }
    }
    public static List<String> getListServiceOptions(String serviceOptions) {
        List<String> servicesOption = new ArrayList<>();
        if (serviceOptions != null && !serviceOptions.isEmpty()) {
            Type type = new TypeToken<List<String>>() {
            }.getType();
            try {
                List<String> rs = JsonUtils.DeSerialize(serviceOptions, type);
                if (rs != null) {
                    servicesOption = rs;
                }
            } catch (Exception e) {

            }
        }
        return servicesOption;
    }
}
