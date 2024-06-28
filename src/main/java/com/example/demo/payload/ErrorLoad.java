package com.example.demo.payload;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorLoad {
    private int statusCode;
    private String message;
    private String error;
}
