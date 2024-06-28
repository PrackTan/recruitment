package com.example.demo.payload.request;


import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class LoginRequest {
    @NotBlank(message = "Username không được để trống")
    private String userName;
    @NotBlank(message = "Password không được để trống")
    private String password;
}
