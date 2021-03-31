package takeABreak.model.pojo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String description;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "content_id", referencedColumnName = "id")
    @JsonManagedReference
    private Content content;

    @ManyToOne
    @JoinColumn(name = "categories_id")
    @JsonBackReference
    private Category category;
    private LocalDate createdAt;
    private boolean isAdultContent;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @JsonBackReference
    private User user;

    @OneToMany (mappedBy = "post")
    @JsonManagedReference
    private List<Comment> commentList;


    @ManyToMany(mappedBy = "likedPosts")
    @JsonBackReference
    private List<User> likers;

    @ManyToMany(mappedBy = "dislikedPosts")
    @JsonBackReference
    private List<User> dislikers;

}
