package com.basic.backend.mapper;

import com.basic.backend.entity.SymptomQuestion;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SymptomQuestionMapper {

    List<SymptomQuestion> selectAll();
}