package takeABreak.model.pojo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "content")
public class Content {


    @Id
    private int id;

    @JsonIgnore
    private String session;

    private LocalDateTime createdAt;

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
