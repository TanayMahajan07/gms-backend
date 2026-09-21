package com.gms.gmsmvp.validation;

public final class ValidationPatterns {

    public static final String GYM_CODE = "^[A-Za-z0-9][A-Za-z0-9_-]{1,49}$";
    public static final String USERNAME = "^[A-Za-z0-9._-]{3,100}$";
    public static final String PERSON_NAME = "^[A-Za-z][A-Za-z .'-]{0,99}$";
    public static final String PERSON_NAME_OPTIONAL = "^$|^[A-Za-z][A-Za-z .'-]{0,99}$";
    public static final String PHONE = "^$|^[+]?[0-9][0-9\\- ]{6,14}$";
    public static final String EMAIL_OPTIONAL = "^$|^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$";
    public static final String CURRENCY = "^(INR|USD|EUR)$";
    public static final String DATE_FORMAT = "^(dd-MM-yyyy|MM-dd-yyyy|yyyy-MM-dd)$";
    public static final String EXPIRY_THRESHOLDS = "^\\d{1,3}(,\\d{1,3}){0,9}$";
    public static final String RECEIPT_PREFIX = "^[A-Za-z0-9_-]{1,20}$";
    public static final String LOGO_URL = "^$|^(https?://).{3,250}$";
    public static final String PASSWORD = "^(?=.*[A-Za-z])(?=.*\\d).{8,100}$";
    public static final String PASSWORD_OPTIONAL = "^$|^(?=.*[A-Za-z])(?=.*\\d).{8,100}$";

    private ValidationPatterns() {
    }
}
