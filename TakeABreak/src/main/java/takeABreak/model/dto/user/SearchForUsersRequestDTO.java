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
public class SearchForUsersRequestDTO {

    private String firstName;
    private String lastName;
    private String email;
    private int age;
    private String city;
    private String country;
    /*{
   "firstName":"Georgi",
    "lastName":"Ivanov",
    "email":"georgiivanov12@gmail.com",
    "age":15,
    "city":"Alabala123",
    "country":"Alabala123"
}*/
}
