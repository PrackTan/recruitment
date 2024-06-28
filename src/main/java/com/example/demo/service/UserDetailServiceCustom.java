package com.example.demo.service;

import com.example.demo.entity.UserEntity;
import com.example.demo.payload.response.UserResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

// nhận từ authenticationManagerBuilder để qua đây xác thực và trả về user
//component ghi đè gián tiếp thông qua tên của bean UserDetailsService
@Component("userDetailsService")
public class UserDetailServiceCustom implements UserDetailsService {
    private final UserService userService;

    public UserDetailServiceCustom(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userService.getUserByUsername(username);
        if(user == null){
            throw new UsernameNotFoundException("Username hoặc password không tồn tại");
        }
        /// dựa vào tiính đa hình return User (children) tự spring hình dung ra thằng UserDetails (parent)
        return new User(
                user.getUserName(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
