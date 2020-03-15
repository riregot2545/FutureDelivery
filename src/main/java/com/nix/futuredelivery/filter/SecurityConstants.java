package com.nix.futuredelivery.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Component
public class SecurityConstants {
    public static final String AUTH_LOGIN_URL = "/authorize";

    // Signing key for HS512 algorithm
    // You can use the page http://www.allkeysgenerator.com/ to generate all kinds of keys
    @Value("${spring.security.jwt.token.JWT_SECRET}")
    public String JWT_SECRET;

    // JWT token defaults
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_TYPE = "JWT";
    public static final String TOKEN_ISSUER = "secure-api";
    public static final String TOKEN_AUDIENCE = "secure-app";

    /*private SecurityConstants() {
       // throw new IllegalStateException("Cannot create instance of static util class");
    }*/

}
