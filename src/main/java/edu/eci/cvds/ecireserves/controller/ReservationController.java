package edu.eci.cvds.ecireserves.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import edu.eci.cvds.ecireserves.model.User;
import edu.eci.cvds.ecireserves.service.UserService;
import org.springframework.security.access.AccessDeniedException;

import edu.eci.cvds.ecireserves.dto.ReservationDTO;
import edu.eci.cvds.ecireserves.enums.ReservationStatus;
import edu.eci.cvds.ecireserves.exception.EciReservesException;
import edu.eci.cvds.ecireserves.model.ApiResponse;
import edu.eci.cvds.ecireserves.model.Reservation;
import edu.eci.cvds.ecireserves.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api")
@Tag(name = "Reservas", description = "Operaciones para la gestión de reservas en el sistema")
public class ReservationController {

    private final ReservationService reservationService;
    private final UserService userService;

    public ReservationController(ReservationService reservationService, UserService userService) {
        this.reservationService = reservationService;
        this.userService = userService;
    }

    @GetMapping("/user/reservations")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ADMINISTRADOR', 'PROFESOR')")
    @Operation(summary = "Obtener todas las reservas", description = "Devuelve una lista de todas las reservas registradas en el sistema.")
    public ResponseEntity<ApiResponse<List<Reservation>>> getAllReservations() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Reservas obtenidas exitosamente", reservationService.getAllReservations()));
    }

    @GetMapping("/user/reservations/{id}")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ADMINISTRADOR', 'PROFESOR')")
    @Operation(summary = "Obtener una reserva por ID", description = "Busca una reserva en el sistema según su identificador único.")
    public ResponseEntity<ApiResponse<Reservation>> getReservationById(
            @Parameter(description = "ID de la reserva a buscar", required = true) @PathVariable("id") String id)
            throws EciReservesException {
        return ResponseEntity.ok(new ApiResponse<>(true, "Reserva con id " + id + " encontrada", reservationService.getReservationById(id)));
    }

    @GetMapping("/user/laboratory/{laboratoryId}")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ADMINISTRADOR', 'PROFESOR')")
    @Operation(summary = "Obtener reservas por laboratorio", description = "Devuelve todas las reservas realizadas en un laboratorio específico.")
    public ResponseEntity<ApiResponse<List<Reservation>>> getReservationsByLaboratoryId(
            @Parameter(description = "ID del laboratorio", required = true) @PathVariable("laboratoryId") String laboratoryId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Reservas del laboratorio con id: " + laboratoryId, reservationService.getReservationsByLaboratoryId(laboratoryId)));
    }

    @GetMapping("/user/reservations/status/{status}")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ADMINISTRADOR', 'PROFESOR')")
    @Operation(summary = "Obtener reservas por estado", description = "Devuelve todas las reservas según su estado (PENDIENTE, CONFIRMADA, CANCELADA).")
    public ResponseEntity<ApiResponse<List<Reservation>>> getReservationsByStatus(
            @Parameter(description = "Estado de la reserva", required = true) @PathVariable("status") ReservationStatus status) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Reservas por estado: " + status, reservationService.getReservationsByStatus(status)));
    }

    @GetMapping("/user/reservations/{userId}/status/{status}")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ADMINISTRADOR', 'PROFESOR')")
    @Operation(summary = "Obtener reservas por usuario y estado", description = "Devuelve todas las reservas de un usuario filtradas por estado.")
    public ResponseEntity<ApiResponse<List<Reservation>>> getReservationsByUserIdAndStatus(
            @Parameter(description = "ID del usuario", required = true) @PathVariable("userId") String userId,
            @Parameter(description = "Estado de la reserva", required = true) @PathVariable("status") ReservationStatus status) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Reservas del usuario con id: " + userId + " y estado: " + status, reservationService.getReservationsByUserIdAndStatus(userId, status)));
    }

    @GetMapping("/user/reservations/date/{date}")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ADMINISTRADOR', 'PROFESOR')")
    @Operation(summary = "Obtener reservas por fecha", description = "Devuelve todas las reservas realizadas en una fecha específica.")
    public ResponseEntity<ApiResponse<List<Reservation>>> getReservationsByDate(
            @Parameter(description = "Fecha de la reserva (YYYY-MM-DD)", required = true) @PathVariable("date") LocalDate date) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Reservas por fecha: " + date.toString(), reservationService.getReservationsByDate(date)));
    }

    @GetMapping("/user/reservations/startsTime/{startTime}")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ADMINISTRADOR', 'PROFESOR')")
    @Operation(summary = "Obtener reservas por hora de inicio", description = "Devuelve todas las reservas que inician a una hora específica.")
    public ResponseEntity<ApiResponse<List<Reservation>>> getReservationsByStartTime(
            @Parameter(description = "Hora de inicio de la reserva (HH:MM:SS)", required = true) @PathVariable("startTime") LocalTime startTime) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Reservas que inician a las: " + startTime.toString(), reservationService.getReservationsByStartTime(startTime)));
    }

    @GetMapping("/user/reservations/duration/{duration}")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ADMINISTRADOR', 'PROFESOR')")
    @Operation(summary = "Obtener reservas por duración", description = "Devuelve todas las reservas con una duración específica en minutos.")
    public ResponseEntity<ApiResponse<List<Reservation>>> getReservationsByDuration(
            @Parameter(description = "Duración de la reserva en minutos", required = true) @PathVariable("duration") int duration) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Reservas por duración: " + duration + " MIN", reservationService.getReservationsByDuration(duration)));
    }

    @PostMapping("/user/reservations")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ADMINISTRADOR', 'PROFESOR')")
    @Operation(summary = "Crear una reserva", description = "Registra una nueva reserva en el sistema con la información proporcionada.")
    public ResponseEntity<ApiResponse<Reservation>> createReservation(
            @Parameter(description = "Datos de la reserva a crear", required = true) @RequestBody ReservationDTO reservationDTO)
            throws EciReservesException {
        return ResponseEntity.ok(new ApiResponse<>(true, "Reserva creada", reservationService.createReservation(reservationDTO)));
    }

    @PutMapping("/admin/reservations/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Actualizar una reserva", description = "Modifica los datos de una reserva existente según su ID.")
    public ResponseEntity<ApiResponse<Reservation>> updateReservation(
            @Parameter(description = "ID de la reserva a actualizar", required = true) @PathVariable("id") String id,
            @Parameter(description = "Datos actualizados de la reserva", required = true) @RequestBody ReservationDTO reservationDTO)
            throws EciReservesException {
        return ResponseEntity.ok(new ApiResponse<>(true, "Reserva actualizada", reservationService.updateReservation(id, reservationDTO)));
    }

    @DeleteMapping("/user/reservations/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReservation(
            @PathVariable("id") String id,
            Authentication auth) throws EciReservesException {
        Reservation r = reservationService.getReservationById(id);
        User me = userService.getUserByEmail(auth.getName())
                .orElseThrow(() -> new EciReservesException(EciReservesException.USER_NOT_FOUND));
        if (!r.getUserId().equals(me.getId()) && !isAdmin(auth))
            throw new AccessDeniedException("No tienes permiso para eliminar esta reserva");
        reservationService.deleteReservation(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Reserva eliminada", null));
    }

    @GetMapping("/user/reservations/user/{userId}")
    public ResponseEntity<ApiResponse<List<Reservation>>> getReservationsByUserId(
            @PathVariable("userId") String userId,
            Authentication auth) throws EciReservesException {
        User me = userService.getUserByEmail(auth.getName())
                .orElseThrow(() -> new EciReservesException(EciReservesException.USER_NOT_FOUND));
        if (!me.getId().equals(userId) && !isAdmin(auth))
            throw new AccessDeniedException("No tienes permiso para ver estas reservas");
        return ResponseEntity.ok(new ApiResponse<>(true,
                "Reservas del usuario con id: " + userId,
                reservationService.getReservationsByUserId(userId)));
    }
    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));
    }
}