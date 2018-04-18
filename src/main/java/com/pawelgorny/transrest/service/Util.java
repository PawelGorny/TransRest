package com.pawelgorny.transrest.service;

import java.text.SimpleDateFormat;

public class Util {
    private static final String  RQL_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static SimpleDateFormat getRQLDateFormatter() {
        return new SimpleDateFormat(RQL_DATE_FORMAT);
    }

}
