package takeABreak.model.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Email;

@Setter
@Getter
@NoArgsConstructor
@Component
public class RegisterRequestUserDTO {

    private String firstName; // can be null
    private String lastName; // can be null
    @Email(message = "Is not a valid email")
    private String email;
    private int age;
    private String password;
    private String confirmPassword;

}
