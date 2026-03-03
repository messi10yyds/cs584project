package com.basic.backend.mapper;

import com.basic.backend.entity.Medication;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MedicationMapper {
    int insert(Medication m);

    int countByUserId(@Param("userId") Long userId);

    List<Medication> selectByUserId(@Param("userId") Long userId);
}