package com.basic.backend.service;

import com.basic.backend.dto.InitRegistrationRequest;
import com.basic.backend.entity.HealthProfile;
import com.basic.backend.entity.Medication;
import com.basic.backend.entity.UserScreening;
import com.basic.backend.exception.BizException;
import com.basic.backend.mapper.HealthProfileMapper;
import com.basic.backend.mapper.MedicationMapper;
import com.basic.backend.mapper.ScreeningTypeMapper;
import com.basic.backend.mapper.UserScreeningMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor

public class RegistrationService {

    private final HealthProfileMapper healthProfileMapper;
    private final UserScreeningMapper userScreeningMapper;
    private final ScreeningTypeMapper screeningTypeMapper;
    private final MedicationMapper medicationMapper;
    public void initProfile(Long userId, InitRegistrationRequest req) {

        // 0) 基本空指针保护（可选，但建议）
        if (req == null || req.getProfile() == null) {
            throw new BizException("profile is required");
        }

        // 1) profile（幂等：已有就跳过）
        if (healthProfileMapper.selectByUserId(userId) == null) {
            HealthProfile hp = new HealthProfile();
            hp.setUserId(userId);
            hp.setAge(req.getProfile().getAge());
            hp.setGender(req.getProfile().getGender());
            hp.setDiabetesType(req.getProfile().getDiabetesType());
            hp.setDiagnosisTimeframe(req.getProfile().getDiagnosisTimeframe());
            healthProfileMapper.insert(hp);
        }

        // 2) screenings（幂等：已有就跳过；没有才做校验+插入）
        if (userScreeningMapper.countByUserId(userId) == 0) {

            // 2.1) 基础校验：必须 3 条且 typeId 不重复
            if (req.getScreenings() == null || req.getScreenings().size() != 3) {
                throw new BizException("screenings must contain exactly 3 items");
            }
            Set<Integer> seen = new HashSet<>();
            for (var s : req.getScreenings()) {
                if (s == null || s.getScreeningTypeId() == null) {
                    throw new BizException("screeningTypeId is required");
                }
                if (!seen.add(s.getScreeningTypeId())) {
                    throw new BizException("duplicate screeningTypeId");
                }
            }

            // 2.2) 互斥校验（A1C/EYE/KIDNEY）
            validateMutualExclusion(req);

            // 2.3) 插入 3 条 user_screenings
            for (var s : req.getScreenings()) {

                LocalDate last = parseOrToday(s.getLastCompletedDate());

                Integer intervalDays = screeningTypeMapper.selectIntervalDaysById(s.getScreeningTypeId());
                if (intervalDays == null) {
                    throw new BizException("Invalid screeningTypeId: " + s.getScreeningTypeId());
                }

                LocalDate next = last.plusDays(intervalDays.longValue());

                UserScreening us = new UserScreening();
                us.setUserId(userId);
                us.setScreeningTypeId(s.getScreeningTypeId());
                us.setLastCompletedDate(last);
                us.setNextDueDate(next);

                userScreeningMapper.insert(us);
            }
        }

        // 3) medications（允许为空；幂等：表里已有就跳过；没填就跳过）
        if (req.getMedications() != null && !req.getMedications().isEmpty()) {

            if (medicationMapper.countByUserId(userId) == 0) {
                for (var m : req.getMedications()) {
                    if (m == null) continue;
                    if (m.getName() == null || m.getName().isBlank()) {
                        continue; // 你也可以改成 throw BizException 更严格
                    }

                    Medication med = new Medication();
                    med.setUserId(userId);
                    med.setName(m.getName());
                    med.setDosage(m.getDosage());
                    med.setExpectedDosesPerDay(m.getExpectedDosesPerDay());
                    med.setIsActive(true); // 默认 true
                    medicationMapper.insert(med);
                }
            }
        }
    }

    private void validateMutualExclusion(InitRegistrationRequest req) {
        // 你的 screening_types id 固定：
        // A1C: 1,2; EYE: 3,4; KIDNEY: 5
        boolean hasA1C = false;
        boolean hasEYE = false;
        boolean hasKIDNEY = false;

        for (var s : req.getScreenings()) {
            int id = s.getScreeningTypeId();
            if (id == 1 || id == 2) hasA1C = true;
            else if (id == 3 || id == 4) hasEYE = true;
            else if (id == 5) hasKIDNEY = true;
            else throw new BizException("Unsupported screeningTypeId: " + id);
        }
        if (!(hasA1C && hasEYE && hasKIDNEY)) {
            throw new BizException("screenings must include A1C, EYE, and KIDNEY");
        }
    }

    private LocalDate parseOrToday(String s) {
        if (s == null || s.isBlank()) {
            return LocalDate.now();
        }
        // 约定 yyyy-MM-dd
        return LocalDate.parse(s);
    }
}