package com.document.upload.controller;

import com.document.upload.dto.FileResponse;
import com.document.upload.entity.FileEntity;
import com.document.upload.service.DocumentUploadService;
import com.document.upload.util.UrlConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping(value = UrlConstants.FILE)
public class DocumentUploadController {
    @Autowired
    DocumentUploadService documentUploadService;

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping(value = UrlConstants.UPLOAD)
    ResponseEntity<String> uploadDocument(@RequestParam("document") MultipartFile document) {
        System.out.println("upload document controller invoked");
        return documentUploadService.upload(document);

    }
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping(value = UrlConstants.FILE_LIST)
    public ResponseEntity<List<FileResponse>> getAllUploadedFiles() throws FileNotFoundException {
        System.out.println("get document controller invoked");
        return documentUploadService.getAllFiles();
    }

}
