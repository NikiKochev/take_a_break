package takeABreak.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import takeABreak.model.pojo.Country;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class SearchForUsersRequestDTO {

    private String firstName;
    private String lastName;
    private String email;
    private Integer age;
    private String city;
    private Integer country;
    private int page;
    private int perpage;
    /*{
   "firstName":"Georgi",
    "lastName":"Ivanov",
    "email":"georgiivanov12@gmail.com",
    "age":15,
    "city":"Alabala123",
    "country":"Alabala123"
}*/
}
