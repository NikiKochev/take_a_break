package takeABreak.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class EditResponseUserDTO {

    private String firstName;
    private String lastName;
    private String email;
    private int age;
    private String password;
    private String country;
    private String city;

}
