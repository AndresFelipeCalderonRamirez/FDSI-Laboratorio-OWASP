package edu.eci.cvds.ecireserves.dto;

import edu.eci.cvds.ecireserves.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private String id;
    private String name;
    private String email;
    private String password;
    private UserRole rol;
}
