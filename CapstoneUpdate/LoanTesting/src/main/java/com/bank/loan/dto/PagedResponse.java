package com.bank.loan.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PagedResponse<T> {

    private List<T> content;

    private long totalElements;

    private int totalPages;

    private int page;

    private int size;
}
