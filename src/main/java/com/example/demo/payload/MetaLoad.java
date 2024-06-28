package com.example.demo.payload;


import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetaLoad {
    private int page;
    private int pageSize;
    private int pages;
    private long total;
}
