package edu.eci.cvds.ecireserves.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import edu.eci.cvds.ecireserves.dto.LaboratoryDTO;
import edu.eci.cvds.ecireserves.enums.LaboratoryStatus;
import edu.eci.cvds.ecireserves.exception.EciReservesException;
import edu.eci.cvds.ecireserves.model.ApiResponse;
import edu.eci.cvds.ecireserves.model.Laboratory;
import edu.eci.cvds.ecireserves.service.LaboratoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api")
@Tag(name = "Laboratorios", description = "Operaciones para la gestión de laboratorios en el sistema")
public class LaboratoryController {

    private final LaboratoryService laboratoryService;

    public LaboratoryController(LaboratoryService laboratoryService) {
        this.laboratoryService = laboratoryService;
    }

    @GetMapping("/user/laboratories")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ADMINISTRADOR', 'PROFESOR')")
    @Operation(summary = "Obtener todos los laboratorios", description = "Devuelve una lista de todos los laboratorios registrados.")
    public ResponseEntity<ApiResponse<List<Laboratory>>> getAllLaboratories() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Laboratorios obtenidos exitosamente", laboratoryService.getAllLaboratories()));
    }

    @GetMapping("/user/laboratories/{id}")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ADMINISTRADOR', 'PROFESOR')")
    @Operation(summary = "Obtener un laboratorio por ID", description = "Busca un laboratorio en el sistema según su identificador único.")
    public ResponseEntity<ApiResponse<Laboratory>> getLaboratoryById(
            @Parameter(description = "ID del laboratorio a buscar", required = true) @PathVariable("id") String id)
            throws EciReservesException {
        return ResponseEntity.ok(new ApiResponse<>(true, "Laboratorio con id " + id + " encontrado", laboratoryService.getLaboratoryById(id)));
    }

    @GetMapping("/user/laboratories/classroom/{classroom}")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ADMINISTRADOR', 'PROFESOR')")
    @Operation(summary = "Buscar laboratorios por salón", description = "Devuelve una lista de laboratorios asociados a un salón específico.")
    public ResponseEntity<ApiResponse<List<Laboratory>>> getLaboratoriesByClassroom(
            @Parameter(description = "Salón del laboratorio", required = true) @PathVariable("classroom") String classroom) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Laboratorios del salón " + classroom, laboratoryService.getLaboratoriesByClassroom(classroom)));
    }

    @GetMapping("/user/laboratories/search")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ADMINISTRADOR', 'PROFESOR')")
    @Operation(summary = "Buscar laboratorios por nombre", description = "Devuelve una lista de laboratorios cuyo nombre coincide con el parámetro proporcionado.")
    public ResponseEntity<ApiResponse<List<Laboratory>>> getLaboratoriesByName(
            @Parameter(description = "Nombre del laboratorio", required = true) @RequestParam String name) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Laboratorios por nombre " + name, laboratoryService.getLaboratoriesByName(name)));
    }

    @GetMapping("/user/laboratories/capacity/{capacity}")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ADMINISTRADOR', 'PROFESOR')")
    @Operation(summary = "Buscar laboratorios por capacidad", description = "Devuelve una lista de laboratorios con la capacidad especificada.")
    public ResponseEntity<ApiResponse<List<Laboratory>>> getLaboratoriesByCapacity(
            @Parameter(description = "Capacidad del laboratorio", required = true) @PathVariable("capacity") int capacity) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Laboratorios de capacidad " + capacity, laboratoryService.getLaboratoriesByCapacity(capacity)));
    }

    @GetMapping("/user/laboratories/date/{date}")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ADMINISTRADOR', 'PROFESOR')")
    @Operation(summary = "Buscar laboratorios por fecha", description = "Devuelve una lista de laboratorios disponibles en una fecha específica.")
    public ResponseEntity<ApiResponse<List<Laboratory>>> getLaboratoriesByDate(
            @Parameter(description = "Fecha en formato YYYY-MM-DD", required = true) @PathVariable("date") LocalDate date) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Laboratorios del día " + date, laboratoryService.getLaboratoriesByDate(date)));
    }

    @GetMapping("/user/laboratories/opening-time/{openingTime}")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ADMINISTRADOR', 'PROFESOR')")
    @Operation(summary = "Buscar laboratorios por hora de apertura", description = "Devuelve una lista de laboratorios que abren a la hora especificada.")
    public ResponseEntity<ApiResponse<List<Laboratory>>> getLaboratoriesByOpeningTime(
            @Parameter(description = "Hora de apertura en formato HH:mm", required = true) @PathVariable("openingTime") LocalTime openingTime) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Laboratorios que abren a las: " + openingTime.toString(), laboratoryService.getLaboratoriesByOpeningTime(openingTime)));
    }

    @GetMapping("/user/laboratories/status/{status}")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ADMINISTRADOR', 'PROFESOR')")
    @Operation(summary = "Buscar laboratorios por estado", description = "Devuelve una lista de laboratorios filtrados por su estado actual.")
    public ResponseEntity<ApiResponse<List<Laboratory>>> getLaboratoriesByStatus(
            @Parameter(description = "Estado del laboratorio", required = true) @PathVariable("status") LaboratoryStatus status) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Laboratorios con estado " + status, laboratoryService.getLaboratoriesByStatus(status)));
    }

    @PostMapping("/admin/laboratories")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear un laboratorio", description = "Registra un nuevo laboratorio en el sistema con la información proporcionada.")
    public ResponseEntity<ApiResponse<Laboratory>> createLaboratory(
            @Parameter(description = "Datos del laboratorio a crear", required = true) @RequestBody LaboratoryDTO laboratoryDTO)
            throws EciReservesException {
        return ResponseEntity.ok(new ApiResponse<>(true, "Laboratorio creado", laboratoryService.createLaboratory(laboratoryDTO)));
    }

    @PutMapping("/admin/laboratories/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Actualizar un laboratorio", description = "Modifica los datos de un laboratorio existente según su ID.")
    public ResponseEntity<ApiResponse<Laboratory>> updateLaboratory(
            @Parameter(description = "ID del laboratorio a actualizar", required = true) @PathVariable("id") String id,
            @Parameter(description = "Datos actualizados del laboratorio", required = true) @RequestBody LaboratoryDTO laboratoryDTO)
            throws EciReservesException {
        return ResponseEntity.ok(new ApiResponse<>(true, "Laboratorio actualizado", laboratoryService.updateLaboratory(id, laboratoryDTO)));
    }

    @DeleteMapping("/admin/laboratories/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar un laboratorio", description = "Elimina un laboratorio del sistema según su ID.")
    public ResponseEntity<ApiResponse<Void>> deleteLaboratory(
            @Parameter(description = "ID del laboratorio a eliminar", required = true) @PathVariable("id") String id)
            throws EciReservesException {
        laboratoryService.deleteLaboratory(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Laboratorio eliminado", null));
    }
}