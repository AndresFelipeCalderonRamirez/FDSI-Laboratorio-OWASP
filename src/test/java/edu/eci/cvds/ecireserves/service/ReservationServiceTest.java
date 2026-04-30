package edu.eci.cvds.ecireserves.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import edu.eci.cvds.ecireserves.dto.ReservationDTO;
import edu.eci.cvds.ecireserves.enums.ReservationStatus;
import edu.eci.cvds.ecireserves.exception.EciReservesException;
import edu.eci.cvds.ecireserves.model.Laboratory;
import edu.eci.cvds.ecireserves.model.Reservation;
import edu.eci.cvds.ecireserves.repository.LaboratoryRepository;
import edu.eci.cvds.ecireserves.repository.ReservationRepository;

class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private LaboratoryRepository laboratoryRepository;

    private Reservation reservation;
    private Laboratory laboratory;
    private ReservationDTO reservationDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        reservation = new Reservation();
        reservation.setId("res123");
        reservation.setUserId("user1");
        reservation.setLaboratoryId("lab1");
        reservation.setDate(LocalDate.now());
        reservation.setStartTime(LocalTime.of(10, 0));
        reservation.setDuration(60);
        reservation.setPurpose("Study session");
        reservation.setStatus(ReservationStatus.AGENDADA);

        laboratory = new Laboratory();
        laboratory.setId("lab1");
        laboratory.setOpeningTime(LocalTime.of(8, 0));
        laboratory.setClosingTime(LocalTime.of(18, 0));
        laboratory.setTimeSlots(new ArrayList<>());

        reservationDTO = new ReservationDTO();
        reservationDTO.setUserId("user1");
        reservationDTO.setLaboratoryId("lab1");
        reservationDTO.setDate(LocalDate.now());
        reservationDTO.setStartTime(LocalTime.of(10, 0));
        reservationDTO.setDuration(60);
        reservationDTO.setPurpose("Study session");
    }

    @Test
    void shouldGetAllReservations() {
        when(reservationRepository.findAll()).thenReturn(List.of(reservation));

        List<Reservation> reservations = reservationService.getAllReservations();

        assertNotNull(reservations);
        assertEquals(1, reservations.size());
        verify(reservationRepository, times(1)).findAll();
    }

    @Test
    void shouldGetReservationById() throws EciReservesException {
        when(reservationRepository.findById("res123")).thenReturn(Optional.of(reservation));

        Reservation found = reservationService.getReservationById("res123");

        assertNotNull(found);
        assertEquals("res123", found.getId());
        verify(reservationRepository, times(1)).findById("res123");
    }

    @Test
    void shouldGetReservationsByUserId() {
        when(reservationRepository.findByUserId("user1")).thenReturn(List.of(reservation));

        List<Reservation> reservations = reservationService.getReservationsByUserId("user1");

        assertNotNull(reservations);
        assertEquals(1, reservations.size());
        verify(reservationRepository, times(1)).findByUserId("user1");
    }

    @Test
    void shouldGetReservationsByLaboratoryId() {
        when(reservationRepository.findByLaboratoryId("lab1")).thenReturn(List.of(reservation));

        List<Reservation> reservations = reservationService.getReservationsByLaboratoryId("lab1");

        assertNotNull(reservations);
        assertEquals(1, reservations.size());
        verify(reservationRepository, times(1)).findByLaboratoryId("lab1");
    }

    @Test
    void shouldGetReservationsByStatus() {
        when(reservationRepository.findByStatus(ReservationStatus.AGENDADA)).thenReturn(List.of(reservation));

        List<Reservation> reservations = reservationService.getReservationsByStatus(ReservationStatus.AGENDADA);

        assertNotNull(reservations);
        assertEquals(1, reservations.size());
        verify(reservationRepository, times(1)).findByStatus(ReservationStatus.AGENDADA);
    }

    @Test
    void shouldGetReservationsByUserIdAndStatus() {
        when(reservationRepository.findByUserIdAndStatus("user1", ReservationStatus.AGENDADA)).thenReturn(List.of(reservation));

        List<Reservation> reservations = reservationService.getReservationsByUserIdAndStatus("user1", ReservationStatus.AGENDADA);

        assertNotNull(reservations);
        assertEquals(1, reservations.size());
        verify(reservationRepository, times(1)).findByUserIdAndStatus("user1", ReservationStatus.AGENDADA);
    }

    @Test
    void shouldGetReservationsByDate() {
        LocalDate date = LocalDate.now();
        when(reservationRepository.findByDate(date)).thenReturn(List.of(reservation));

        List<Reservation> reservations = reservationService.getReservationsByDate(date);

        assertNotNull(reservations);
        assertEquals(1, reservations.size());
        verify(reservationRepository, times(1)).findByDate(date);
    }

    @Test
    void shouldGetReservationsByStartTime() {
        LocalTime startTime = LocalTime.of(10, 0);
        when(reservationRepository.findByStartTime(startTime)).thenReturn(List.of(reservation));

        List<Reservation> reservations = reservationService.getReservationsByStartTime(startTime);

        assertNotNull(reservations);
        assertEquals(1, reservations.size());
        verify(reservationRepository, times(1)).findByStartTime(startTime);
    }

    @Test
    void shouldGetReservationsByDuration() {
        when(reservationRepository.findByDuration(60)).thenReturn(List.of(reservation));

        List<Reservation> reservations = reservationService.getReservationsByDuration(60);

        assertNotNull(reservations);
        assertEquals(1, reservations.size());
        verify(reservationRepository, times(1)).findByDuration(60);
    }



    @Test
    void shouldThrowExceptionWhenReservationNotFound() {
        when(reservationRepository.findById("invalidId")).thenReturn(Optional.empty());

        EciReservesException exception = assertThrows(EciReservesException.class, () -> reservationService.getReservationById("invalidId"));
        assertNotNull(exception);
    }

    @Test
    void shouldCreateReservation() throws EciReservesException {
        when(laboratoryRepository.findById("lab1")).thenReturn(Optional.of(laboratory));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        Reservation created = reservationService.createReservation(reservationDTO);

        assertNotNull(created);
        assertEquals("user1", created.getUserId());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void shouldNotCreateReservationWhenLaboratoryNotFound() {
        when(laboratoryRepository.findById("lab1")).thenReturn(Optional.empty());

        EciReservesException exception = assertThrows(EciReservesException.class, () -> reservationService.createReservation(reservationDTO));
        assertNotNull(exception);
    }

    @Test
    void shouldUpdateReservation() throws EciReservesException {
        when(reservationRepository.findById("res123")).thenReturn(Optional.of(reservation));
        when(laboratoryRepository.findById("lab1")).thenReturn(Optional.of(laboratory));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        laboratory.addTimeSlot(reservation.getDate(), reservation.getStartTime(), reservation.getStartTime().plusMinutes(reservation.getDuration()));
        reservationDTO.setNewStartTime(LocalTime.of(11, 0));
        reservationDTO.setNewDuration(90);
        
        Reservation updated = reservationService.updateReservation("res123", reservationDTO);

        assertNotNull(updated);
        assertEquals(LocalTime.of(11, 0), updated.getStartTime());
        assertEquals(90, updated.getDuration());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void shouldNotUpdateReservationWhenNotFound() {
        when(reservationRepository.findById("invalidId")).thenReturn(Optional.empty());

        EciReservesException exception = assertThrows(EciReservesException.class, () -> reservationService.updateReservation("invalidId", reservationDTO));
        assertNotNull(exception);
    }

    @Test
    void shouldDeleteReservation() throws EciReservesException {
        when(reservationRepository.findById("res123")).thenReturn(Optional.of(reservation));
        when(laboratoryRepository.findById("lab1")).thenReturn(Optional.of(laboratory));
        laboratory.addTimeSlot(LocalDate.now(),reservation.getStartTime(), reservation.getStartTime().plusMinutes(reservation.getDuration()));
        reservationService.deleteReservation("res123");

        verify(reservationRepository, times(1)).deleteById("res123");
    }

    @Test
    void shouldNotDeleteReservationWhenNotFound() {
        when(reservationRepository.findById("invalidId")).thenReturn(Optional.empty());

        EciReservesException exception = assertThrows(EciReservesException.class, () -> reservationService.deleteReservation("invalidId"));
        assertNotNull(exception);
    }
}
