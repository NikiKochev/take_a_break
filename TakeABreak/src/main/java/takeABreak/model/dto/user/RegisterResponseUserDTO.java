package takeABreak.model.dto.user;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import takeABreak.model.pojo.User;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@Component
public class RegisterResponseUserDTO {

    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private int age;
    private LocalDate createdAt;


    public RegisterResponseUserDTO(User user) {
        id = user.getId();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        email = user.getEmail();
        age = user.getAge();
        createdAt = user.getCreatedAt();
    }
}
