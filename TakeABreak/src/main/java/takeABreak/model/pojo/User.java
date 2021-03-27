package takeABreak.model.pojo;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import takeABreak.model.dto.RegisterRequestUserDTO;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String verification;
    private int age;
    private String password;
    private LocalDate createdAt;
    private LocalDate deletedAt;
    private String country;
    private String city;
    private String avatar; // url of avatar

    @OneToMany (mappedBy = "user")
    @JsonManagedReference
    List<Post> posts;

    @OneToMany (mappedBy = "user")
    @JsonManagedReference
    private List<Comment> comments;

    public User(RegisterRequestUserDTO dto){
        firstName = dto.getFirstName();
        lastName = dto.getLastName();
        email = dto.getEmail();
        password = dto.getPassword();
        age = dto.getAge();
        createdAt = LocalDate.now();
    }

}
