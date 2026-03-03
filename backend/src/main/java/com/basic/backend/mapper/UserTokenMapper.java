package com.basic.backend.mapper;

import com.basic.backend.entity.UserToken;
import org.apache.ibatis.annotations.Param;

public interface UserTokenMapper {

    UserToken selectByUserId(@Param("userId") Long userId);

    int upsert(@Param("userId") Long userId,
               @Param("token") String token,
               @Param("expireAt") java.time.LocalDateTime expireAt);

    int deleteByUserId(@Param("userId") Long userId); // 可选：登出用
}