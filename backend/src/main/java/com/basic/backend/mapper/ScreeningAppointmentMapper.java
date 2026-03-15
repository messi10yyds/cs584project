package com.basic.backend.mapper;

import com.basic.backend.entity.ScreeningAppointment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

@Mapper
public interface ScreeningAppointmentMapper {

    ScreeningAppointment selectNearestScheduledAppointment(
            @Param("userId") Long userId,
            @Param("screeningTypeId") Long screeningTypeId,
            @Param("today") LocalDate today
    );
}