package edu.eci.cvds.ecireserves.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import edu.eci.cvds.ecireserves.dto.ReservationDTO;
import edu.eci.cvds.ecireserves.enums.ReservationStatus;
import edu.eci.cvds.ecireserves.exception.EciReservesException;
import edu.eci.cvds.ecireserves.model.ApiResponse;
import edu.eci.cvds.ecireserves.model.Reservation;
import edu.eci.cvds.ecireserves.service.ReservationService;

class ReservationControllerTest {

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationController reservationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @SuppressWarnings("null")
    @Test
    void getReservationById_ShouldReturnReservation() throws EciReservesException {
        Reservation reservation = new Reservation("1", "user1", "lab1", LocalDate.now(), LocalTime.of(10, 0), 30, LocalTime.now().plusMinutes(30), "Study", ReservationStatus.AGENDADA);
        when(reservationService.getReservationById("1")).thenReturn(reservation);

        ResponseEntity<ApiResponse<Reservation>> response = reservationController.getReservationById("1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("1", response.getBody().getData().getId());
        verify(reservationService, times(1)).getReservationById("1");
    }
    
    @SuppressWarnings("null")
    @Test
    void getAllReservations_ShouldReturnListOfReservations() {
        List<Reservation> reservations = Arrays.asList(new Reservation("1", "u1", "l1", LocalDate.now(), LocalTime.now(), 30, LocalTime.now().plusMinutes(30), "description", ReservationStatus.AGENDADA));
        when(reservationService.getAllReservations()).thenReturn(reservations);

        ResponseEntity<ApiResponse<List<Reservation>>> response = reservationController.getAllReservations();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().size());
        verify(reservationService, times(1)).getAllReservations();
    }



    @SuppressWarnings("null")
    @Test
    void getReservationsByLaboratoryId_ShouldReturnReservations() {
        List<Reservation> reservations = Arrays.asList(new Reservation("1", "user1", "lab1", LocalDate.now(), LocalTime.of(10, 0), 120, LocalTime.now().plusMinutes(120), "Study", ReservationStatus.AGENDADA));
        when(reservationService.getReservationsByLaboratoryId("lab1")).thenReturn(reservations);

        ResponseEntity<ApiResponse<List<Reservation>>> response = reservationController.getReservationsByLaboratoryId("lab1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().size());
        verify(reservationService, times(1)).getReservationsByLaboratoryId("lab1");
    }

    @SuppressWarnings("null")
    @Test
    void getReservationsByStatus_ShouldReturnReservations() {
        List<Reservation> reservations = Arrays.asList(new Reservation("1", "user1", "lab1", LocalDate.now(), LocalTime.of(10, 0), 30, LocalTime.now().plusMinutes(30), "Study", ReservationStatus.AGENDADA));
        when(reservationService.getReservationsByStatus(ReservationStatus.AGENDADA)).thenReturn(reservations);

        ResponseEntity<ApiResponse<List<Reservation>>> response = reservationController.getReservationsByStatus(ReservationStatus.AGENDADA);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().size());
        verify(reservationService, times(1)).getReservationsByStatus(ReservationStatus.AGENDADA);
    }

    @SuppressWarnings("null")
    @Test
    void createReservation_ShouldReturnCreatedReservation() throws EciReservesException {
        ReservationDTO reservationDTO = new ReservationDTO("1", "user1", "lab1", LocalDate.now(), LocalTime.of(10, 0), 30, "Study");
        Reservation createdReservation = new Reservation("1", "user1", "lab1", LocalDate.now(), LocalTime.of(10, 0), 30, LocalTime.now().plusMinutes(30), "Study", ReservationStatus.AGENDADA);
        when(reservationService.createReservation(reservationDTO)).thenReturn(createdReservation);

        ResponseEntity<ApiResponse<Reservation>> response = reservationController.createReservation(reservationDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("1", response.getBody().getData().getId());
        verify(reservationService, times(1)).createReservation(reservationDTO);
    }

    @SuppressWarnings("null")
    @Test
    void updateReservation_ShouldReturnUpdatedReservation() throws EciReservesException {
        ReservationDTO reservationDTO = new ReservationDTO("1", "user1", "lab1", LocalDate.now(), LocalTime.of(11, 0), 30, "Updated Study");
        Reservation updatedReservation = new Reservation("1", "user1", "lab1", LocalDate.now(), LocalTime.of(11, 0), 30, LocalTime.now().plusMinutes(30), "Updated Study", ReservationStatus.AGENDADA);
        when(reservationService.updateReservation("1", reservationDTO)).thenReturn(updatedReservation);

        ResponseEntity<ApiResponse<Reservation>> response = reservationController.updateReservation("1", reservationDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Updated Study", response.getBody().getData().getPurpose());
        verify(reservationService, times(1)).updateReservation("1", reservationDTO);
    }



    @SuppressWarnings("null")
    @Test
    void getReservationsByUserIdAndStatus_ShouldReturnReservations() {
        List<Reservation> reservations = Arrays.asList(
            new Reservation("1", "user1", "lab1", LocalDate.now(), LocalTime.of(10, 0), 30, LocalTime.now().plusMinutes(30), "Study", ReservationStatus.AGENDADA)
        );
        when(reservationService.getReservationsByUserIdAndStatus("user1", ReservationStatus.AGENDADA)).thenReturn(reservations);

        ResponseEntity<ApiResponse<List<Reservation>>> response = reservationController.getReservationsByUserIdAndStatus("user1", ReservationStatus.AGENDADA);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().size());
        verify(reservationService, times(1)).getReservationsByUserIdAndStatus("user1", ReservationStatus.AGENDADA);
    }

    @SuppressWarnings("null")
    @Test
    void getReservationsByDuration_ShouldReturnReservations() {
        List<Reservation> reservations = Arrays.asList(
            new Reservation("1", "user1", "lab1", LocalDate.now(), LocalTime.of(10, 0), 60, LocalTime.now().plusMinutes(60), "Study", ReservationStatus.AGENDADA)
        );
        when(reservationService.getReservationsByDuration(60)).thenReturn(reservations);

        ResponseEntity<ApiResponse<List<Reservation>>> response = reservationController.getReservationsByDuration(60);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().size());
        verify(reservationService, times(1)).getReservationsByDuration(60);
    }

    @SuppressWarnings("null")
    @Test
    void getReservationsByDate_ShouldReturnReservations() {
        LocalDate date = LocalDate.now();
        List<Reservation> reservations = Arrays.asList(
            new Reservation("1", "user1", "lab1", date, LocalTime.of(10, 0), 30, LocalTime.now().plusMinutes(30), "Study", ReservationStatus.AGENDADA)
        );
        when(reservationService.getReservationsByDate(date)).thenReturn(reservations);

        ResponseEntity<ApiResponse<List<Reservation>>> response = reservationController.getReservationsByDate(date);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().size());
        verify(reservationService, times(1)).getReservationsByDate(date);
    }

    @SuppressWarnings("null")
    @Test
    void getReservationsByStartTime_ShouldReturnReservations() {
        LocalTime startTime = LocalTime.of(10, 0);
        List<Reservation> reservations = Arrays.asList(
            new Reservation("1", "user1", "lab1", LocalDate.now(), startTime, 30, LocalTime.now().plusMinutes(30), "Study", ReservationStatus.AGENDADA)
        );
        when(reservationService.getReservationsByStartTime(startTime)).thenReturn(reservations);

        ResponseEntity<ApiResponse<List<Reservation>>> response = reservationController.getReservationsByStartTime(startTime);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().size());
        verify(reservationService, times(1)).getReservationsByStartTime(startTime);
    }

}
