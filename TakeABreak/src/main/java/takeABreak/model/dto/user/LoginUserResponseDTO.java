package takeABreak.model.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import takeABreak.model.dto.CountryResponseDTO;
import takeABreak.model.pojo.Country;
import takeABreak.model.pojo.Post;
import takeABreak.model.pojo.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@NoArgsConstructor
@Setter
@Getter
public class LoginUserResponseDTO {
    private int id;
    private String firstName;
    private String lastName;
    private int age;
    private String avatar;
    private LocalDate createdAt;
    private CountryResponseDTO country;
    private String city;
    private List<PostsWithoutUserDTO> posts;


    public LoginUserResponseDTO(User user){
        id= user.getId();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        age= user.getAge();
        createdAt = user.getCreatedAt();
        if(user.getCountry() != null) {
            country = new CountryResponseDTO(user.getCountry());
        }
        avatar = user.getAvatar();
        city = user.getCity();
        posts = new ArrayList<>();
        for (Post p : user.getPosts()){
            posts.add(new PostsWithoutUserDTO(p));
        }

    }
}
