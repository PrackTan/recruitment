package com.example.demo.entity;


import com.example.demo.EnumClass.GenderEnum;
import com.example.demo.util.SercurityUtil;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "email")
    private String email;
    @Column(name = "username")
    private String userName;
    @Column(name = "pwd")
    private String password;
    @Column(name = "fullname")
    private String fullName;
    @Column(name = "firstname")
    private String firstName;
    @Column(name = "lastname")
    private String lastName;
    @Column(name = "phone")
    private int phone;
    @Column(name = "age")
    private int age;
    @Enumerated(EnumType.STRING)
    private GenderEnum gender;
    @Column(name = "address")
    private String address;
    @Column(name = "refreshToken")
    private String refreshToken;
    @Column(name = "createAt")
    private Instant createAt;
    @Column(name = "updatedAt")
    private Instant updatedAt;
    @Column(name = "createBy")
    private String createBy;
    @Column(name = "updatedBy")
    private String updatedBy;
    @ManyToOne
    @JoinColumn(name = "idRole")
    private RoleEntity role;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private CompanyEntity companyEntity;
    @PrePersist
    public void handleBeforeCreate(){
        this.createBy = SercurityUtil.getCurrentUserLogin().isPresent() == true ? SercurityUtil.getCurrentUserLogin().get():"";

        this.createAt = Instant.now();
    }
    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedBy = SercurityUtil.getCurrentUserLogin().isPresent() == true ? SercurityUtil.getCurrentUserLogin().get() : "";
        this.updatedAt = Instant.now();
    }
}
