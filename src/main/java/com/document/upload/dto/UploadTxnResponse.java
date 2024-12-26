package com.document.upload.dto;

import lombok.Data;



@Data
public class UploadTxnResponse {
    private String fileName;
    private String recipients;
    private String expiryIn;
    private String url;
    private String passcode;
}
