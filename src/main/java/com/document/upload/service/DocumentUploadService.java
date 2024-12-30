package com.document.upload.service;

import com.document.upload.dto.FileResponse;
import com.document.upload.entity.FileEntity;
import com.document.upload.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
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


    public ResponseEntity<String> upload(MultipartFile document, String uploadedBy, String description) {
        try {

//            byte[] bytes = document.getBytes();


            FileEntity fileEntity = new FileEntity();
            String fileId = UUID.randomUUID().toString();
            fileEntity.setFileId(fileId);
            fileEntity.setFileName(document.getOriginalFilename());
            fileEntity.setCreatedAt(Date.from(Instant.now()));
            fileEntity.setUploadedBy(uploadedBy);
            fileEntity.setDescription(description);


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
            fileDownloadResponse.setDescription(fileEntity.getDescription());
            fileDownloadResponse.setUploadedAt(fileEntity.getCreatedAt());
            fileDownloadResponse.setUploadedBy(fileEntity.getUploadedBy());
            fileResponses.add(fileDownloadResponse);
        }
        return ResponseEntity.ok(fileResponses);
    }


}