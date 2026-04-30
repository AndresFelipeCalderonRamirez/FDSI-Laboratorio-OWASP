package edu.eci.cvds.ecireserves.model;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import edu.eci.cvds.ecireserves.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "reservations")
public class Reservation {
    @Id
    private String id;
    
    private String userId;
    private String laboratoryId;
    private LocalDate date;
    private LocalTime startTime;
    private Integer duration;
    private LocalTime endTime;
    private String purpose;
    private ReservationStatus status;
}