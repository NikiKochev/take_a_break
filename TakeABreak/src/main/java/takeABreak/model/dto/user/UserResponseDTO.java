package takeABreak.model.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import takeABreak.model.pojo.Country;
import takeABreak.model.pojo.User;

import java.time.LocalDate;

@Component
@NoArgsConstructor
@Setter
@Getter
public class UserResponseDTO {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private int age;
    private String avatar;
    private LocalDate createdAt;
    private Country country;
    private String city;

    public UserResponseDTO(UserResponseDTO user) {
        id = user.getId();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        email = user.getEmail();
        age = user.getAge();
        avatar = user.getAvatar();
        createdAt = user.getCreatedAt();
        country = user.getCountry();
        city = user.getCity();
    }

    public UserResponseDTO(User user) {
        id = user.getId();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        email = user.getEmail();
        age = user.getAge();
        avatar = user.getAvatar();
        createdAt = user.getCreatedAt();
        country = user.getCountry();
        city = user.getCity();
    }
}
