package com.basic.backend.mapper;

import org.apache.ibatis.annotations.Param;

public interface ScreeningTypeMapper {
    Integer selectIntervalDaysById(@Param("id") Integer id);
}