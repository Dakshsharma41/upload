package com.document.upload.controller;

import com.document.upload.service.DocumentUploadService;
import com.document.upload.util.UrlConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = UrlConstants.FILE)
public class DocumentUploadController {
    @Autowired
    DocumentUploadService documentUploadService;

    @PostMapping(value = UrlConstants.UPLOAD)
    ResponseEntity<String> uploadDocument(@RequestParam("document") MultipartFile document) {
        return documentUploadService.upload(document);

    }
}
