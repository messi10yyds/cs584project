package com.basic.backend.security;

import com.basic.backend.entity.UserToken;
import com.basic.backend.mapper.UserTokenMapper;
import com.basic.backend.util.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserTokenMapper userTokenMapper;

    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil, UserTokenMapper userTokenMapper) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userTokenMapper = userTokenMapper;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            String auth = request.getHeader(HttpHeaders.AUTHORIZATION);

            if (StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
                String token = auth.substring(7);

                if (jwtTokenUtil.validate(token)) {
                    String subject = jwtTokenUtil.getSubject(token); // userId
                    Long userId;
                    try {
                        userId = Long.valueOf(subject);
                    } catch (NumberFormatException e) {
                        filterChain.doFilter(request, response);
                        return;
                    }

                    // ✅ DB 校验：必须等于当前 token，且未过期
                    UserToken db = userTokenMapper.selectByUserId(userId);
                    if (db != null
                            && token.equals(db.getToken())
                            && db.getExpireAt() != null
                            && db.getExpireAt().isAfter(LocalDateTime.now())) {

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(subject, null, Collections.emptyList());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}