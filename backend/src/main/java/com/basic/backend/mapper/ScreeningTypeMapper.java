package com.basic.backend.mapper;

import com.basic.backend.entity.ScreeningType;
import org.apache.ibatis.annotations.Param;

public interface ScreeningTypeMapper {
    Integer selectIntervalDaysById(@Param("id") Integer id);

    ScreeningType selectById(@Param("id") Long id);
}