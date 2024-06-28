package com.example.demo.payload.response;

import lombok.*;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class RoleResponse {
    private String name;
    private String description;
    List<UserResponse> userList;

}
