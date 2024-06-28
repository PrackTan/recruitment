package com.example.demo.controller;


import com.example.demo.Exception.IdValidationException;
import com.example.demo.Exception.UsernameExistance;
import com.example.demo.entity.UserEntity;
import com.example.demo.payload.BaseLoad;
import com.example.demo.payload.MetaLoad;
import com.example.demo.payload.request.UserRequest;
import com.example.demo.payload.response.UserResponse;
import com.example.demo.service.UserService;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.SecretJWK;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    UserService userService;
    @GetMapping("")
    public ResponseEntity<?> getListUser(){
        List<UserResponse> userResponses = userService.getListUsers();
//        SecureRandom random = new SecureRandom();
//        byte[] sharedSecret = new byte[32];
//        random.nextBytes(sharedSecret);
//        String encodedSecret = Base64.getEncoder().encodeToString(sharedSecret);
//        System.out.println(encodedSecret);
        return new ResponseEntity<>(userResponses,HttpStatus.OK);

    }
    @PostMapping("")
    public ResponseEntity<?> addUser(@Valid @RequestBody UserRequest userRequest) throws UsernameExistance {
        boolean isExistUsername = userService.CheckUsernameIsExist(userRequest.getUserName());
        if(isExistUsername){
            throw new UsernameExistance("Username tồn tại");
        }
      UserEntity userEntity = userService.addUser(userRequest);
      MetaLoad metaLoad = new MetaLoad();
      BaseLoad baseLoad = new BaseLoad();
      baseLoad.setMessage("Đăng ký thành công");
      baseLoad.setMetaLoad(metaLoad);
      baseLoad.setData(userEntity);
      return ResponseEntity.status(HttpStatus.CREATED).body(baseLoad);
    }
    @PutMapping("")
    public ResponseEntity<?> updateUser(@RequestBody UserRequest userRequest) throws IdValidationException {
        UserEntity userEntity = userService.updateUser(userRequest);
        if(userEntity == null){
            throw new IdValidationException("user không tồn tại");
        }
        UserResponse userResponse = userService.convertUserResponse(userEntity);
        MetaLoad metaLoad = new MetaLoad();
        BaseLoad baseLoad = new BaseLoad();
        baseLoad.setMetaLoad(metaLoad);
        baseLoad.setMessage("Update thành công");
        baseLoad.setData(userResponse);
        return ResponseEntity.status(HttpStatus.OK).body(baseLoad);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable long id) throws IdValidationException{
        UserEntity userEntity = userService.findUserById(id);
        if(userEntity == null){
            throw new IdValidationException("người dùng không tồn tại");
        }
        userService.deleteUser(id);
        MetaLoad metaLoad = new MetaLoad();
        BaseLoad baseLoad = new BaseLoad("Xóa thành công",metaLoad,null);
        return ResponseEntity.status(HttpStatus.OK).body(baseLoad);
    }
}
