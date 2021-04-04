package takeABreak.model.pojo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "format_type")
@IdClass(FormatTypeId.class)
public class FormatType  {

    @Id
    @ManyToOne
    @JoinColumn(name = "content_id")
    @JsonBackReference
    private Content content;

    @Id
    @ManyToOne
    @JoinColumn(name = "size")
    private Size size;

    private String url;

}
