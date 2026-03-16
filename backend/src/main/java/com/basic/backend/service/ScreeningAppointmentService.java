package com.basic.backend.service;

import com.basic.backend.dto.ScreeningAppointmentQueryDTO;
import com.basic.backend.dto.ScreeningAppointmentRequestDTO;

public interface ScreeningAppointmentService {

    ScreeningAppointmentQueryDTO getAppointmentPage(Long userId, Long screeningTypeId);

    void bookOrReschedule(Long userId, Long screeningTypeId, ScreeningAppointmentRequestDTO request);

    void completeAppointment(Long userId, Long screeningTypeId);
}