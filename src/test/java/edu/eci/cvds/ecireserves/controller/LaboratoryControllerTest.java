package edu.eci.cvds.ecireserves.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import edu.eci.cvds.ecireserves.dto.LaboratoryDTO;
import edu.eci.cvds.ecireserves.enums.LaboratoryStatus;
import edu.eci.cvds.ecireserves.exception.EciReservesException;
import edu.eci.cvds.ecireserves.model.ApiResponse;
import edu.eci.cvds.ecireserves.model.Laboratory;
import edu.eci.cvds.ecireserves.service.LaboratoryService;

@ExtendWith(MockitoExtension.class)
class LaboratoryControllerTest {

    @InjectMocks
    private LaboratoryController laboratoryController;

    @Mock
    private LaboratoryService laboratoryService;

    private List<Laboratory> sampleLabs;

    @BeforeEach
    void setUp() {
        sampleLabs = List.of(
            new Laboratory("1", "A101", "Lab Redes", 30, "Laboratorio de Redes", LocalTime.of(7, 0), LocalTime.of(19, 0), LaboratoryStatus.ACTIVO, new ArrayList<>(), Collections.emptyMap()),
            new Laboratory("2", "B202", "Lab Software", 25, "Laboratorio de Software", LocalTime.of(7, 0), LocalTime.of(19, 0), LaboratoryStatus.ACTIVO, new ArrayList<>(), Collections.emptyMap())
        );
    }

    @SuppressWarnings("null")
    @Test
    void updateLaboratory_ShouldReturnUpdatedLaboratory_WhenExists() throws EciReservesException {
        LaboratoryDTO laboratoryDTO = new LaboratoryDTO("L202", "B-202", "Lab Redes", 30, "Laboratorio de Redes", LocalTime.of(8, 0), LocalTime.of(18, 0), LaboratoryStatus.ACTIVO);
        Laboratory updatedLab = new Laboratory("L202", "B-202", "Lab Redes", 30, "Laboratorio de Redes", LocalTime.of(8, 0), LocalTime.of(18, 0), LaboratoryStatus.ACTIVO, new ArrayList<>(), Collections.emptyMap());

        when(laboratoryService.updateLaboratory("L202", laboratoryDTO)).thenReturn(updatedLab);

        ResponseEntity<ApiResponse<Laboratory>> response = laboratoryController.updateLaboratory("L202", laboratoryDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Lab Redes", response.getBody().getData().getName());
        verify(laboratoryService, times(1)).updateLaboratory("L202", laboratoryDTO);
    }

    @Test
    void updateLaboratory_ShouldThrowException_WhenLaboratoryNotFound() throws EciReservesException {
        LaboratoryDTO laboratoryDTO = new LaboratoryDTO("L202", "B-202", "Lab Redes", 30, "Laboratorio de Redes", LocalTime.of(8, 0), LocalTime.of(18, 0), LaboratoryStatus.ACTIVO);

        when(laboratoryService.updateLaboratory("99", laboratoryDTO)).thenThrow(new EciReservesException("Laboratorio no encontrado"));

        EciReservesException exception = assertThrows(EciReservesException.class, () -> {
            laboratoryController.updateLaboratory("99", laboratoryDTO);
        });

        assertEquals("Laboratorio no encontrado", exception.getMessage());
        verify(laboratoryService, times(1)).updateLaboratory("99", laboratoryDTO);
    }

    @SuppressWarnings("null")
    @Test
    void deleteLaboratory_ShouldReturnSuccessMessage_WhenExists() throws EciReservesException {
        doNothing().when(laboratoryService).deleteLaboratory("1");

        ResponseEntity<ApiResponse<Void>> response = laboratoryController.deleteLaboratory("1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Laboratorio eliminado", response.getBody().getMessage());
        verify(laboratoryService, times(1)).deleteLaboratory("1");
    }

    @Test
    void deleteLaboratory_ShouldThrowException_WhenLaboratoryNotFound() throws EciReservesException {
        doThrow(new EciReservesException("Laboratorio no encontrado")).when(laboratoryService).deleteLaboratory("99");

        EciReservesException exception = assertThrows(EciReservesException.class, () -> {
            laboratoryController.deleteLaboratory("99");
        });

        assertEquals("Laboratorio no encontrado", exception.getMessage());
        verify(laboratoryService, times(1)).deleteLaboratory("99");
    }

    @SuppressWarnings("null")
    @Test
    void getLaboratoryById_ShouldReturnLaboratory_WhenExists() throws EciReservesException {
        Laboratory lab = new Laboratory("L202", "B-202", "Lab Redes", 30, "Laboratorio de Redes", LocalTime.of(8, 0), LocalTime.of(18, 0), LaboratoryStatus.ACTIVO, new ArrayList<>(), Collections.emptyMap());

        when(laboratoryService.getLaboratoryById("L202")).thenReturn(lab);

        ResponseEntity<ApiResponse<Laboratory>> response = laboratoryController.getLaboratoryById("L202");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("B-202", response.getBody().getData().getClassroom());
        verify(laboratoryService, times(1)).getLaboratoryById("L202");
    }

    @Test
    void getLaboratoryById_ShouldThrowException_WhenLaboratoryNotFound() throws EciReservesException {
        when(laboratoryService.getLaboratoryById("99")).thenThrow(new EciReservesException("Laboratorio no encontrado"));

        EciReservesException exception = assertThrows(EciReservesException.class, () -> {
            laboratoryController.getLaboratoryById("99");
        });

        assertEquals("Laboratorio no encontrado", exception.getMessage());
        verify(laboratoryService, times(1)).getLaboratoryById("99");
    }

    @SuppressWarnings("null")
    @Test
    void getLaboratoryByCapacity_ShouldReturnLaboratories_WhenExists() {
        List<Laboratory> labs = List.of(
            new Laboratory("1", "A101", "Lab Redes", 30, "Laboratorio de Redes", LocalTime.of(7, 0), LocalTime.of(19, 0), LaboratoryStatus.ACTIVO, new ArrayList<>(), Collections.emptyMap()),
            new Laboratory("2", "A101", "Lab Redes", 30, "Laboratorio de Redes", LocalTime.of(7, 0), LocalTime.of(19, 0), LaboratoryStatus.ACTIVO, new ArrayList<>(), Collections.emptyMap())
        );

        when(laboratoryService.getLaboratoriesByCapacity(30)).thenReturn(labs);

        ResponseEntity<ApiResponse<List<Laboratory>>> response = laboratoryController.getLaboratoriesByCapacity(30);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(2, response.getBody().getData().size());
        verify(laboratoryService, times(1)).getLaboratoriesByCapacity(30);
    }

    @SuppressWarnings("null")
    @Test
    void getAllLaboratories_ShouldReturnListOfLaboratories() {
        when(laboratoryService.getAllLaboratories()).thenReturn(sampleLabs);

        ResponseEntity<ApiResponse<List<Laboratory>>> response = laboratoryController.getAllLaboratories();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(2, response.getBody().getData().size());
        verify(laboratoryService, times(1)).getAllLaboratories();
    }

    @SuppressWarnings("null")
    @Test
    void getLaboratoryByClassroom_ShouldReturnLaboratory_WhenExists() {
        when(laboratoryService.getLaboratoriesByClassroom("A101")).thenReturn(List.of(sampleLabs.get(0)));

        ResponseEntity<ApiResponse<List<Laboratory>>> response = laboratoryController.getLaboratoriesByClassroom("A101");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("A101", response.getBody().getData().get(0).getClassroom());
        verify(laboratoryService, times(1)).getLaboratoriesByClassroom("A101");
    }

    @SuppressWarnings("null")
    @Test
    void getLaboratoryByClassroom_ShouldReturnEmptyList_WhenNotFound() {
        when(laboratoryService.getLaboratoriesByClassroom("Z999")).thenReturn(Collections.emptyList());

        ResponseEntity<ApiResponse<List<Laboratory>>> response = laboratoryController.getLaboratoriesByClassroom("Z999");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertTrue(response.getBody().getData().isEmpty());
        verify(laboratoryService, times(1)).getLaboratoriesByClassroom("Z999");
    }

    @SuppressWarnings("null")
    @Test
    void getLaboratoriesByName_ShouldReturnMatchingLaboratories() {
        when(laboratoryService.getLaboratoriesByName("Lab Redes")).thenReturn(List.of(sampleLabs.get(0)));

        ResponseEntity<ApiResponse<List<Laboratory>>> response = laboratoryController.getLaboratoriesByName("Lab Redes");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Lab Redes", response.getBody().getData().get(0).getName());
        verify(laboratoryService, times(1)).getLaboratoriesByName("Lab Redes");
    }
    
    @SuppressWarnings("null")
    @Test
    void getLaboratoryByOpeningTime_ShouldReturnLaboratories_WhenExists() {
        LocalTime openingTime = LocalTime.of(7, 0);

        when(laboratoryService.getLaboratoriesByOpeningTime(openingTime)).thenReturn(sampleLabs);

        ResponseEntity<ApiResponse<List<Laboratory>>> response = laboratoryController.getLaboratoriesByOpeningTime(openingTime);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(2, response.getBody().getData().size());
        assertEquals(openingTime, response.getBody().getData().get(0).getOpeningTime());
        verify(laboratoryService, times(1)).getLaboratoriesByOpeningTime(openingTime);
    }

    @SuppressWarnings("null")
    @Test
    void createLaboratory_ShouldReturnCreatedLaboratory() throws EciReservesException {
        LaboratoryDTO laboratoryDTO = new LaboratoryDTO("L202", "B-202", "Lab Redes", 30, "Laboratorio de Redes", LocalTime.of(8, 0), LocalTime.of(18, 0), LaboratoryStatus.ACTIVO);
        Laboratory createdLab = new Laboratory("L202", "B-202", "Lab Redes", 30, "Laboratorio de Redes", LocalTime.of(8, 0), LocalTime.of(18, 0), LaboratoryStatus.ACTIVO, new ArrayList<>(), Collections.emptyMap());
        when(laboratoryService.createLaboratory(laboratoryDTO)).thenReturn(createdLab);

        ResponseEntity<ApiResponse<Laboratory>> response = laboratoryController.createLaboratory(laboratoryDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Laboratorio creado", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        assertEquals(createdLab.getId(), response.getBody().getData().getId());
        assertEquals(createdLab.getName(), response.getBody().getData().getName());

        verify(laboratoryService, times(1)).createLaboratory(laboratoryDTO);
    }

    @SuppressWarnings("null")
    @Test
    void getLaboratoriesByDate_ShouldReturnLaboratories_WhenExists() {
        LocalDate date = LocalDate.of(2025, 3, 29);

        when(laboratoryService.getLaboratoriesByDate(date)).thenReturn(sampleLabs);

        ResponseEntity<ApiResponse<List<Laboratory>>> response = laboratoryController.getLaboratoriesByDate(date);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(2, response.getBody().getData().size());
        verify(laboratoryService, times(1)).getLaboratoriesByDate(date);
    }

    @SuppressWarnings("null")
    @Test
    void getLaboratoriesByDate_ShouldReturnEmptyList_WhenNoLaboratoriesFound() {
        LocalDate date = LocalDate.of(2025, 3, 30);

        when(laboratoryService.getLaboratoriesByDate(date)).thenReturn(Collections.emptyList());

        ResponseEntity<ApiResponse<List<Laboratory>>> response = laboratoryController.getLaboratoriesByDate(date);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertTrue(response.getBody().getData().isEmpty());
        verify(laboratoryService, times(1)).getLaboratoriesByDate(date);
    }

    @SuppressWarnings("null")
    @Test
    void getLaboratoriesByStatus_ShouldReturnLaboratories_WhenExists() {
        when(laboratoryService.getLaboratoriesByStatus(LaboratoryStatus.ACTIVO)).thenReturn(List.of(sampleLabs.get(0)));

        ResponseEntity<ApiResponse<List<Laboratory>>> response = laboratoryController.getLaboratoriesByStatus(LaboratoryStatus.ACTIVO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().size());
        assertEquals(LaboratoryStatus.ACTIVO, response.getBody().getData().get(0).getStatus());
        verify(laboratoryService, times(1)).getLaboratoriesByStatus(LaboratoryStatus.ACTIVO);
    }

}
