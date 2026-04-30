package edu.eci.cvds.ecireserves.dto;

import java.time.LocalTime;

import edu.eci.cvds.ecireserves.enums.LaboratoryStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LaboratoryDTO {
    private String id;
    private String classroom;
    private String name;
    private int capacity;
    private String description;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private LaboratoryStatus status;
}
