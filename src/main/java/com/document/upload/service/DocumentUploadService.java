package com.document.upload.service;

import com.document.upload.dto.FileResponse;
import com.document.upload.entity.FileEntity;
import com.document.upload.repository.DocumentRepository;
import com.document.upload.util.EmailService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentUploadService {
    @Autowired
    DocumentRepository documentRepository;

    @Value("${upload.directory}")
    private String uploadDir;

    @Autowired
    EmailService emailService;

    private String SECRET_KEY = "9ogZjRn0rk1qQ8VMiidCCuztOSVjnIbRGfrxekvV3Ls";

    public ResponseEntity<String> upload(MultipartFile document) {
        try {

//            byte[] bytes = document.getBytes();


            FileEntity fileEntity = new FileEntity();
            String fileId = UUID.randomUUID().toString();
            fileEntity.setFileId(fileId);
            fileEntity.setFileName(document.getOriginalFilename());

            String passcode = RandomStringUtils.randomAlphanumeric(8);



            File destinationFile = null;
            try {
                File directory = new File(uploadDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                String filePath = uploadDir + File.separator + document.getOriginalFilename();
                destinationFile = new File(filePath);
                document.transferTo(destinationFile);


                byte[] fileContent = Files.readAllBytes(Paths.get(destinationFile.getAbsolutePath()));
                fileEntity.setFileContent(fileContent);
                documentRepository.save(fileEntity);
                return new ResponseEntity<>("File Uploaded Successfully !!", HttpStatus.OK);
            } catch (IOException e) {
                e.printStackTrace();
                return new ResponseEntity<>("File Upload Failed !!", HttpStatus.INTERNAL_SERVER_ERROR);

            }
        } finally {

        }
    }

    public ResponseEntity<List<FileResponse>> getAllFiles() throws FileNotFoundException {
        List<FileEntity> fileList = documentRepository.findAll();
        if (fileList.isEmpty()) {
            throw new FileNotFoundException("No files found!");
        }


        List<FileResponse> fileResponses = new ArrayList<>();
        for (FileEntity fileEntity : fileList) {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileEntity.getFileContent());
            InputStreamResource resource = new InputStreamResource(byteArrayInputStream);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileEntity.getFileName());
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
            headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileEntity.getFileContent().length));
            FileResponse fileDownloadResponse = new FileResponse();
            fileDownloadResponse.setFileName(fileEntity.getFileName());
            fileDownloadResponse.setFileSize(fileEntity.getFileContent().length);
            fileDownloadResponse.setFileId(fileEntity.getFileId());
            fileResponses.add(fileDownloadResponse);
        }
        return ResponseEntity.ok(fileResponses);
    }

    public String generateAndShareLink(String fileId, String expiryIn, List<String> emails, String passcode) {

        String shareableLink = generateSharableLink(fileId, expiryIn);
        String subject = "URL for the document";

        emailService.sendEmailToUsers(emails, subject, shareableLink);
        return "Shareable URL Generated and Emails Sent Successfully!!!";
    }


    public String generateSharableLink(String fileId, String expiresIn) {
        FileEntity fileEntity = documentRepository.findByFileId(fileId);
        if (fileEntity == null) {
            throw new IllegalArgumentException("Document not found");
        }
        long expirationTimeMillis;

        if (expiresIn == null) {
            expirationTimeMillis = System.currentTimeMillis() + (10000);
        } else {

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date expirationDate;
            try {
                expirationDate = dateFormat.parse(expiresIn);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid expiration date format. Expected dd-MM-yyyy", e);
            }

            expirationTimeMillis = expirationDate.getTime();
        }
        String token = Jwts.builder()
                .setSubject(fileId.toString())
                .setExpiration(new Date(expirationTimeMillis))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();

        String sharableUrl = "http://localhost:8080/file/secure?token=" + token;

        return sharableUrl;
    }

    public FileEntity getDocumentFromLink(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();

            String fileIdString = claims.getSubject();


            FileEntity document = documentRepository.findByFileId(fileIdString);
            if (document == null) {
                throw new IllegalArgumentException("Document not found");
            }

            return document;
        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired token", e);
        }
    }

}