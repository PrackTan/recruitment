package com.example.demo.payload.request;


import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyRequest {
    private long id;
    @NotBlank(message = "tên công ty không được để trống")
    private String name;
    private String description;
    private String address;
    private String logo;
    private Instant createAt;
    private Instant updatedAt;

}
