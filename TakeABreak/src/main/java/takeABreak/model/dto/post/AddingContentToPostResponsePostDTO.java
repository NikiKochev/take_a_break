package takeABreak.model.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import takeABreak.model.dto.user.AddingResponseContentDTO;
import takeABreak.model.pojo.Content;
import takeABreak.model.pojo.FormatType;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Component
public class AddingContentToPostResponsePostDTO {

    private List<AddingResponseContentDTO> contentDTOList;

    public AddingContentToPostResponsePostDTO(Content content) {
        contentDTOList = new ArrayList<>();
        for (FormatType f : content.getFormatTypes()){
            contentDTOList.add(new AddingResponseContentDTO(content.getId(), content.getFileType(),f.getSize(),f.getUrl()));
        }
    }
    /*[
    {
    contentId : 5,
    fileTypeId: 4,
    sizeId : 1,
    url : "kljdsa;ljd;la"
    }
    {
    ....
    }
    ]*/
}
