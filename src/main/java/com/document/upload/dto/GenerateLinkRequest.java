package com.document.upload.dto;

import lombok.Data;

import java.util.List;

@Data
public class GenerateLinkRequest {

    String fileId;
    String expiryIn;
    String passcode;
    List<String> emails;
}
