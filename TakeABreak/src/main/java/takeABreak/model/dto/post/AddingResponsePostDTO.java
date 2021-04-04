package takeABreak.model.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import takeABreak.model.dto.categorory.SingleCategoryResponseDTO;
import takeABreak.model.dto.user.UserResponseDTO;
import takeABreak.model.pojo.Content;
import takeABreak.model.pojo.Post;

import java.time.LocalDate;

@Component
@NoArgsConstructor
@Setter
@Getter
public class AddingResponsePostDTO {
    private int id;
    private String title;
    private String description;
    private LocalDate createdAt;
    private SingleCategoryResponseDTO category;
    private boolean isAdultContent;
    private UserResponseDTO user;
    private Content content;


    public AddingResponsePostDTO(Post post) {

        id = post.getId();
        title = post.getTitle();
        description = post.getDescription();;
        createdAt = post.getCreatedAt();
        category = new SingleCategoryResponseDTO(post.getCategory());
        isAdultContent = post.isAdultContent();
        user = new UserResponseDTO(post.getUser());
        content = post.getContent();

    }
}
