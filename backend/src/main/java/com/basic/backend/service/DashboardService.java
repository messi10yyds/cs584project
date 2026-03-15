package com.basic.backend.service;

import com.basic.backend.dto.DashboardGreetingDTO;
import com.basic.backend.dto.DashboardMedicationDTO;
import com.basic.backend.dto.DashboardOrganDTO;
import com.basic.backend.dto.DashboardOverviewDTO;
import com.basic.backend.dto.DashboardRiskAlertDTO;
import com.basic.backend.dto.DashboardScreeningDTO;
import com.basic.backend.entity.*;
import com.basic.backend.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserMapper userMapper;
    private final HealthProfileMapper healthProfileMapper;
    private final UserScreeningMapper userScreeningMapper;
    private final MedicationMapper medicationMapper;
    private final MedicationDailyLogMapper medicationDailyLogMapper;
    private final ScreeningAppointmentMapper screeningAppointmentMapper;

    private static final LocalDate UNKNOWN_LAST_COMPLETED_DATE = LocalDate.of(2000, 1, 1);
//    public DashboardService(UserMapper userMapper, HealthProfileMapper healthProfileMapper, UserScreeningMapper userScreeningMapper) {
//        this.userMapper = userMapper;
//        this.healthProfileMapper = healthProfileMapper;
//        this.userScreeningMapper = userScreeningMapper;
//    }

    public DashboardOverviewDTO getOverview(Long userId) {
        String displayName = buildDisplayName(userId);

        DashboardGreetingDTO greeting = new DashboardGreetingDTO(
                buildGreetingText(),
                displayName,
                LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH))
        );

        List<DashboardOrganDTO> organs = Arrays.asList(
                new DashboardOrganDTO("eye", "Eye"),
                new DashboardOrganDTO("foot", "Foot"),
                new DashboardOrganDTO("kidney", "Kidney"),
                new DashboardOrganDTO("heart", "Heart")
        );

        List<DashboardMedicationDTO> todayMedications = buildTodayMedications(userId);

        List<DashboardScreeningDTO> screenings = buildScreenings(userId);
        List<DashboardRiskAlertDTO> riskAlerts = buildRiskAlerts(screenings,userId);
        return new DashboardOverviewDTO(
                greeting,
                organs,
                riskAlerts,
                todayMedications,
                screenings
        );
    }

    private String buildDisplayName(Long userId) {
        HealthProfile healthProfile = healthProfileMapper.selectByUserId(userId);
        if (healthProfile != null && StringUtils.hasText(healthProfile.getName())) {
            return healthProfile.getName();
        }

        User user = userMapper.selectById(userId);
        if (user != null && StringUtils.hasText(user.getUsername())) {
            return user.getUsername();
        }

        return "User";
    }

    private String buildGreetingText() {
        int hour = LocalTime.now().getHour();
        if (hour < 12) {
            return "Good morning 👋";
        }
        if (hour < 18) {
            return "Good afternoon 👋";
        }
        return "Good evening 👋";
    }

    private boolean shouldHideDueDate(LocalDate lastCompletedDate) {
        return UNKNOWN_LAST_COMPLETED_DATE.equals(lastCompletedDate);
    }

    private List<DashboardScreeningDTO> buildScreenings(Long userId) {
        return userScreeningMapper.selectDashboardScreeningsByUserId(userId)
                .stream()
                .map(item -> {
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

                    return new DashboardScreeningDTO(
                            item.getUserScreeningId(),
                            item.getScreeningType(),
                            toDisplayName(item.getScreeningType()),
                            status,
                            displayDueDate,
                            appointmentDate
                    );
                })
                .toList();
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

    private List<DashboardMedicationDTO> buildTodayMedications(Long userId) {

        return medicationMapper.selectDashboardMedications(userId)
                .stream()
                .map(item -> new DashboardMedicationDTO(
                        item.getMedicationId(),
                        item.getName(),
                        item.getDosage(),
                        item.getExpectedDosesPerDay(),
                        item.getTakenToday(),
                        resolveMedicationStatus(item.getTakenToday(), item.getExpectedDosesPerDay())
                ))
                .toList();
    }

    private List<DashboardRiskAlertDTO> buildRiskAlerts(List<DashboardScreeningDTO> screenings, Long userId) {
        List<DashboardRiskAlertDTO> alerts = new java.util.ArrayList<>();

        DashboardRiskAlertDTO screeningAlert = buildScreeningRiskAlert(screenings);
        if (screeningAlert != null) {
            alerts.add(screeningAlert);
        }

        DashboardRiskAlertDTO footAlert = buildFootRiskAlert(userId);
        if (footAlert != null) {
            alerts.add(footAlert);
        } else {
            alerts.add(buildDefaultFootAlert());
        }

        DashboardRiskAlertDTO medicationAlert = buildMedicationRiskAlert(userId);
        if (medicationAlert != null) {
            alerts.add(medicationAlert);
        }
        return alerts;
    }

    private DashboardRiskAlertDTO buildScreeningRiskAlert(List<DashboardScreeningDTO> screenings) {
        if (screenings == null || screenings.isEmpty()) {
            return null;
        }

        DashboardScreeningDTO overdue = screenings.stream()
                .filter(item -> "OVERDUE".equals(item.getStatus()))
                .filter(item -> isRiskAlertSupportedScreening(item.getScreeningType()))
                .findFirst()
                .orElse(null);

        if (overdue != null) {
            String organKey = toOrganKey(overdue.getScreeningType());
            String displayName = overdue.getDisplayName();

            return new DashboardRiskAlertDTO(
                    "SCREENING_OVERDUE",
                    "HIGH",
                    displayName + " overdue",
                    "Your " + displayName + " is overdue.",
                    organKey,
                    "VIEW"
            );
        }

        DashboardScreeningDTO dueSoon = screenings.stream()
                .filter(item -> "DUE_SOON".equals(item.getStatus()))
                .filter(item -> isRiskAlertSupportedScreening(item.getScreeningType()))
                .findFirst()
                .orElse(null);

        if (dueSoon != null) {
            String organKey = toOrganKey(dueSoon.getScreeningType());
            String displayName = dueSoon.getDisplayName();

            return new DashboardRiskAlertDTO(
                    "SCREENING_DUE_SOON",
                    "MEDIUM",
                    displayName + " due soon",
                    "Your " + displayName + " is coming due within 7 days.",
                    organKey,
                    "VIEW"
            );
        }

        return null;
    }

    private DashboardRiskAlertDTO buildDefaultFootAlert() {
        return new DashboardRiskAlertDTO(
                "FOOT_HEALTH_TIP",
                "LOW",
                "Foot check reminder",
                "Check your feet regularly for any unusual symptoms.",
                "foot",
                "VIEW"
        );
    }

    private String toOrganKey(String screeningType) {
        if (screeningType == null) {
            return "screening";
        }

        return switch (screeningType) {
            case "A1C_6M", "A1C_3M" -> "general";
            case "EYE_12M", "EYE_6M" -> "eye";
            case "KIDNEY_12M" -> "kidney";
            default -> "screening";
        };
    }

    private boolean isRiskAlertSupportedScreening(String screeningType) {
        String organKey = toOrganKey(screeningType);
        return "eye".equals(organKey) || "kidney".equals(organKey);
    }

    private DashboardRiskAlertDTO buildFootRiskAlert(Long userId) {
        return null;
    }

    private DashboardRiskAlertDTO buildMedicationRiskAlert(Long userId) {
        return null;
    }

    public DashboardMedicationDTO logMedicationDose(Long userId, Long medicationId) {
        Medication medication = medicationMapper.selectActiveByIdAndUserId(medicationId, userId);
        if (medication == null) {
            throw new RuntimeException("Medication not found");
        }

        LocalDate today = LocalDate.now();
        MedicationDailyLog todayLog = medicationDailyLogMapper
                .selectByUserIdAndMedicationIdAndLogDate(userId, medicationId, today);

        int expected = medication.getExpectedDosesPerDay() == null ? 0 : medication.getExpectedDosesPerDay();
        int takenToday;

        if (todayLog == null) {
            MedicationDailyLog newLog = new MedicationDailyLog();
            newLog.setMedicationId(medicationId);
            newLog.setUserId(userId);
            newLog.setLogDate(today);
            newLog.setDosesTaken(expected > 0 ? 1 : 0);
            newLog.setCreatedAt(java.time.LocalDateTime.now());
            medicationDailyLogMapper.insert(newLog);

            takenToday = newLog.getDosesTaken();
        } else {
            int current = todayLog.getDosesTaken() == null ? 0 : todayLog.getDosesTaken();
            int next = current + 1;

            if (expected > 0 && next > expected) {
                next = expected;
            }

            medicationDailyLogMapper.updateDosesTakenById(todayLog.getId(), next);
            takenToday = next;
        }

        return new DashboardMedicationDTO(
                medication.getId(),
                medication.getName(),
                medication.getDosage(),
                medication.getExpectedDosesPerDay(),
                takenToday,
                resolveMedicationStatus(takenToday, expected)
        );
    }

    private String resolveMedicationStatus(Integer takenToday, Integer expectedDosesPerDay) {
        int taken = takenToday == null ? 0 : takenToday;
        int expected = expectedDosesPerDay == null ? 0 : expectedDosesPerDay;

        if (taken == 0) {
            return "NOT_STARTED";
        }
        if (expected > 0 && taken < expected) {
            return "PARTIAL";
        }
        return "COMPLETED";
    }

}