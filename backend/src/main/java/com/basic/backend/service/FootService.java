package com.basic.backend.service;

import com.basic.backend.dto.*;
import com.basic.backend.entity.SymptomCheckin;
import com.basic.backend.entity.SymptomQuestion;
import com.basic.backend.mapper.SymptomCheckinMapper;
import com.basic.backend.mapper.SymptomQuestionMapper;
import com.basic.backend.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class FootService {

    private final SymptomQuestionMapper symptomQuestionMapper;
    private final SymptomCheckinMapper symptomCheckinMapper;

    public FootDetailDTO getFootDetail() {
        Long userId = SecurityUtil.getUserId();
        LocalDate today = LocalDate.now();

        List<SymptomQuestion> questions = symptomQuestionMapper.selectAll();
        List<SymptomCheckin> todayCheckins =
                symptomCheckinMapper.selectByUserIdAndCheckinDate(userId, today);

        LocalDate latestCheckinDate =
                symptomCheckinMapper.selectLatestCheckinDateByUserId(userId);

        List<SymptomCheckin> latestCheckins = new ArrayList<>();
        if (latestCheckinDate != null) {
            latestCheckins = symptomCheckinMapper.selectByUserIdAndCheckinDate(userId, latestCheckinDate);
        }

        FootDetailDTO dto = new FootDetailDTO();
        dto.setOrgan("foot");
        dto.setEducation(buildFootEducation());
        dto.setQuestions(toQuestionDTOList(questions));
        dto.setTodayCompleted(!todayCheckins.isEmpty());
        dto.setTodayAnswers(toAnswerDTOList(todayCheckins));
        dto.setLatestResult(buildResult(latestCheckinDate, latestCheckins));

        return dto;
    }

    public FootCheckResultDTO submitFootCheck(FootCheckSubmitDTO request) {
        Long userId = SecurityUtil.getUserId();
        LocalDate today = LocalDate.now();

        if (request == null || request.getAnswers() == null || request.getAnswers().isEmpty()) {
            throw new RuntimeException("Answers cannot be empty");
        }

        for (FootAnswerDTO answerDTO : request.getAnswers()) {
            if (answerDTO.getQuestionId() == null || answerDTO.getAnswer() == null) {
                throw new RuntimeException("Invalid foot check answer");
            }

            int updated = symptomCheckinMapper.updateAnswerByUserIdAndQuestionIdAndCheckinDate(
                    userId,
                    answerDTO.getQuestionId(),
                    today,
                    answerDTO.getAnswer()
            );

            if (updated == 0) {
                SymptomCheckin symptomCheckin = new SymptomCheckin();
                symptomCheckin.setUserId(userId);
                symptomCheckin.setQuestionId(answerDTO.getQuestionId());
                symptomCheckin.setAnswer(answerDTO.getAnswer());
                symptomCheckin.setCheckinDate(today);

                symptomCheckinMapper.insert(symptomCheckin);
            }
        }

        List<SymptomCheckin> latestCheckins =
                symptomCheckinMapper.selectByUserIdAndCheckinDate(userId, today);

        return buildResult(today, latestCheckins);
    }

    private FootEducationDTO buildFootEducation() {
        return new FootEducationDTO(
                "Daily Foot Check",
                "Check your feet every day for warning signs such as redness, swelling, cuts, blisters, pain, or numbness.",
                "If you notice any new or worsening foot problem, contact a healthcare professional as soon as possible."
        );
    }

    private List<FootQuestionDTO> toQuestionDTOList(List<SymptomQuestion> questions) {
        List<FootQuestionDTO> list = new ArrayList<>();
        for (SymptomQuestion question : questions) {
            list.add(new FootQuestionDTO(
                    question.getId(),
                    question.getQuestion()
            ));
        }
        return list;
    }

    private List<FootAnswerDTO> toAnswerDTOList(List<SymptomCheckin> checkins) {
        List<FootAnswerDTO> list = new ArrayList<>();
        for (SymptomCheckin checkin : checkins) {
            list.add(new FootAnswerDTO(
                    checkin.getQuestionId(),
                    checkin.getAnswer()
            ));
        }
        return list;
    }

    private FootCheckResultDTO buildResult(LocalDate checkinDate, List<SymptomCheckin> checkins) {
        if (checkinDate == null || checkins == null || checkins.isEmpty()) {
            return null;
        }

        boolean hasRisk = false;
        for (SymptomCheckin checkin : checkins) {
            if (Boolean.TRUE.equals(checkin.getAnswer())) {
                hasRisk = true;
                break;
            }
        }

        if (hasRisk) {
            return new FootCheckResultDTO(
                    checkinDate,
                    "URGENT",
                    "Possible foot symptoms detected",
                    buildFootSymptomMessage(checkins),
                    "16000"
            );
        }

        return new FootCheckResultDTO(
                checkinDate,
                "NORMAL",
                "No symptoms detected",
                buildFootSymptomMessage(checkins),
                "16000"
        );
    }

    private String resolveFootSymptom(Long questionId) {
        return switch (questionId.intValue()) {
            case 1 -> "skin sores or blisters";
            case 2 -> "numbness or tingling";
            case 3 -> "color changes or swelling";
            case 4 -> "slow-healing wounds";
            case 5 -> "pain or burning sensation";
            default -> "foot symptoms";
        };
    }

    private String buildFootSymptomMessage(List<SymptomCheckin> checkins) {
        List<String> symptoms = new ArrayList<>();

        for (SymptomCheckin checkin : checkins) {
            if (Boolean.TRUE.equals(checkin.getAnswer())) {
                String symptom = resolveFootSymptom(checkin.getQuestionId());
                if (!symptoms.contains(symptom)) {
                    symptoms.add(symptom);
                }
            }
        }

        if (symptoms.isEmpty()) {
            return "No foot symptoms were reported in your latest check. Continue checking your feet daily.";
        }

        if (symptoms.size() == 1) {
            return "Possible foot symptom reported: " + symptoms.get(0)
                    + ". Please contact your healthcare provider.";
        }

        return "Possible foot symptoms reported: " + String.join(", ", symptoms)
                + ". Please contact your healthcare provider.";
    }
}