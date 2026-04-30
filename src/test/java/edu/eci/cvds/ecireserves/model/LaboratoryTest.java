package edu.eci.cvds.ecireserves.model;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.eci.cvds.ecireserves.exception.EciReservesException;

class LaboratoryTest {

    private Laboratory laboratory;

    @BeforeEach
    void setUp() {
        laboratory = new Laboratory();
        laboratory.setOpeningTime(LocalTime.of(8, 0));
        laboratory.setClosingTime(LocalTime.of(18, 0));
    }

    @Test
    void testAddValidTimeSlot() throws EciReservesException {
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(10, 0);
        laboratory.addTimeSlot(LocalDate.now(), startTime, endTime);
        assertEquals(1, laboratory.getTimeSlotsByDate().get(LocalDate.now()).size());
    }

    @Test
    void testAddTimeSlotBeforeOpening() {
        LocalTime startTime = LocalTime.of(7, 30);
        LocalTime endTime = LocalTime.of(9, 0);
        EciReservesException exception = assertThrows(EciReservesException.class, () -> laboratory.addTimeSlot(LocalDate.now(), startTime, endTime));
        assertEquals(EciReservesException.INVALID_TIMESLOT, exception.getMessage());
    }

    @Test
    void testAddTimeSlotAfterClosing() {
        LocalTime startTime = LocalTime.of(17, 30);
        LocalTime endTime = LocalTime.of(18, 30);
        EciReservesException exception = assertThrows(EciReservesException.class, () -> laboratory.addTimeSlot(LocalDate.now(),startTime, endTime));
        assertEquals(EciReservesException.INVALID_TIMESLOT, exception.getMessage());
    }

    @Test
    void testAddTimeSlotWithSameStartAndEnd() {
        LocalTime startTime = LocalTime.of(10, 0);
        EciReservesException exception = assertThrows(EciReservesException.class, () -> laboratory.addTimeSlot(LocalDate.now(),startTime, startTime));
        assertEquals(EciReservesException.INVALID_TIMESLOT, exception.getMessage());
    }

    @Test
    void testAddTimeSlotWithStartAfterEnd() {
        LocalTime startTime = LocalTime.of(11, 0);
        LocalTime endTime = LocalTime.of(10, 0);
        EciReservesException exception = assertThrows(EciReservesException.class, () -> laboratory.addTimeSlot(LocalDate.now(),startTime, endTime));
        assertEquals(EciReservesException.INVALID_TIMESLOT, exception.getMessage());
    }

    @Test
    void testAddDuplicateTimeSlot() throws EciReservesException {
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(11, 0);
        laboratory.addTimeSlot(LocalDate.now(), startTime, endTime);
        EciReservesException exception = assertThrows(EciReservesException.class, () -> laboratory.addTimeSlot(LocalDate.now(),startTime, endTime));
        assertEquals(EciReservesException.TIMESLOT_OVERLAPS, exception.getMessage());
    }

    @Test
    void testAddOverlappingTimeSlot() throws EciReservesException {
        laboratory.addTimeSlot(LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0));
        EciReservesException exception = assertThrows(EciReservesException.class, () -> laboratory.addTimeSlot(LocalDate.now(), LocalTime.of(9, 30), LocalTime.of(10, 30)));
        assertEquals(EciReservesException.TIMESLOT_OVERLAPS, exception.getMessage());
    }

    @Test
    void testRemoveExistingTimeSlot() throws EciReservesException {
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(11, 0);
        laboratory.addTimeSlot(LocalDate.now(), startTime, endTime);
        laboratory.removeTimeSlot(LocalDate.now(), startTime, endTime);
        assertTrue(laboratory.getTimeSlots().isEmpty());
    }

    @Test
    void testTimeSlotsAndAvailablesSync() throws EciReservesException {
        laboratory.addTimeSlot(LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0));
        laboratory.addTimeSlot(LocalDate.now(), LocalTime.of(10, 30), LocalTime.of(11, 30));
        assertEquals(2, laboratory.getTimeSlotsByDate().get(LocalDate.now()).size());
    }

    @Test
    void shouldAddValidTimeSlot() throws EciReservesException {
        laboratory.addTimeSlot(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(12, 0));
        assertEquals(1, laboratory.getTimeSlotsByDate().get(LocalDate.now()).size());
    }

    @Test
    void shouldThrowExceptionWhenTimeSlotOutOfOpeningHours() {
        EciReservesException exception = assertThrows(EciReservesException.class, () -> 
            laboratory.addTimeSlot(LocalDate.now(), LocalTime.of(7, 0), LocalTime.of(9, 0))
        );
        assertEquals(EciReservesException.INVALID_TIMESLOT, exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTimeSlotOverlaps() throws EciReservesException {
        laboratory.addTimeSlot(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(12, 0));

        EciReservesException exception = assertThrows(EciReservesException.class, () -> 
            laboratory.addTimeSlot(LocalDate.now(), LocalTime.of(11, 0), LocalTime.of(13, 0))
        );
        assertEquals(EciReservesException.TIMESLOT_OVERLAPS, exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTimeSlotAlreadyExists() throws EciReservesException {
        laboratory.addTimeSlot(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(12, 0));

        EciReservesException exception = assertThrows(EciReservesException.class, () -> 
            laboratory.addTimeSlot(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(12, 0))
        );
        assertEquals(EciReservesException.TIMESLOT_OVERLAPS, exception.getMessage());
    }

    @Test
    void shouldRemoveExistingTimeSlot() throws EciReservesException {
        laboratory.addTimeSlot(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(12, 0));
        laboratory.removeTimeSlot(LocalDate.now(),LocalTime.of(10, 0), LocalTime.of(12, 0));
        assertEquals(0, laboratory.getTimeSlots().size());
    }

    @Test
    void shouldThrowExceptionWhenRemovingNonExistentTimeSlot() {
        EciReservesException exception = assertThrows(EciReservesException.class, () -> 
            laboratory.removeTimeSlot(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(12, 0))
        );
        assertEquals(EciReservesException.TIMESLOT_NOT_FOUND, exception.getMessage());
    }

    @Test
    void shouldReplaceTimeSlotCorrectly() throws EciReservesException {
        laboratory.addTimeSlot(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(12, 0));
        laboratory.removeTimeSlot(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(12, 0), LocalTime.of(14, 0), LocalTime.of(16, 0));
        
        assertEquals(1, laboratory.getTimeSlotsByDate().get(LocalDate.now()).size());
    }

    @Test
    void shouldRestoreTimeSlotIfNewTimeSlotIsInvalid() throws EciReservesException {
        laboratory.addTimeSlot(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(12, 0));

        EciReservesException exception = assertThrows(EciReservesException.class, () -> 
            laboratory.removeTimeSlot(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(12, 0), LocalTime.of(7, 0), LocalTime.of(9, 0))
        );
        assertEquals(EciReservesException.INVALID_TIMESLOT, exception.getMessage());

        assertEquals(1, laboratory.getTimeSlotsByDate().get(LocalDate.now()).size());
    }

    @Test
    void shouldThrowExceptionWhenAddingAdjacentTimeSlot() throws EciReservesException {
        laboratory.addTimeSlot(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 0));
        EciReservesException exception = assertThrows(EciReservesException.class, () -> 
            laboratory.addTimeSlot(LocalDate.now(), LocalTime.of(10, 30), LocalTime.of(11, 30))
        );
        assertEquals(EciReservesException.TIMESLOT_OVERLAPS, exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenRemovingNonExistentDate() {
        LocalDate date = LocalDate.now().plusDays(1);
        EciReservesException exception = assertThrows(EciReservesException.class, () -> 
            laboratory.removeTimeSlot(date, LocalTime.of(10, 0), LocalTime.of(12, 0))
        );
        assertEquals(EciReservesException.TIMESLOT_NOT_FOUND, exception.getMessage());
    }
}
