package com.ocr.p3_back.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Une location au format JSON attendu par le front
 * ({@code owner_id}, {@code created_at}, {@code updated_at}).
 */
public class RentalResponse {

    private Integer id;
    private String name;
    private BigDecimal surface;
    private BigDecimal price;
    private String picture;
    private String description;

    @JsonProperty("owner_id")
    private Integer ownerId;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    public RentalResponse() {}

    public RentalResponse(
            Integer id,
            String name,
            BigDecimal surface,
            BigDecimal price,
            String picture,
            String description,
            Integer ownerId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.surface = surface;
        this.price = price;
        this.picture = picture;
        this.description = description;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getSurface() { return surface; }
    public BigDecimal getPrice() { return price; }
    public String getPicture() { return picture; }
    public String getDescription() { return description; }

    @JsonProperty("owner_id")
    public Integer getOwnerId() { return ownerId; }

    @JsonProperty("created_at")
    public LocalDateTime getCreatedAt() { return createdAt; }

    @JsonProperty("updated_at")
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}