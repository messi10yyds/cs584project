package com.basic.backend.service;

import com.basic.backend.dto.*;
import com.basic.backend.entity.ScreeningAppointment;
import com.basic.backend.mapper.ScreeningAppointmentMapper;
import com.basic.backend.mapper.UserScreeningMapper;
import com.basic.backend.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganService {

    private final UserScreeningMapper userScreeningMapper;
    private final ScreeningAppointmentMapper screeningAppointmentMapper;

    private static final LocalDate UNKNOWN_LAST_COMPLETED_DATE = LocalDate.of(2000, 1, 1);
    public OrganDetailDTO getOrganDetail(String organ) {
        if ("eye".equalsIgnoreCase(organ)) {
            return buildEyeDetail();
        }
        if ("kidney".equalsIgnoreCase(organ)) {
            return buildKidneyDetail();
        }
        if ("heart".equalsIgnoreCase(organ)) {
            return buildHeartDetail();
        }

        throw new RuntimeException("Unsupported organ: " + organ);
    }

    private OrganDetailDTO buildEyeDetail() {
        OrganDetailDTO dto = new OrganDetailDTO();
        dto.setOrgan("eye");
        dto.setEducation(buildEyeEducation());

        Long userId = SecurityUtil.getUserId();

        DashboardScreeningQueryDTO item =
                userScreeningMapper.selectDashboardScreeningByUserIdAndScreeningTypeId(userId, 3L);

        if (item == null) {
            item = userScreeningMapper.selectDashboardScreeningByUserIdAndScreeningTypeId(userId, 4L);
        }

        if (item != null) {
            LocalDate dueDate = calculateDueDate(
                    item.getLastCompletedDate(),
                    item.getRecommendedIntervalDays()
            );

            LocalDate displayDueDate = shouldHideDueDate(item.getLastCompletedDate()) ? null : dueDate;
            String status = resolveScreeningStatus(item.getLastCompletedDate(), dueDate);

            ScreeningAppointment appointment = screeningAppointmentMapper.selectNearestScheduledAppointment(
                    userId,
                    item.getScreeningTypeId(),
                    LocalDate.now()
            );

            LocalDate appointmentDate = appointment == null ? null : appointment.getScheduledFor();

            DashboardScreeningDTO screeningDTO = new DashboardScreeningDTO(
                    item.getUserScreeningId(),
                    item.getScreeningType(),
                    toDisplayName(item.getScreeningType()),
                    status,
                    displayDueDate,
                    appointmentDate
            );

            dto.setScreening(screeningDTO);
        }

        return dto;
    }

    private OrganDetailDTO buildKidneyDetail() {
        OrganDetailDTO dto = new OrganDetailDTO();
        dto.setOrgan("kidney");
        dto.setEducation(buildKidneyEducation());

        Long userId = SecurityUtil.getUserId();

        DashboardScreeningQueryDTO item =
                userScreeningMapper.selectDashboardScreeningByUserIdAndScreeningTypeId(userId, 5L);

        if (item != null) {
            LocalDate dueDate = calculateDueDate(
                    item.getLastCompletedDate(),
                    item.getRecommendedIntervalDays()
            );

            LocalDate displayDueDate = shouldHideDueDate(item.getLastCompletedDate()) ? null : dueDate;
            String status = resolveScreeningStatus(item.getLastCompletedDate(), dueDate);

            ScreeningAppointment appointment = screeningAppointmentMapper.selectNearestScheduledAppointment(
                    userId,
                    item.getScreeningTypeId(),
                    LocalDate.now()
            );

            LocalDate appointmentDate = appointment == null ? null : appointment.getScheduledFor();

            DashboardScreeningDTO screeningDTO = new DashboardScreeningDTO(
                    item.getUserScreeningId(),
                    item.getScreeningType(),
                    toDisplayName(item.getScreeningType()),
                    status,
                    displayDueDate,
                    appointmentDate
            );

            dto.setScreening(screeningDTO);
        }

        return dto;
    }

    private OrganDetailDTO buildHeartDetail() {
        OrganDetailDTO dto = new OrganDetailDTO();
        dto.setOrgan("heart");
        dto.setEducation(buildHeartEducation());

        Long userId = SecurityUtil.getUserId();

        DashboardScreeningQueryDTO item =
                userScreeningMapper.selectDashboardScreeningByUserIdAndScreeningTypeId(userId, 1L);

        if (item == null) {
            item = userScreeningMapper.selectDashboardScreeningByUserIdAndScreeningTypeId(userId, 2L);
        }

        if (item != null) {
            LocalDate dueDate = calculateDueDate(
                    item.getLastCompletedDate(),
                    item.getRecommendedIntervalDays()
            );

            LocalDate displayDueDate = shouldHideDueDate(item.getLastCompletedDate()) ? null : dueDate;
            String status = resolveScreeningStatus(item.getLastCompletedDate(), dueDate);

            ScreeningAppointment appointment = screeningAppointmentMapper.selectNearestScheduledAppointment(
                    userId,
                    item.getScreeningTypeId(),
                    LocalDate.now()
            );

            LocalDate appointmentDate = appointment == null ? null : appointment.getScheduledFor();

            DashboardScreeningDTO screeningDTO = new DashboardScreeningDTO(
                    item.getUserScreeningId(),
                    item.getScreeningType(),
                    toDisplayName(item.getScreeningType()),
                    status,
                    displayDueDate,
                    appointmentDate
            );

            dto.setScreening(screeningDTO);
        }

        return dto;
    }

    private OrganEducationDTO buildEyeEducation() {
        OrganEducationDTO dto = new OrganEducationDTO();
        dto.setTitle("Eye Care");
        dto.setWhatIsIt(List.of(new WhatIsItDTO("Retina","Light-sensitive layer\n" +
                "with blood vessels")));
        dto.setRiskFactors(List.of(
                new RiskFactorDTO("\uD83E\uDE78 Poorly controlled blood sugar","High A1C over time is the main cause of vessel damage."),
                new RiskFactorDTO("\uD83D\uDCC5 Longer duration of diabetes","The longer you've had diabetes, the higher the risk."),
                new RiskFactorDTO("\uD83D\uDC8A High blood pressure","Adds pressure on already fragile eye vessels."),
                new RiskFactorDTO("\uD83D\uDEAC Smoking","Speeds up blood vessel damage throughout the body.")
        ));
        dto.setCheckups(List.of(
                "Dilated eye exam",
                "Regular vision screening",
                "Follow your doctor's eye care advice"
        ));
        return dto;
    }

    private OrganEducationDTO buildKidneyEducation() {
        OrganEducationDTO dto = new OrganEducationDTO();
        dto.setTitle("Kidney Care");
        dto.setWhatIsIt(List.of(new WhatIsItDTO("Kidneys","Organs that filter waste and extra fluid from the blood")));
        dto.setRiskFactors(List.of(
                new RiskFactorDTO("\uD83E\uDE78 High blood sugar over time","Persistently high glucose levels damage the kidney’s filtering units."),
                new RiskFactorDTO("\uD83E\uDE7A High blood pressure","Elevated blood pressure increases strain on the kidneys and speeds up damage."),
                new RiskFactorDTO("\uD83D\uDCC5 Long-term diabetes","The longer someone lives with diabetes, the higher the risk of kidney disease."),
                new RiskFactorDTO("\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67 Family history of kidney problems","Genetics can increase the likelihood of developing kidney disease.")
        ));
        dto.setCheckups(List.of(
                "A urine test checks whether protein is leaking into your urine",
                "A blood test measures how well your kidneys filter waste from your blood",
                "Your doctor may review the results and recommend follow-up care if needed"
        ));
        return dto;
    }

    private OrganEducationDTO buildHeartEducation() {
        OrganEducationDTO dto = new OrganEducationDTO();
        dto.setTitle("Heart Care");
        dto.setWhatIsIt(List.of(new WhatIsItDTO("Heart Health","Keeps blood circulating and supplies oxygen to your body")));
        dto.setRiskFactors(List.of(
                new RiskFactorDTO("\uD83E\uDE78 High blood sugar","High glucose levels can damage blood vessels and increase cardiovascular risk."),
                new RiskFactorDTO("\uD83E\uDE7A High blood pressure","High blood pressure forces the heart to work harder and damages arteries."),
                new RiskFactorDTO("\uD83E\uDD53 High cholesterol","Excess cholesterol can build up in blood vessels and restrict blood flow."),
                new RiskFactorDTO("\uD83D\uDEAC Smoking","Smoking damages blood vessels and significantly increases heart disease risk.")
        ));
        dto.setCheckups(List.of(
                "Your blood pressure will be measured to monitor your heart health",
                "A blood test may be done to check cholesterol levels",
                "Your doctor evaluates overall heart health and risk factors"
        ));
        return dto;
    }

    private boolean shouldHideDueDate(LocalDate lastCompletedDate) {
        return UNKNOWN_LAST_COMPLETED_DATE.equals(lastCompletedDate);
    }

    private LocalDate calculateDueDate(LocalDate lastCompletedDate, Integer recommendedIntervalDays) {
        if (lastCompletedDate == null || recommendedIntervalDays == null) {
            return null;
        }
        return lastCompletedDate.plusDays(recommendedIntervalDays);
    }

    private String resolveScreeningStatus(LocalDate lastCompletedDate, LocalDate dueDate) {
        if (UNKNOWN_LAST_COMPLETED_DATE.equals(lastCompletedDate)) {
            return "OVERDUE";
        }

        if (dueDate == null) {
            return "ON_TRACK";
        }

        LocalDate today = LocalDate.now();
        if (dueDate.isBefore(today)) {
            return "OVERDUE";
        }
        if (!dueDate.isAfter(today.plusDays(7))) {
            return "DUE_SOON";
        }
        return "ON_TRACK";
    }

    private String toDisplayName(String screeningType) {
        if (screeningType == null) {
            return "Screening";
        }

        return switch (screeningType) {
            case "A1C_6M", "A1C_3M" -> "A1C Test";
            case "EYE_12M", "EYE_6M" -> "Eye Exam";
            case "KIDNEY_12M" -> "Kidney Test";
            default -> screeningType;
        };
    }
}