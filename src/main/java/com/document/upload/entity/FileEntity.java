package com.document.upload.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.UUID;

@Entity
@Table(name = "document_txn")
@Data
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid", updatable = false, nullable = false)
    private UUID uuid;
    @Column(name = "file_id", nullable = false, unique = true)
    private String fileId;
    @Column(name = "file_name", nullable = false)
    private String fileName;



    @Column
    @JdbcTypeCode(Types.VARBINARY)
    private byte[] fileContent;

}
