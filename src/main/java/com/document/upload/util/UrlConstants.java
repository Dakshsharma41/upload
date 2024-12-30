package com.document.upload.util;

public class UrlConstants {
    public static final String UPLOAD = "/upload";
    public static final String FILE = "file";
    public static final String FILE_LIST = "/list";
    public static final String USERS = "/api/users";
    public static final String GENERATE_AND_SHARE = "/generate/share";
    public static final String UPLOAD_TXN = "/get/upload/txn";
    public static final String EDIT_EXPIRY = "/edit/expiry";

    private UrlConstants() {
        throw new IllegalStateException("Utility class");
    }
}

