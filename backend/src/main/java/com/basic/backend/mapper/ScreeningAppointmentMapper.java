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

    int insert(ScreeningAppointment appointment);

    ScreeningAppointment selectLatestAppointment(
            @Param("userId") Long userId,
            @Param("screeningTypeId") Long screeningTypeId
    );

    ScreeningAppointment selectActiveAppointment(
            @Param("userId") Long userId,
            @Param("screeningTypeId") Long screeningTypeId,
            @Param("today") LocalDate today
    );

    int cancelAppointment(@Param("appointmentId") Long appointmentId);

    int completeAppointment(@Param("appointmentId") Long appointmentId);
}