package com.document.upload.dto;

import java.io.File;

import lombok.Data;

@Data
public class FileResponse {

    private long fileSize;
    private String downloadUrl;
    private String fileName ;
    private String filesize ;
}
