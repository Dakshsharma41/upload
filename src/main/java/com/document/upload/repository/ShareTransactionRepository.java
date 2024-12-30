package com.document.upload.repository;

import com.document.upload.entity.ShareTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShareTransactionRepository extends JpaRepository<ShareTransactionEntity,Long> {
    ShareTransactionEntity findByFileId(String fileId);
}
