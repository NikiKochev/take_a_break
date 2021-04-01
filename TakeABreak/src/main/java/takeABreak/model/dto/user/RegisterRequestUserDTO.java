package takeABreak.model.dto.user;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@NoArgsConstructor
@Component
public class RegisterRequestUserDTO {

    private String firstName; // can be null
    private String lastName; // can be null
    private String email;
    private int age;
    private String password;
    private String confirmPassword;
    private boolean isAdult;

}
