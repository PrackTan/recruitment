package com.example.demo.service.serviceImp;


import com.example.demo.Exception.UsernameExistance;
import com.example.demo.entity.UserEntity;
import com.example.demo.payload.request.UserRequest;
import com.example.demo.payload.response.UserResponse;

import java.util.List;

public interface UserServiceImp {
    List<UserResponse> getListUsers();
    UserEntity getUserByUsername(String username);
    UserEntity addUser(UserRequest userRequest);

    boolean CheckUsernameIsExist (String username);

    UserEntity updateUser(UserRequest userRequest);
    UserEntity findUserById(long id);
    UserResponse convertUserResponse(UserEntity userEntity);
    void deleteUser(long id);

    UserEntity getUserByEmailAndToken(String token, String email);

}
