package takeABreak.model.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Setter
@Getter
@NoArgsConstructor
@Component
public class RegisterRequestUserDTO {

    private String firstName;
    private String lastName;

    @NotEmpty
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}")
    private String email;
    private int age;
    @NotEmpty
    @Pattern(regexp = "((?=.*[a-z])(?=.*\\d)(?=.*[A-Z]).{8,40})")
    @Size(min = 8,max = 40, message = "password must be at least 6 characters an to have one upper letter, one lower letter and one digit" )
    private String password;
    @NotEmpty
    @Size(min = 8,max = 40, message = "password must be at least 6 characters an to have one upper letter, one lower letter and one digit" )
    private String confirmPassword;
    private boolean isAdult;

}
