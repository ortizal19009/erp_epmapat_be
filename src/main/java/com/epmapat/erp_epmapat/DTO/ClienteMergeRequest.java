package com.epmapat.erp_epmapat.DTO;

import java.util.List;

import lombok.Data;

@Data
public class ClienteMergeRequest {
    private Long masterId;
    private List<Long> duplicateIds;
}
