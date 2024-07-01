package com.example.demo.payload.response;

import com.example.demo.EnumClass.GenderEnum;
import com.example.demo.entity.CompanyEntity;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserResponse {
    private long id;
    private String email;
    private String userName;
    private String fullName;
    private String firstName;
    private String lastName;
    private int phone;
    private int roleId;
    private GenderEnum gender;
    private String address;
    private int age;
    private CompanyEntity companyEntity;

    @Getter
    @Setter
    public static class CompanyUser{
        private long id;
        private String name;
    }
}
