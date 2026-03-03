package com.basic.backend.mapper;

import com.basic.backend.entity.UserScreening;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserScreeningMapper {
    int insert(UserScreening us);

    int countByUserId(@Param("userId") Long userId);

    List<UserScreening> selectByUserId(@Param("userId") Long userId);
}