package takeABreak.model.pojo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "content")
public class Content {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(mappedBy = "content")
    @JsonBackReference
    private Post post;

    @ManyToOne
    @JoinColumn(name = "file_type_id")
    @JsonBackReference
    private FileType fileType;


    @OneToMany (mappedBy = "content")
    @JsonManagedReference
    private List<FormatType> formatTypes;
}
