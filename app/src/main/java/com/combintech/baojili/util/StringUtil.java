package com.combintech.baojili.util;

import android.text.TextUtils;

/**
 * Created by Hendry Setiadi
 */

public class StringUtil {
    public static String omitPunctuationAndDoubleSpace(String stringToReplace){
        if (TextUtils.isEmpty(stringToReplace)){
            return "";
        }
        else {
            return stringToReplace.replaceAll("\\r|\\n", " ")
                    .replaceAll("\\s+", " ")
                    .replaceAll("[^0-9a-zA-Z ]", "")
                    .trim();
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static String removeLeadingZero(String s) {
        while (s.length() > 1 && s.indexOf("0")==0) {
            s = s.substring(1);
        }
        return s;
    }

    public static String makeEAN8(String s) {
        s = addLeadingZero(s, 7);
        int checkSum = checkSum(s);
        s += checkSum;
        return s;
    }

    public static String addLeadingZero(String s, int length) {
        while (s.length() < length) {
            s = "0" + s;
        }
        return s;
    }

    public static int checkSum(String code){
        int sum1 = code.charAt(1)+ code.charAt(3) + code.charAt(5) - (3*48);
        int sum2 = 3 * (code.charAt(0) + code.charAt(2) + code.charAt(4) + code.charAt(6) - 4*48);
        int checksum_value = sum1 + sum2;

        int checksum_digit = 10 - (checksum_value % 10);
        if (checksum_digit == 10)
            checksum_digit = 0;
        return checksum_digit;
    }

    public static String removeAllNonNumeric(String s){
        return s.replaceAll("[^\\d]", "");
    }
}
