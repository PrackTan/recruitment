package com.example.demo.payload;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class BaseLoad {
    private String message;
    private MetaLoad metaLoad;
    private Object data;
}
