package com.document.upload.repository;

import com.document.upload.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<FileEntity,Long> {
}
