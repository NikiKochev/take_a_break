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
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String content;
    private LocalDate createdAt;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(cascade={CascadeType.ALL})
    @JoinColumn(name="parent_id")
    private Comment parent;

    @OneToMany(mappedBy="parent")
    @JsonManagedReference
    private List<Comment> subComments;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User user;

    @ManyToMany(mappedBy = "likedComments")
    @JsonBackReference
    private List<User> likers;
    @ManyToMany(mappedBy = "dislikedComments")
    @JsonBackReference
    private List<User> dislikers;

}
