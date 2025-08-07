package ru.yjailbir.shopappyandexpracticumsprint5.dto;

import org.springframework.lang.Nullable;

public class PaymentsApiPayRequestDto {
    private String userId;
    private Long amount;

    public PaymentsApiPayRequestDto(String userId, Long amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public String getUserId() {
        return userId;
    }

    public Long getAmount() {
        return amount;
    }
}
