package com.example.demo.util;


import com.example.demo.payload.response.LoginResponse;
import com.nimbusds.jose.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SercurityUtil {
    @Value("${jwt.secretKey}")
    private String jwtKey;
    @Value("${jwt.access-token-validity-in-seconds}")
    private long accessTokenExpiration;
    @Value("${jwt.refresh-token-in-seconds}")
    private long refreshTokenExpiration;
    // khai báo Chuẩn mã hóa
    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;
    private final JwtEncoder  jwtEncoder;

    public SercurityUtil(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }


//  tạo accesstoken  trả cho authController
    public String createToken (String email, LoginResponse.UserLogin loginResponse){
        // lấy giá trị thời gian hiện tại
        Instant now = Instant.now();
        /// lấy giá trị hiện tại + thời gian expriration theo GIÂY
        Instant validaty = now.plus(accessTokenExpiration, ChronoUnit.SECONDS);
        //hard code permission
        List<String> listAuth = new ArrayList<>();
        listAuth.add("ROLE_USER_CREATE");
        listAuth.add("ROLE_USER_UPDATE");

        // body token
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validaty)
                // có thể thêm bao nhiêu claim(object) cũng đc
                .subject(email)
                .claim("user",loginResponse)
                .claim("permission",listAuth)
                .build();


        // header token
        JwsHeader jwtHeader = JwsHeader.with(JWT_ALGORITHM).build();

        //return jwt encode
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwtHeader, jwtClaimsSet)).getTokenValue();

    };

    public String createFreshToken (String email, LoginResponse loginResponse){
        // lấy giá trị thời gian hiện tại
        Instant now = Instant.now();
        /// lấy giá trị hiện tại + thời gian expriration theo GIÂY
        Instant validaty = now.plus(refreshTokenExpiration, ChronoUnit.SECONDS);

        // body token
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validaty)
                .subject(email)
                .claim("user",loginResponse.getUserLogin())
                .build();


        // header token
        JwsHeader jwtHeader = JwsHeader.with(JWT_ALGORITHM).build();

        //return jwt encode
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwtHeader, jwtClaimsSet)).getTokenValue();

    };


    //get keyjwk để check refreshtoken
    private SecretKey getJwtSecretKey(){
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes,0,keyBytes.length, JWT_ALGORITHM.getName());
    }
    /// check validate refreshtoken
    //decode cho JWT REFRESHTOKEN
    public Jwt checkValidRefreshToken(String token){
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(getJwtSecretKey()).macAlgorithm(SercurityUtil.JWT_ALGORITHM).build();
        try {
          return  jwtDecoder.decode(token);
        }catch (Exception e){
            System.out.println(">>> Refresh token error: "+e.getMessage());
            throw e;
        }
    }


    /**
     * Get the login of the current user.
     *
     * @return the login of the current user.
     */
    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        } else if (authentication.getPrincipal() instanceof String s) {
            return s;
        }
        return null;
    }

    /**
     * Get the JWT of the current user.
     *
     * @return the JWT of the current user.
     */
    public static Optional<String> getCurrentUserJWT() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .filter(authentication -> authentication.getCredentials() instanceof String)
                .map(authentication -> (String) authentication.getCredentials());
    }

    /**
     * Check if a user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise.
     */
//    public static boolean isAuthenticated() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        return authentication != null && getAuthorities(authentication).noneMatch(AuthoritiesConstants.ANONYMOUS::equals);
//    }

    /**
     * Checks if the current user has any of the authorities.
     *
     * @param authorities the authorities to check.
     * @return true if the current user has any of the authorities, false otherwise.
     */
//    public static boolean hasCurrentUserAnyOfAuthorities(String... authorities) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        return (
//                authentication != null && getAuthorities(authentication).anyMatch(authority -> Arrays.asList(authorities).contains(authority))
////        );
//    }

    /**
     * Checks if the current user has none of the authorities.
     *
     * @param authorities the authorities to check.
     * @return true if the current user has none of the authorities, false otherwise.
     */
//    public static boolean hasCurrentUserNoneOfAuthorities(String... authorities) {
//        return !hasCurrentUserAnyOfAuthorities(authorities);
//    }

    /**
     * Checks if the current user has a specific authority.
     *
     * @param authority the authority to check.
     * @return true if the current user has the authority, false otherwise.
     */
//    public static boolean hasCurrentUserThisAuthority(String authority) {
//        return hasCurrentUserAnyOfAuthorities(authority);
//    }
//
//    private static Stream<String> getAuthorities(Authentication authentication) {
//        return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority);
//    }
}
