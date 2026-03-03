package com.basic.backend.mapper;

import com.basic.backend.entity.HealthProfile;
import org.apache.ibatis.annotations.Param;

public interface HealthProfileMapper {
    int insert(HealthProfile profile);

    HealthProfile selectByUserId(@Param("userId") Long userId);
}