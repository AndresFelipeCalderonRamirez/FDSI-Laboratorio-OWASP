package edu.eci.cvds.ecireserves.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import edu.eci.cvds.ecireserves.enums.LaboratoryStatus;
import edu.eci.cvds.ecireserves.exception.EciReservesException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "laboratories")
public class Laboratory {
    @Id
    private String id;

    private String classroom;
    private String name;
    private int capacity;
    private String description;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private LaboratoryStatus status;
    private List<TimeSlot> timeSlots = new ArrayList<>();
    private Map<LocalDate, List<TimeSlot>> timeSlotsByDate = new HashMap<>();

    /**
     * Add a  valid time slot to the laboratory and set it as unavailable
     * @param startTime
     * @param endTime
     * @throws EciReservesException 
    */
    public void addTimeSlot(LocalDate date, LocalTime startTime, LocalTime endTime) throws EciReservesException {
        if (startTime.isBefore(openingTime) || endTime.isAfter(closingTime) || !startTime.isBefore(endTime)) {
            throw new EciReservesException(EciReservesException.INVALID_TIMESLOT);
        }

        List<TimeSlot> slotsForDate = timeSlotsByDate.computeIfAbsent(date, k -> new ArrayList<>());
        for (TimeSlot slot : slotsForDate) {
            if (startTime.isBefore(slot.getEndTime()) && endTime.isAfter(slot.getStartTime())) {
                throw new EciReservesException(EciReservesException.TIMESLOT_OVERLAPS);
            }
        }

        slotsForDate.add(new TimeSlot(startTime, endTime));
    }

    /**
     * Remove a time slot from the laboratory and add a new one
     * @param startTime
     * @param endTime
     * @param newStartTime
     * @param newEndTime
     * @throws EciReservesException
     */
    public void removeTimeSlot(LocalDate date, LocalTime startTime, LocalTime endTime, LocalTime newStartTime, LocalTime newEndTime) throws EciReservesException {
        List<TimeSlot> slotsForDate = timeSlotsByDate.get(date);
        if (slotsForDate == null) {
            throw new EciReservesException(EciReservesException.TIMESLOT_NOT_FOUND);
        }

        TimeSlot removedSlot = null;
        int removedIndex = -1;
    
        for (int i = 0; i < slotsForDate.size(); i++) {
        TimeSlot slot = slotsForDate.get(i);
        if (slot.getStartTime().equals(startTime) && slot.getEndTime().equals(endTime)) {
            removedSlot = slot;
            removedIndex = i;
            slotsForDate.remove(i);
            break;
        }
    }
    
        if (removedSlot == null) {
            throw new EciReservesException(EciReservesException.TIMESLOT_NOT_FOUND);
        }
    
        try {
            addTimeSlot(date, newStartTime, newEndTime);
        } catch (EciReservesException e) {
            slotsForDate.add(removedIndex, removedSlot);
            throw e;
        }
    }
    
    /**
     * Remove a time slot from the laboratory
     * @param startTime
     * @param endTime
     * @throws EciReservesException
     */
    public void removeTimeSlot(LocalDate date, LocalTime startTime, LocalTime endTime) throws EciReservesException {
        List<TimeSlot> slotsForDate = timeSlotsByDate.get(date);
        
        if (slotsForDate == null) {
            throw new EciReservesException(EciReservesException.TIMESLOT_NOT_FOUND);
        }
    
        for (int i = 0; i < slotsForDate.size(); i++) {
            TimeSlot slot = slotsForDate.get(i);
            if (slot.getStartTime().equals(startTime) && slot.getEndTime().equals(endTime)) {
                slotsForDate.remove(i);
                if (slotsForDate.isEmpty()) {
                    timeSlotsByDate.remove(date);
                }
                return;
            }
        }
    
        throw new EciReservesException(EciReservesException.TIMESLOT_NOT_FOUND);
    }
    
    
}