package com.example.demo.controller;


import com.example.demo.Exception.IdValidationException;
import com.example.demo.entity.UserEntity;
import com.example.demo.payload.BaseLoad;
import com.example.demo.payload.MetaLoad;
import com.example.demo.payload.request.LoginRequest;
import com.example.demo.payload.response.LoginResponse;
import com.example.demo.service.UserService;
import com.example.demo.util.SercurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
//    @Autowired
//    AuthenticationManagerBuilder authenticationManagerBuilder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SercurityUtil sercurityUtil;
    @Value("${jwt.refresh-token-in-seconds}")
    private long jwtRefreshToken;

    private final UserService userService;
    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SercurityUtil sercurityUtil, UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.sercurityUtil = sercurityUtil;
        this.userService = userService;
    }
    @PostMapping("/login")
    public ResponseEntity<?> Login(@Valid @RequestBody LoginRequest loginRequest){
//        String userName = loginRequest.getUserName();
//        String password = loginRequest.getPassword();


        // truyền  username password vào chuẩn authen
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getUserName(),loginRequest.getPassword());
//

        // xác thực người dùng
        // sau khi xác thực người dùng sẽ trả qua userdetail loaduserbyusername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);


        /// bước cuối cùng sau khi đăng nhập xin ra contextHolder
        //set thông tin người dùng đăng nhập vào holdercontext ( có thể sử dụng sau này)
        SecurityContextHolder.getContext().setAuthentication(authentication);
        LoginResponse loginResponse = new LoginResponse();
        UserEntity currentUser = userService.getUserByUsername(loginRequest.getUserName());
        if(currentUser != null){
            LoginResponse.UserLogin userLogin = new LoginResponse.UserLogin();
            userLogin.setId(currentUser.getId());
            userLogin.setName(currentUser.getFullName());
            userLogin.setEmail(currentUser.getEmail());
            loginResponse.setUserLogin(userLogin);
        }


        // để cho service lấy thông tin người dùng trong context ra
        //create token lấy từ sercurityUtil
        String accessToken = sercurityUtil.createToken(authentication.getName(),loginResponse.getUserLogin());
        loginResponse.setAccess_token(accessToken);

        // create refreshToken
        String refresh_token = this.sercurityUtil.createFreshToken(loginRequest.getUserName(),loginResponse);

        //update user
        userService.updateUserToken(refresh_token,loginRequest.getUserName());

        //set cookie
        ResponseCookie responseCookie = ResponseCookie
                .from("refresh_token",refresh_token)
                .path("/")
                .secure(true)
                .httpOnly(true)
                .maxAge(jwtRefreshToken)
                .build();

        MetaLoad metaLoad = new MetaLoad();
        BaseLoad baseLoad = new BaseLoad("LOGIN SUCCESS",metaLoad,loginResponse);


        return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE,responseCookie.toString()).body(baseLoad);

    }

    @GetMapping("/account")
    public ResponseEntity<?> getAccount(){
        String email = SercurityUtil.getCurrentUserLogin().isPresent()?SercurityUtil.getCurrentUserLogin().get() :"";
        UserEntity currentUser = userService.getUserByUsername(email);
        LoginResponse.UserLogin userLogin = new LoginResponse.UserLogin();
        LoginResponse.UserGetAccount userGetAccount = new LoginResponse.UserGetAccount();
        if(currentUser != null){
            userLogin.setId(currentUser.getId());
            userLogin.setName(currentUser.getFullName());
            userLogin.setEmail(currentUser.getEmail());
            userGetAccount.setUserLogin(userLogin);
        }
        MetaLoad metaLoad = new MetaLoad();
        BaseLoad baseLoad = new BaseLoad();
        baseLoad.setMetaLoad(metaLoad);
        baseLoad.setMessage("get infor account");
        baseLoad.setData(userGetAccount);
        return ResponseEntity.ok().body(baseLoad);
    }
//(get refresh mục đích để không cần đăng nhập lần 2 vẫn có thể vào đc và khi refresh token hêt hạn sẽ tạo lại accesstoken và tạo lại refresh token)
    @GetMapping("/refresh")
    public ResponseEntity<?> getRefreshToken(@CookieValue(name = "refresh_token",defaultValue = "null")String refresh_token) throws IdValidationException {
      if(refresh_token.equals("null") ){
            throw new IdValidationException("Refresh token error");
      }
        // check token
       Jwt decodeToken =  sercurityUtil.checkValidRefreshToken(refresh_token);
       String email = decodeToken.getSubject();
       // check user by token + email
        UserEntity userEntity = userService.getUserByEmailAndToken(refresh_token,email);
        if(userEntity==null){
            throw new IdValidationException("Refresh token không hợp lệ");
        }
        /// issuse new token / set refreshe token add to cookie
        LoginResponse loginResponse = new LoginResponse();
        UserEntity currentUser = userService.getUserByUsername(email);
        if(currentUser != null){
            LoginResponse.UserLogin userLogin = new LoginResponse.UserLogin();
            userLogin.setId(currentUser.getId());
            userLogin.setName(currentUser.getFullName());
            userLogin.setEmail(currentUser.getEmail());
            loginResponse.setUserLogin(userLogin);
        }
        // để cho service lấy thông tin người dùng trong context ra
        //create token lấy từ sercurityUtil
        String accessToken = sercurityUtil.createToken(email,loginResponse.getUserLogin());
        loginResponse.setAccess_token(accessToken);

        // create refreshToken
        String new_refresh_token = this.sercurityUtil.createFreshToken(email,loginResponse);

        //update user
        userService.updateUserToken(refresh_token,email);

        //set cookie
        ResponseCookie responseCookie = ResponseCookie
                .from("refresh_token",new_refresh_token)
                .path("/")
                .secure(true)
                .httpOnly(true)
                .maxAge(jwtRefreshToken)
                .build();

        MetaLoad metaLoad = new MetaLoad();
        BaseLoad baseLoad = new BaseLoad("LOGIN SUCCESS",metaLoad,loginResponse);


        return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE,responseCookie.toString()).body(baseLoad);

    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() throws IdValidationException{
        String email = sercurityUtil.getCurrentUserJWT().isPresent()?sercurityUtil.getCurrentUserLogin().get():"";
        if(email.equals("")){
            throw new IdValidationException("Token không hợp lệ");
        }
        // update refresh token = null
        userService.updateUserToken(null,email);
        //remove  refresh token cookies
        ResponseCookie responseCookie = ResponseCookie
                .from("refresh_token",null)
                .path("/")
                .secure(true)
                .httpOnly(true)
                .maxAge(0)
                .build();

        MetaLoad metaLoad = new MetaLoad();
        BaseLoad baseLoad = new BaseLoad("LOGIN SUCCESS",metaLoad,null);


        return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE,responseCookie.toString()).body(null);


    }
}
