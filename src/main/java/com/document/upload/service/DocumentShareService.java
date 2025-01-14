package com.document.upload.service;

import com.document.upload.dto.FileIdRequest;
import com.document.upload.dto.UploadTxnResponse;
import com.document.upload.entity.FileEntity;
import com.document.upload.entity.ShareTransactionEntity;
import com.document.upload.repository.DocumentRepository;
import com.document.upload.repository.ShareTransactionRepository;
import com.document.upload.util.EmailService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentShareService {
    @Autowired
    DocumentRepository documentRepository;

    @Autowired
    ShareTransactionRepository shareTransactionRepository;

    @Value("${upload.directory}")
    private String uploadDir;

    @Autowired
    EmailService emailService;

    @Value("classpath:templates/mailTemplate.html")
    private Resource emailTemplate;

    private String SECRET_KEY = "9ogZjRn0rk1qQ8VMiidCCuztOSVjnIbRGfrxekvV3Ls";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public ShareTransactionEntity generateAndShareLink(String fileId, String expiryIn, List<String> emails, String passcode) {
        try {
            String shareableLink = generateSharableLink(fileId, expiryIn);
            FileEntity fileEntity=documentRepository.findByFileId(fileId);
            String subject = "URL for the document";
            String htmlContent = loadTemplate().replace("{{link}}", shareableLink).replace("{{documentName}}", fileEntity.getFileName());;

            emailService.sendEmailToUsers(emails, subject, htmlContent);


            ShareTransactionEntity shareTransactionEntity = new ShareTransactionEntity();
            shareTransactionEntity.setFileId(fileId);
            shareTransactionEntity.setExpiryIn(expiryIn);
            String result = String.join(",", emails);
            shareTransactionEntity.setEmails(result);
            shareTransactionEntity.setShareableUrl(shareableLink);
            shareTransactionEntity.setPasscode(passcode);
            shareTransactionRepository.save(shareTransactionEntity);
            return shareTransactionEntity;
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }

    }

    private String loadTemplate() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(emailTemplate.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load email template", e);
        }
    }

    public String generateSharableLink(String fileId, String expiresIn) {
        FileEntity fileEntity = documentRepository.findByFileId(fileId);
        if (fileEntity == null) {
            throw new IllegalArgumentException("Document not found");
        }
        long expirationTimeMillis;

        if (expiresIn == null) {
            expirationTimeMillis = System.currentTimeMillis() + (60000);
        } else {
            try {
                LocalDateTime expirationDateTime = LocalDateTime.parse(expiresIn, DATE_FORMATTER);
                expirationTimeMillis = expirationDateTime
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli();

            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid expiration date format. Expected dd-MM-yyyy", e);
            }


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


    public ResponseEntity<List<UploadTxnResponse>> getUploadTxn() throws Exception {
        List<ShareTransactionEntity> uploadTxnList = shareTransactionRepository.findAll();
        if (uploadTxnList.isEmpty()) {
            throw new Exception("No upload transaction found!!");
        }


        List<UploadTxnResponse> uploadTxnResponseList = new ArrayList<>();
        for (ShareTransactionEntity shareTransactionEntity : uploadTxnList) {


            UploadTxnResponse uploadTxnResponse = new UploadTxnResponse();
            uploadTxnResponse.setUrl(shareTransactionEntity.getShareableUrl());
            uploadTxnResponse.setRecipients(shareTransactionEntity.getEmails());
            uploadTxnResponse.setPasscode(shareTransactionEntity.getPasscode());
            uploadTxnResponse.setExpiryIn(shareTransactionEntity.getExpiryIn());

            FileEntity fileEntity = documentRepository.findByFileId(shareTransactionEntity.getFileId());
            String fileName = fileEntity.getFileName();
            uploadTxnResponse.setFileName(fileName);
            uploadTxnResponseList.add(uploadTxnResponse);
        }
        return ResponseEntity.ok(uploadTxnResponseList);
    }


    public ResponseEntity<String> editExpiryDuration(FileIdRequest fileId) throws Exception {
        ShareTransactionEntity shareTransaction = shareTransactionRepository.findByFileId(fileId.getFileId());

        return null;

    }
}
