package com.basic.backend.service;

import com.basic.backend.dto.LoginResponseDTO;
import com.basic.backend.entity.User;
import com.basic.backend.exception.BizException;
import com.basic.backend.mapper.UserMapper;
import com.basic.backend.mapper.UserTokenMapper;
import com.basic.backend.util.JwtTokenUtil;
import com.basic.backend.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final UserTokenMapper userTokenMapper;
    private final JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.expire-ms}")
    private long expireMs;

    public LoginResponseDTO login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BizException("Invalid username or password");
        }
        if (!PasswordUtil.matches(password, user.getPasswordHash())) {
            throw new BizException("Invalid username or password");
        }

        String token = jwtTokenUtil.generateToken(String.valueOf(user.getId()));

        // ✅ 写入/覆盖当前 token（旧 token 立刻失效）
        LocalDateTime expireAt = LocalDateTime.now().plusNanos(expireMs * 1_000_000L);
        userTokenMapper.upsert(user.getId(), token, expireAt);

        return new LoginResponseDTO(user.getId(), token, "Bearer");
    }

    public LoginResponseDTO register(String username, String password) {

        // 1) username 查重
        User existing = userMapper.selectByUsername(username);
        if (existing != null) {
            throw new BizException("Username already exists");
        }

        // 2) 写入 users
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(PasswordUtil.hash(password)); // BCrypt
        userMapper.insert(user); // 自增主键回填到 user.id

        // 3) 签发 JWT（subject = userId）
        String token = jwtTokenUtil.generateToken(String.valueOf(user.getId()));

        // 4) 写入/覆盖 token（与 login 完全一致）
        LocalDateTime expireAt = LocalDateTime.now().plusNanos(expireMs * 1_000_000L);
        userTokenMapper.upsert(user.getId(), token, expireAt);

        return new LoginResponseDTO(user.getId(), token, "Bearer");
    }
}