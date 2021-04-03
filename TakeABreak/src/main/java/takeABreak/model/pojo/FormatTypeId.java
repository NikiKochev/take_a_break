package takeABreak.model.pojo;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class FormatTypeId implements Serializable {

    private Content content;

    private Size size;
}
