package edu.eci.cvds.ecireserves.model;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TimeSlot {
    private LocalTime startTime;
    private LocalTime endTime;
}
