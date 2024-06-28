package com.example.demo.payload.request;

import com.example.demo.EnumClass.GenderEnum;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserRequest {
    private long id;
    private String email;
    @NotNull(message = "Không được để trống username")
    private String userName;
    @NotNull(message = "Không được để trống password")
    private String password;
    private String fullName;
    private String firstName;
    private String lastName;
    private int phone;
    private int roleId;
    private GenderEnum gender;
    private String address;
    private int age;
}
