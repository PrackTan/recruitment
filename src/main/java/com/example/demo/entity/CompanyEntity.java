package com.example.demo.entity;


import com.example.demo.util.SercurityUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.apache.catalina.security.SecurityUtil;

import java.time.Instant;


@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "companies")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @Column(name = "name")
    String name;
    @Column(name = "description",columnDefinition = "MEDIUMTEXT")
    String description;
    @Column(name = "address")
    String address;
    @Column(name = "logo")
    String logo;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a",timezone = "GMT+7")
    @Column(name = "createAt")
    Instant createAt;
    @Column(name = "updatedAt")
    Instant updatedAt;
    @Column(name = "createBy")
    String createBy;
    @Column(name = "updatedBy")
    String updatedBy;

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
//    @PreRemove
//    public void handleBeforeDelete(){
//        this.updatedBy = SercurityUtil.getCurrentUserLogin().isPresent() == true ? SercurityUtil.getCurrentUserLogin().get() : "";
//        this.updatedAt = Instant.now();
//    }

}
