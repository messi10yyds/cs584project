package com.basic.backend.config;

import com.basic.backend.common.Result;
import com.basic.backend.common.ResultCode;
import com.basic.backend.mapper.UserTokenMapper;
import com.basic.backend.security.JwtAuthenticationFilter;
import com.basic.backend.util.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expire-ms:604800000}")
    private long expireMs;

    @Bean
    public JwtTokenUtil jwtTokenUtil() {
        return new JwtTokenUtil(jwtSecret, expireMs);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil, UserTokenMapper userTokenMapper) {
        return new JwtAuthenticationFilter(jwtTokenUtil,userTokenMapper);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public SecurityFilterChain filterChain(
            org.springframework.security.config.annotation.web.builders.HttpSecurity http,
            JwtAuthenticationFilter jwtFilter,
            ObjectMapper objectMapper
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ✅ 第2点：放行 OPTIONS 预检 + /error + /auth/**
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/favicon.ico",
                                "/assets/**",
                                "/error",
                                "/login",
                                "/register",
                                "/init",
                                "/init/**",
                                "/dashboard",
                                "/auth/**",
                                "/vite.svg",
                                "/*.jpg",
                                "/FOOT_SYMPTOMS.jpg"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // ✅ 第3点：统一 401/403 返回 Result
                .exceptionHandling(ex -> ex
                        // 401：未登录 / token 无效 / 没有认证信息
                        .authenticationEntryPoint((request, response, authException) -> {
                            writeJson(response, objectMapper,
                                    HttpServletResponse.SC_UNAUTHORIZED,
                                    Result.fail(ResultCode.UNAUTHORIZED, "Unauthorized"));
                        })
                        // 403：已登录但无权限（你目前没有角色体系，一般不会触发，但留着统一格式）
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            writeJson(response, objectMapper,
                                    HttpServletResponse.SC_FORBIDDEN,
                                    Result.fail(ResultCode.FORBIDDEN, "Forbidden"));
                        })
                )

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private static void writeJson(HttpServletResponse response, ObjectMapper objectMapper, int httpStatus, Object body)
            throws java.io.IOException {
        response.setStatus(httpStatus);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), body);
    }
}