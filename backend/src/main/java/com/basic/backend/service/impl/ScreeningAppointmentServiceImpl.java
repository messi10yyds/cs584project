package com.basic.backend.service.impl;

import com.basic.backend.dto.DashboardScreeningQueryDTO;
import com.basic.backend.dto.ScreeningAppointmentQueryDTO;
import com.basic.backend.dto.ScreeningAppointmentRequestDTO;
import com.basic.backend.entity.ScreeningAppointment;
import com.basic.backend.entity.ScreeningType;
import com.basic.backend.entity.UserScreening;
import com.basic.backend.mapper.ScreeningAppointmentMapper;
import com.basic.backend.mapper.ScreeningTypeMapper;
import com.basic.backend.mapper.UserScreeningMapper;
import com.basic.backend.service.ScreeningAppointmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class ScreeningAppointmentServiceImpl implements ScreeningAppointmentService {

    private final ScreeningTypeMapper screeningTypeMapper;
    private final UserScreeningMapper userScreeningMapper;
    private final ScreeningAppointmentMapper screeningAppointmentMapper;

    public ScreeningAppointmentServiceImpl(ScreeningTypeMapper screeningTypeMapper,
                                           UserScreeningMapper userScreeningMapper,
                                           ScreeningAppointmentMapper screeningAppointmentMapper) {
        this.screeningTypeMapper = screeningTypeMapper;
        this.userScreeningMapper = userScreeningMapper;
        this.screeningAppointmentMapper = screeningAppointmentMapper;
    }

    @Override
    public ScreeningAppointmentQueryDTO getAppointmentPage(Long userId, Long screeningTypeId) {
        ScreeningAppointmentQueryDTO dto = new ScreeningAppointmentQueryDTO();
        dto.setScreeningTypeId(screeningTypeId);
        dto.setAppointmentBooked(false);

        // 1. 查 screening type
        ScreeningType screeningType = screeningTypeMapper.selectById(screeningTypeId);
        if (screeningType != null) {
            dto.setScreeningName(screeningType.getName());
        }

        // 2. 查 user screening（复用你 dashboard 现有查询）
        UserScreening userScreening =
                userScreeningMapper.selectByUserIdAndScreeningTypeId(userId, screeningTypeId);

        if (userScreening != null) {
            dto.setLastCompletedDate(userScreening.getLastCompletedDate());
            dto.setNextDueDate(userScreening.getNextDueDate());
        }

        // 3. 查当前有效预约
        ScreeningAppointment activeAppointment =
                screeningAppointmentMapper.selectActiveAppointment(userId, screeningTypeId, LocalDate.now());

        if (activeAppointment != null) {
            dto.setAppointmentBooked(true);
            dto.setAppointmentId(activeAppointment.getId());
            dto.setScheduledFor(activeAppointment.getScheduledFor());
            dto.setAppointmentStatus(activeAppointment.getStatus());
        }

        return dto;
    }

    @Override
    @Transactional
    public void bookOrReschedule(Long userId, Long screeningTypeId, ScreeningAppointmentRequestDTO request) {
        if (request == null || request.getScheduledFor() == null) {
            throw new IllegalArgumentException("Scheduled date is required.");
        }

        LocalDate scheduledFor = request.getScheduledFor();
        if (scheduledFor.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Scheduled date cannot be in the past.");
        }

        // 1. 查当前有效预约
        ScreeningAppointment activeAppointment =
                screeningAppointmentMapper.selectActiveAppointment(userId, screeningTypeId, LocalDate.now());

        // 2. 如果有旧预约，先取消
        if (activeAppointment != null) {
            screeningAppointmentMapper.cancelAppointment(activeAppointment.getId());
        }

        // 3. 插入新预约
        ScreeningAppointment newAppointment = new ScreeningAppointment();
        newAppointment.setUserId(userId);
        newAppointment.setScreeningTypeId(screeningTypeId);
        newAppointment.setScheduledFor(scheduledFor);
        newAppointment.setStatus("SCHEDULED");
        newAppointment.setCreatedAt(LocalDateTime.now());

        screeningAppointmentMapper.insert(newAppointment);
    }

    @Override
    @Transactional
    public void completeAppointment(Long userId, Long screeningTypeId) {
        // 1. 查当前有效预约
        ScreeningAppointment activeAppointment =
                screeningAppointmentMapper.selectActiveAppointment(userId, screeningTypeId, LocalDate.now());

        if (activeAppointment == null) {
            throw new IllegalArgumentException("No active appointment found.");
        }

        // 2. 将预约置为 COMPLETED
        screeningAppointmentMapper.completeAppointment(activeAppointment.getId());

        // 3. 查 screening type，拿推荐间隔
        ScreeningType screeningType = screeningTypeMapper.selectById(screeningTypeId);
        if (screeningType == null) {
            throw new IllegalArgumentException("Screening type not found.");
        }

        Integer intervalDays = screeningType.getRecommendedIntervalDays();
        if (intervalDays == null) {
            throw new IllegalArgumentException("Recommended interval days not configured.");
        }

        // 4. 计算完成日期和下一次到期日
        //用户点完成时，认为这次 screening 的完成日期就是 appointment 的 scheduledFor
        LocalDate completedDate = activeAppointment.getScheduledFor();
        LocalDate nextDueDate = completedDate.plusDays(intervalDays);

        // 5. 更新 user_screenings
        int updated = userScreeningMapper.updateCompletionByUserIdAndScreeningTypeId(
                userId,
                screeningTypeId,
                completedDate,
                nextDueDate
        );

        if (updated == 0) {
            throw new IllegalArgumentException("User screening record not found.");
        }
    }
}