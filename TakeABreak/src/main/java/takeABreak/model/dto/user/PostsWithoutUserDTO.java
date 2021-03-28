package takeABreak.model.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import takeABreak.model.dto.CategoryResponseDTO;
import takeABreak.model.dto.ContentResponseDTO;
import takeABreak.model.pojo.Post;

import java.time.LocalDate;


@Component
@NoArgsConstructor
@Setter
@Getter
public class PostsWithoutUserDTO {
    private int id;
    private String title;
    private String description;
    private LocalDate createdAt;
    private CategoryResponseDTO category;
    private ContentResponseDTO content;

    public PostsWithoutUserDTO(Post p){
        id= p.getId();
        title = p.getTitle();
        description = p.getDescription();
        createdAt = p.getCreatedAt();
        category = new CategoryResponseDTO(p.getCategory());
        content = new ContentResponseDTO(p.getContent());
    }
}
