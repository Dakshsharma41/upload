package com.document.upload.controller;

import com.document.upload.dto.FileResponse;
import com.document.upload.entity.FileEntity;
import com.document.upload.service.DocumentUploadService;
import com.document.upload.util.UrlConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
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

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/generate-link")
    public ResponseEntity<String> generateSharableLink(@RequestParam String fileId) {
        try {
            String sharableUrl = documentUploadService.generateSharableLink(fileId,null);
            return new ResponseEntity<>(sharableUrl, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error generating link: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping(value = UrlConstants.GENERATE_AND_SHARE)
    public ResponseEntity<String> generateAndShareLink(@RequestParam String fileId,@RequestParam String expiryIn,@RequestParam List<String> emails,@RequestParam String passcode) {
        try {
            String response = documentUploadService.generateAndShareLink(fileId,expiryIn,emails,passcode);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error generating link and failed to trigger emails: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @GetMapping("/secure")
    public ResponseEntity<byte[]> getDocument(@RequestParam String token) {
        try {
            FileEntity document = documentUploadService.getDocumentFromLink(token);

            String mimeType = determineMimeType(document.getFileName());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(mimeType));

            headers.setContentDisposition(ContentDisposition.inline().filename(document.getFileName()).build());


            return ResponseEntity.ok()
                    .headers(headers)
                    .body(document.getFileContent());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }


    private String determineMimeType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        switch (extension) {
            case "pdf":
                return "application/pdf";
            case "doc":
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "ppt":
            case "pptx":
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "txt":
                return "text/plain";
            default:
                return "application/octet-stream";
        }
    }
}