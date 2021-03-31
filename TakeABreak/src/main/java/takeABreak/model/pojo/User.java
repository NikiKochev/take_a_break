package takeABreak.model.pojo;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import takeABreak.model.dto.user.RegisterRequestUserDTO;
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
    private boolean isAdult;

    @OneToMany (mappedBy = "user")
    @JsonManagedReference
    List<Post> posts;

    @OneToMany (mappedBy = "user")
    @JsonManagedReference
    private List<Comment> comments;

    @ManyToMany
    @JoinTable(
            name = "users_like_coments",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn (name = "coments_id")}
    )
    @JsonManagedReference
    private List<Comment> likedComments;
    @ManyToMany
    @JoinTable(
            name = "users_dislike_coments",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn (name = "coments_id")}
    )
    @JsonManagedReference
    private List<Comment> dislikedComments;

    @ManyToMany
    @JoinTable(
            name = "users_like_posts",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn (name = "post_id")}
    )
    @JsonManagedReference
    private List<Post> likedPosts;
    @ManyToMany
    @JoinTable(
            name = "users_dislike_posts",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn (name = "post_id")}
    )
    @JsonManagedReference
    private List<Post> dislikedPosts;

    public User(RegisterRequestUserDTO dto){
        firstName = dto.getFirstName();
        lastName = dto.getLastName();
        email = dto.getEmail();
        password = dto.getPassword();
        age = dto.getAge();
        createdAt = LocalDate.now();
        isAdult = false;

    }



}
