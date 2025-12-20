package com.example.splitly.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillOcrResponse {

    private String merchantName;
    private String merchantAddress;
    private LocalDate billDate;
    private LocalDateTime billDateTime;
    private String billNumber;
    private String taxId;

    private List<BillItem> items;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal serviceCharge;
    private BigDecimal total;

    private String currency;
    private String paymentMethod;

    private String rawText;
    private Integer confidence;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BillItem {
        private String name;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
        private String notes;
    }
}