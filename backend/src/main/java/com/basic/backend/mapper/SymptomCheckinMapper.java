package com.basic.backend.mapper;

import com.basic.backend.entity.SymptomCheckin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface SymptomCheckinMapper {

    List<SymptomCheckin> selectByUserIdAndCheckinDate(@Param("userId") Long userId,
                                                      @Param("checkinDate") LocalDate checkinDate);

    LocalDate selectLatestCheckinDateByUserId(@Param("userId") Long userId);

    int insert(SymptomCheckin symptomCheckin);

    int updateAnswerByUserIdAndQuestionIdAndCheckinDate(@Param("userId") Long userId,
                                                        @Param("questionId") Long questionId,
                                                        @Param("checkinDate") LocalDate checkinDate,
                                                        @Param("answer") Boolean answer);
}