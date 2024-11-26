package com.document.upload.service;

import com.document.upload.entity.FileEntity;
import com.document.upload.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class DocumentUploadService {
    @Autowired
    DocumentRepository documentRepository;
    @Value("${upload.directory}")
    private String uploadDir;

    public ResponseEntity<String> upload(MultipartFile document) {
        try {

//            byte[] bytes = document.getBytes();


            FileEntity fileEntity = new FileEntity();
            String fileId = UUID.randomUUID().toString();
            fileEntity.setFileId(fileId);
            fileEntity.setFileName(document.getOriginalFilename());
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
}