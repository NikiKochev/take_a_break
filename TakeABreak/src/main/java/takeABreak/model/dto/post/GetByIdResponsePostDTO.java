package takeABreak.model.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import takeABreak.model.dto.ContentResponseDTO;
import takeABreak.model.dto.categorory.SingleCategoryResponseDTO;
import takeABreak.model.dto.comments.EditRequestCommentDTO;
import takeABreak.model.dto.user.UserResponseDTO;
import takeABreak.model.pojo.Comment;
import takeABreak.model.pojo.Post;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@Getter
@Setter
@NoArgsConstructor
public class GetByIdResponsePostDTO {

    private int id;
    private String title;
    private String description;
    private LocalDate createdAt;
    private SingleCategoryResponseDTO category;
    private UserResponseDTO user;
    private List<EditRequestCommentDTO> commentList;
    private ContentResponseDTO content;
    private int likers;
    private int dislikers;

    public GetByIdResponsePostDTO(Post post) {
        commentList = new ArrayList<>();
        id= post.getId();
        title = post.getTitle();
        description = post.getDescription();
        createdAt = post.getCreatedAt();
        category = new SingleCategoryResponseDTO(post.getCategory());
        user = new UserResponseDTO(post.getUser());
        content = new ContentResponseDTO(post.getContent());
        likers = post.getLikers().size();
        dislikers = post.getDislikers().size();
        for (Comment c : post.getCommentList()){
            commentList.add(new EditRequestCommentDTO(c));
        }

    }
}
