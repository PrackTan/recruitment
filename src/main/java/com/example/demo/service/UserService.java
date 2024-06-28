package com.example.demo.service;

import com.example.demo.Exception.UsernameExistance;
import com.example.demo.entity.UserEntity;
import com.example.demo.payload.request.UserRequest;
import com.example.demo.payload.response.UserResponse;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.serviceImp.UserServiceImp;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserService implements UserServiceImp {
    //// sử dụng DI (IoC)
//    @Autowired
//    UserRepository userRepository;
//    @Autowired
//    PasswordEncoder passwordEncoder;
    /// sử dụng thường
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserResponse> getListUsers() {
        List<UserEntity> userEntity = userRepository.findAll();
        return userEntity.stream().map(item ->UserResponse.builder()
                .userName(item.getUserName())
                .fullName(item.getFullName())
                .firstName(item.getFirstName())
                .lastName(item.getLastName())
                .phone(item.getPhone())
                .build()).toList();

    }

    @Override
    public UserEntity getUserByUsername(String username) {
        UserEntity userEntity =  userRepository.findByUserName(username);
        return userEntity;
    }

    @Override
    public UserEntity addUser(UserRequest userRequest){
         return userRepository.save(UserEntity.builder()
                        .userName(userRequest.getUserName())
                        .email(userRequest.getEmail())
                        .password(passwordEncoder.encode(userRequest.getPassword()))
                        .fullName(userRequest.getFullName())
                        .gender(userRequest.getGender())
                        .address(userRequest.getAddress())
                        .age(userRequest.getAge())
                        .build());

    }

    @Override
    public boolean CheckUsernameIsExist(String username) {
        return userRepository.existsByUserName(username);
    }
    @Override
    public UserEntity findUserById(long id) {
        return userRepository.findById(id);
    }

    @Override
    public UserEntity updateUser(UserRequest userRequest) {
        UserEntity user = userRepository.findById(userRequest.getId());
        UserEntity userResponse = new UserEntity();
        if(user != null){
            user.setId(userRequest.getId());
            user.setFirstName(userRequest.getFirstName());
            user.setAge(userRequest.getAge());
            user.setGender(userRequest.getGender());
            user.setAddress(userRequest.getAddress());
            user.setPhone(userRequest.getPhone());
            user.setLastName(userRequest.getLastName());
            user.setFullName(userRequest.getFullName());
            return userRepository.save(user);
        }
        return null;
    }


    @Override
    public UserResponse convertUserResponse(UserEntity userEntity) {
       return UserResponse.builder()
                .id(userEntity.getId())
                .fullName(userEntity.getFullName())
                .address(userEntity.getAddress())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .gender(userEntity.getGender())
                .phone(userEntity.getPhone()).build();
    };

    @Override
    @Transactional
    public void deleteUser(long id)  {
         userRepository.deleteById(id);
    }

    @Override
    public UserEntity getUserByEmailAndToken(String token, String email) {
        return userRepository.findByRefreshTokenAndEmail(token, email);
    }

    public void updateUserToken(String token, String email){
        UserEntity userEntity = userRepository.findByUserName(email);
        if(userEntity!= null){
            userEntity.setRefreshToken(token);
            userRepository.save(userEntity);
        }
    }

}
