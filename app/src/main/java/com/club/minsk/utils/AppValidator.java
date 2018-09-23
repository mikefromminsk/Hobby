package com.club.minsk.utils;

import java.util.regex.Pattern;

public class AppValidator {

    public static boolean isEmpty(String str) {
        return (str == null || str.isEmpty());
    }

    private static Pattern email = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    public static boolean isEmail(String str) {
        return email.matcher(str).matches();
    }

    private static Pattern login = Pattern.compile("[A-Za-z0-9].*");

    public static boolean isLogin(String str) {
        return login.matcher(str).matches();
    }

}
