package com.example.demo.entity;


import jakarta.persistence.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Entity(name = "role")
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name="name")
    private String name;
    @Column(name = "description")
    private String description;
    @OneToMany(mappedBy = "role")
    Set<UserEntity> userList;
}
