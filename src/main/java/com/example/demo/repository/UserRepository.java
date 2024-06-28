package com.example.demo.repository;

import com.example.demo.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    UserEntity findByUserName(String username);
    boolean existsByUserName(String username);

    void deleteById(long id);

    UserEntity findById(long id);

    UserEntity findByRefreshTokenAndEmail(String token, String email);
}
