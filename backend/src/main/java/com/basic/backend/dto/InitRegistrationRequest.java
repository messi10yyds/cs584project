package com.basic.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class InitRegistrationRequest {

    @NotNull
    @Valid
    private HealthProfileDTO profile;

    /**
     * 固定 3 条：
     * - 1条 A1C (typeId=1 or 2)
     * - 1条 EYE (typeId=3 or 4)
     * - 1条 KIDNEY (typeId=5)
     */
    @NotNull
    @Valid
    private List<ScreeningInitDTO> screenings;

    /**
     * 允许为空或不传
     */
    @Valid
    private List<MedicationDTO> medications;

    @Data
    public static class HealthProfileDTO {
        private Integer age;
        private String gender;
        private String diabetesType;
        private String diagnosisTimeframe;
    }

    @Data
    public static class ScreeningInitDTO {
        @NotNull
        private Integer screeningTypeId;

        /**
         * yyyy-MM-dd，允许 null -> 后端默认 today
         */
        private String lastCompletedDate;
    }

    @Data
    public static class MedicationDTO {
        private String name;
        private String dosage;
        private Integer expectedDosesPerDay;
        // isActive 不用前端传，后端默认 true
    }
}