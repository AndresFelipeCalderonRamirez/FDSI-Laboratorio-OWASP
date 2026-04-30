package edu.eci.cvds.ecireserves.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDTO {

    private String id;
    private String userId;
    private String laboratoryId;
    private LocalDate date;
    private LocalTime startTime;
    private Integer duration;
    private LocalTime endTime;
    private LocalTime newStartTime;
    private Integer newDuration;
    private String purpose;

    public ReservationDTO(String id, String userId, String laboratoryId, LocalDate date, LocalTime startTime, Integer duration, String purpose) {
        this.id = id;
        this.userId = userId;
        this.laboratoryId = laboratoryId;
        this.date = date;
        this.startTime = startTime;
        this.duration = duration;
        this.purpose = purpose;
    }
}
