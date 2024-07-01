package com.example.demo.service;

import com.example.demo.entity.CompanyEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.payload.request.UserRequest;
import com.example.demo.payload.response.UserResponse;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.serviceImp.UserServiceImp;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


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
    private final CompanyService companyService;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, CompanyService companyService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.companyService = companyService;
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
                .companyEntity(CompanyEntity.builder()
                        .id(item.getCompanyEntity().getId())
                        .name(item.getCompanyEntity().getName())
                        .build())
                .build()).toList();

    }

    @Override
    public UserEntity getUserByUsername(String username) {
        return userRepository.findByUserName(username);
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
                        .companyEntity(CompanyEntity.builder()
                                 .id(userRequest.getCompanyId())
                                 .build())
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
        UserEntity userEntity = userRepository.findById(userRequest.getId());
        if(userEntity != null){
            userEntity.setId(userRequest.getId());
            userEntity.setFirstName(userRequest.getFirstName());
            userEntity.setAge(userRequest.getAge());
            userEntity.setGender(userRequest.getGender());
            userEntity.setAddress(userRequest.getAddress());
            userEntity.setPhone(userRequest.getPhone());
            userEntity.setLastName(userRequest.getLastName());
            userEntity.setFullName(userRequest.getFullName());
            if(userRequest.getCompanyId() > 0){
                Optional<CompanyEntity> companyEntity = companyService.getCompanyById(userRequest.getCompanyId());
                userRequest.setCompanyId(companyEntity.map(CompanyEntity::getId).orElse(0L));
            }

            return userRepository.save(userEntity);
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
                .phone(userEntity.getPhone())
                .companyEntity(userEntity.getCompanyEntity())
                .build();

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
