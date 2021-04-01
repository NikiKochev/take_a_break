package takeABreak.model.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import takeABreak.model.pojo.Post;

import java.util.List;

@Component
@Getter
@Setter
@NoArgsConstructor
public class SearchResponsePostDTO {

    private List<AddingResponsePostDTO> posts;

    public SearchResponsePostDTO(List<Post> posts) {
        for (Post p : posts){
            this.posts.add(new AddingResponsePostDTO(p));
        }
    }
}
