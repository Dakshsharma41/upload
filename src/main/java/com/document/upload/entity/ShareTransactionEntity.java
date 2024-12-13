package com.document.upload.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.UUID;

@Entity
@Table(name = "share_txn")
@Data

public class ShareTransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private long id;
    @Column(name = "file_id", nullable = false)
    private String fileId;
    @Column(name = "emails")
    private String emails;
    @Column(name = "shareable_url")
    private String shareableUrl;
    @Column(name = "expiry_in")
    private String expiryIn;
    @Column(name = "passcode")
    private String passcode;


}
