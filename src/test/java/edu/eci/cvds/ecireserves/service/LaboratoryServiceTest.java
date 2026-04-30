package edu.eci.cvds.ecireserves.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.eci.cvds.ecireserves.dto.LaboratoryDTO;
import edu.eci.cvds.ecireserves.enums.LaboratoryStatus;
import edu.eci.cvds.ecireserves.exception.EciReservesException;
import edu.eci.cvds.ecireserves.model.Laboratory;
import edu.eci.cvds.ecireserves.repository.LaboratoryRepository;

@ExtendWith(MockitoExtension.class)
class LaboratoryServiceTest {

    @Mock
    private LaboratoryRepository laboratoryRepository;

    @InjectMocks
    private LaboratoryService laboratoryService;

    private Laboratory laboratory;
    private LaboratoryDTO laboratoryDTO;

    @BeforeEach
    void setUp() {
        laboratory = new Laboratory("1", "A101", "Computer Lab", 30, "Lab for programming", LocalTime.of(8, 0), LocalTime.of(18, 0), LaboratoryStatus.ACTIVO, null, Map.of());
        laboratoryDTO = new LaboratoryDTO("1", "A101", "Computer Lab", 30, "Lab for programming", LocalTime.of(8, 0), LocalTime.of(18, 0), LaboratoryStatus.ACTIVO);
    }

    @Test
    void shouldGetAllLaboratories() {
        when(laboratoryRepository.findAll()).thenReturn(List.of(laboratory));

        List<Laboratory> laboratories = laboratoryService.getAllLaboratories();

        assertFalse(laboratories.isEmpty());
        assertEquals(1, laboratories.size());
        assertEquals("1", laboratories.get(0).getId());
    }

    @Test
    void shouldGetLaboratoryById() throws EciReservesException {
        when(laboratoryRepository.findById("1")).thenReturn(Optional.of(laboratory));

        Laboratory foundLab = laboratoryService.getLaboratoryById("1");

        assertNotNull(foundLab);
        assertEquals("1", foundLab.getId());
    }

    @Test
    void shouldNotGetLaboratoryById_WhenNotFound() {
        when(laboratoryRepository.findById("2")).thenReturn(Optional.empty());

        EciReservesException exception = assertThrows(EciReservesException.class, () -> laboratoryService.getLaboratoryById("2"));

        assertEquals(EciReservesException.LABORATORY_NOT_FOUND, exception.getMessage());
    }

    @Test
    void shouldGetLaboratoryByClassroom() {
        when(laboratoryRepository.findByClassroom("A101")).thenReturn(List.of(laboratory));
    
        List<Laboratory> labs = laboratoryService.getLaboratoriesByClassroom("A101");
    
        assertFalse(labs.isEmpty());
        assertEquals(1, labs.size());
        assertEquals("A101", labs.get(0).getClassroom());
    }    

    @Test
    void shouldCreateLaboratory() throws EciReservesException {
        when(laboratoryRepository.findById("1")).thenReturn(Optional.empty());
        when(laboratoryRepository.save(any(Laboratory.class))).thenReturn(laboratory);

        Laboratory createdLab = laboratoryService.createLaboratory(laboratoryDTO);

        assertNotNull(createdLab);
        assertEquals("1", createdLab.getId());
    }

    @Test
    void shouldNotCreateLaboratory_WhenAlreadyExists() {
        when(laboratoryRepository.findById("1")).thenReturn(Optional.of(laboratory));

        EciReservesException exception = assertThrows(EciReservesException.class, () -> laboratoryService.createLaboratory(laboratoryDTO));

        assertEquals(EciReservesException.LABORATORY_ALREADY_EXISTS, exception.getMessage());
    }

    @Test
    void shouldUpdateLaboratory() throws EciReservesException {
        when(laboratoryRepository.findById("1")).thenReturn(Optional.of(laboratory));
        when(laboratoryRepository.save(any(Laboratory.class))).thenReturn(laboratory);

        LaboratoryDTO updatedDTO = new LaboratoryDTO("1", "B202", "Updated Lab", 40, "New description", LocalTime.of(9, 0), LocalTime.of(19, 0), LaboratoryStatus.ACTIVO);
        
        Laboratory updatedLab = laboratoryService.updateLaboratory("1", updatedDTO);

        assertNotNull(updatedLab);
        assertEquals("B202", updatedLab.getClassroom());
        assertEquals("Updated Lab", updatedLab.getName());
        assertEquals(40, updatedLab.getCapacity());
    }

    @Test
    void shouldNotUpdateLaboratory_WhenNotFound() {
        when(laboratoryRepository.findById("2")).thenReturn(Optional.empty());

        EciReservesException exception = assertThrows(EciReservesException.class, () -> laboratoryService.updateLaboratory("2", laboratoryDTO));

        assertEquals(EciReservesException.LABORATORY_NOT_FOUND, exception.getMessage());
    }

    @Test
    void shouldDeleteLaboratory() throws EciReservesException {
        when(laboratoryRepository.existsById("1")).thenReturn(true);

        laboratoryService.deleteLaboratory("1");

        verify(laboratoryRepository).deleteById("1");
    }

    @Test
    void shouldNotDeleteLaboratory_WhenNotFound() {
        when(laboratoryRepository.existsById("1")).thenReturn(false);

        EciReservesException exception = assertThrows(EciReservesException.class, () -> laboratoryService.deleteLaboratory("1"));

        assertEquals(EciReservesException.LABORATORY_NOT_FOUND, exception.getMessage());
    }

    @Test
    void shouldGetLaboratoryByName() {
        when(laboratoryRepository.findByName("Computer Lab")).thenReturn(List.of(laboratory));

        List<Laboratory> labs = laboratoryService.getLaboratoriesByName("Computer Lab");

        assertFalse(labs.isEmpty());
        assertEquals(1, labs.size());
        assertEquals("Computer Lab", labs.get(0).getName());
    }

    @Test
    void shouldGetLaboratoryByCapacity() {
        when(laboratoryRepository.findByCapacity(30)).thenReturn(List.of(laboratory));

        List<Laboratory> labs = laboratoryService.getLaboratoriesByCapacity(30);

        assertFalse(labs.isEmpty());
        assertEquals(30, labs.get(0).getCapacity());
    }

    @Test
    void shouldGetLaboratoryByOpeningTime() {
        when(laboratoryRepository.findByOpeningTime(LocalTime.of(8, 0))).thenReturn(List.of(laboratory));

        List<Laboratory> labs = laboratoryService.getLaboratoriesByOpeningTime(LocalTime.of(8, 0));

        assertFalse(labs.isEmpty());
        assertEquals(LocalTime.of(8, 0), labs.get(0).getOpeningTime());
    }

    @Test
    void shouldGetLaboratoriesByDate() {
        LocalDate date = LocalDate.of(2025, 3, 29);
        laboratory.setTimeSlotsByDate(Map.of(date, List.of())); // Simulamos que el laboratorio tiene horarios en esa fecha
        
        when(laboratoryRepository.findAll()).thenReturn(List.of(laboratory));

        List<Laboratory> labs = laboratoryService.getLaboratoriesByDate(date);

        assertFalse(labs.isEmpty());
        assertEquals(1, labs.size());
        assertEquals("1", labs.get(0).getId());
    }

    @Test
    void shouldGetLaboratoriesByStatus() {
        when(laboratoryRepository.findByStatus(LaboratoryStatus.ACTIVO)).thenReturn(List.of(laboratory));

        List<Laboratory> labs = laboratoryService.getLaboratoriesByStatus(LaboratoryStatus.ACTIVO);

        assertFalse(labs.isEmpty());
        assertEquals(1, labs.size());
        assertEquals(LaboratoryStatus.ACTIVO, labs.get(0).getStatus());
    }

}
