package com.epmapat.erp_epmapat.modelo;

import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "mobile_reading_receipts")
@Data
public class MobileReadingReceipt {
    @Column(nullable=false, length=16) private String operationKind = "READING";
    private String resultPath;
    @Id @Column(length = 36) private String operationId;
    @Column(nullable = false) private Long readerId;
    @Column(nullable = false) private Long readingId;
    @Column(nullable = false, length = 160) private String payloadHash;
    @Column(nullable = false) private java.time.Instant confirmedAt;
}
