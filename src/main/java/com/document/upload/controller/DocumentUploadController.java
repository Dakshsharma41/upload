package com.document.upload.controller;

import com.document.upload.dto.FileResponse;
import com.document.upload.entity.FileEntity;
import com.document.upload.service.DocumentUploadService;
import com.document.upload.util.UrlConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.UUID;

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
    @GetMapping("/generate-link")
    public ResponseEntity<String> generateSharableLink(@RequestParam String fileId) {
        try {
            String sharableUrl = documentUploadService.generateSharableLink(fileId);
            return new ResponseEntity<>(sharableUrl, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error generating link: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/secure")
    public ResponseEntity<byte[]> getDocument(@RequestParam String token) {
        try {
            FileEntity document = documentUploadService.getDocumentFromLink(token);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + document.getFileName())
                    .body(document.getFileContent());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }
}
