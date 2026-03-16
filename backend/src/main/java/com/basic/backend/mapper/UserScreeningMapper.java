package com.basic.backend.mapper;

import com.basic.backend.dto.DashboardScreeningQueryDTO;
import com.basic.backend.entity.UserScreening;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface UserScreeningMapper {
    int insert(UserScreening us);

    int countByUserId(@Param("userId") Long userId);

    List<UserScreening> selectByUserId(@Param("userId") Long userId);

    List<DashboardScreeningQueryDTO> selectDashboardScreeningsByUserId(@Param("userId") Long userId);

    DashboardScreeningQueryDTO selectDashboardScreeningByUserIdAndScreeningTypeId(@Param("userId") Long userId,
                                                                                  @Param("screeningTypeId") Long screeningTypeId);

    UserScreening selectByUserIdAndScreeningTypeId(@Param("userId") Long userId,
                                                   @Param("screeningTypeId") Long screeningTypeId);

    int updateCompletionByUserIdAndScreeningTypeId(@Param("userId") Long userId,
                                                   @Param("screeningTypeId") Long screeningTypeId,
                                                   @Param("lastCompletedDate") LocalDate lastCompletedDate,
                                                   @Param("nextDueDate") LocalDate nextDueDate);
}