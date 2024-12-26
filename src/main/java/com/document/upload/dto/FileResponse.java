package com.document.upload.dto;

import java.io.File;
import java.util.Date;

import lombok.Data;

@Data
public class FileResponse {

    private long fileSize;
    private String fileName;
    private String filesize;
    private String fileId;
    private String description;
    private String uploadedBy;
    private Date uploadedAt;
}
