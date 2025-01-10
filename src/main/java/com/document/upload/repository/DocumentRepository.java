package com.document.upload.repository;

import com.document.upload.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DocumentRepository extends JpaRepository<FileEntity, Long> {
    FileEntity findByFileId(String fileId);

}
