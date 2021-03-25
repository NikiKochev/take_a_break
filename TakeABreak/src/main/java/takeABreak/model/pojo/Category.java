package takeABreak.model.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@Component
public class Category {

    private long id;
    private String name;
    private List<Post> posts;
}
