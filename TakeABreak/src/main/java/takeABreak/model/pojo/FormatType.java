package takeABreak.model.pojo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

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
    @JsonBackReference
    private Size size;

    private String url;

    public FormatType(Content content, String absolutePath, Size byId) {
        size = byId;
    }

        @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FormatType that = (FormatType) o;
        return Objects.equals(content, that.content) &&
                Objects.equals(size, that.size) &&
                Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, size, url);
    }
}
