package com.ocr.p3_back.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MessageRequest {

    @NotNull
    @JsonProperty("rental_id")
    private Integer rentalId;

    @NotBlank
    private String message;

    public Integer getRentalId() { return rentalId; }
    public void setRentalId(Integer rentalId) { this.rentalId = rentalId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}