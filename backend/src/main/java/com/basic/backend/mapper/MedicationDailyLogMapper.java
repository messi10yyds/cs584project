package com.basic.backend.mapper;

import com.basic.backend.entity.MedicationDailyLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

@Mapper
public interface MedicationDailyLogMapper {

    MedicationDailyLog selectByUserIdAndMedicationIdAndLogDate(@Param("userId") Long userId,
                                                               @Param("medicationId") Long medicationId,
                                                               @Param("logDate") LocalDate logDate);

    int insert(MedicationDailyLog log);

    int updateDosesTakenById(@Param("id") Long id,
                             @Param("dosesTaken") Integer dosesTaken);

}