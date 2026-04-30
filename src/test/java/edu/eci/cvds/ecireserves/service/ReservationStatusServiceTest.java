package edu.eci.cvds.ecireserves.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.eci.cvds.ecireserves.enums.ReservationStatus;
import edu.eci.cvds.ecireserves.exception.EciReservesException;
import edu.eci.cvds.ecireserves.model.Laboratory;
import edu.eci.cvds.ecireserves.model.Reservation;
import edu.eci.cvds.ecireserves.model.TimeSlot;
import edu.eci.cvds.ecireserves.repository.LaboratoryRepository;
import edu.eci.cvds.ecireserves.repository.ReservationRepository;

@ExtendWith(MockitoExtension.class)
class ReservationStatusServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private LaboratoryRepository laboratoryRepository;

    @InjectMocks
    private ReservationStatusService reservationStatusService;

    private Reservation expiredReservation;
    private Laboratory laboratory;

    @BeforeEach
    void setUp() {
        expiredReservation = new Reservation("1", "user123", "Lab-101", LocalDate.now(), LocalTime.of(10, 0), 2, LocalTime.of(12, 0), "Study Session", ReservationStatus.AGENDADA);

        Map<LocalDate, List<TimeSlot>> timeSlotsByDate = new HashMap<>();
        timeSlotsByDate.put(LocalDate.now(), new ArrayList<>(List.of(new TimeSlot(LocalTime.of(10, 0), LocalTime.of(12, 0)))));

        laboratory = new Laboratory("Lab-101", "A101", "Computer Lab", 30, "Programming Lab", LocalTime.of(8, 0), LocalTime.of(18, 0), null, null, timeSlotsByDate);
    }

    @Test
    void shouldUpdateExpiredReservationsAndFreeLaboratory() throws EciReservesException {
        when(reservationRepository.findByEndTimeBeforeAndStatus(any(LocalTime.class), eq(ReservationStatus.AGENDADA)))
                .thenReturn(List.of(expiredReservation));
        when(laboratoryRepository.findById("Lab-101")).thenReturn(Optional.of(laboratory));

        reservationStatusService.cleanupReservations();

        verify(reservationRepository).save(expiredReservation);
        verify(laboratoryRepository).save(laboratory);
    }

    @Test
    void shouldNotChangeAnythingIfNoExpiredReservations() throws EciReservesException {
        when(reservationRepository.findByEndTimeBeforeAndStatus(any(LocalTime.class), eq(ReservationStatus.AGENDADA)))
                .thenReturn(List.of());

        reservationStatusService.cleanupReservations();

        verify(reservationRepository, never()).save(any());
        verify(laboratoryRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionIfLaboratoryNotFound() {
        when(reservationRepository.findByEndTimeBeforeAndStatus(any(LocalTime.class), eq(ReservationStatus.AGENDADA)))
                .thenReturn(List.of(expiredReservation));
        when(laboratoryRepository.findById("Lab-101")).thenReturn(Optional.empty());

        EciReservesException exception = assertThrows(EciReservesException.class, () -> reservationStatusService.cleanupReservations());
        assertEquals(EciReservesException.LABORATORY_NOT_FOUND, exception.getMessage());

    }
}
