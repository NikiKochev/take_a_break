package takeABreak.model.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import takeABreak.model.dto.CountryResponseDTO;
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
    private int age;
    private String avatar;
    private LocalDate createdAt;
    private CountryResponseDTO country;
    private String city;

    public UserResponseDTO(User user) {
        id = user.getId();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        age = user.getAge();
        avatar = user.getAvatar();
        createdAt = user.getCreatedAt();
        if(user.getCountry() != null) {
            country = new CountryResponseDTO(user.getCountry());
        }
        city = user.getCity();
    }
}
