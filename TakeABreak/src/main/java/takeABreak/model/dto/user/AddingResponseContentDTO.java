package takeABreak.model.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import takeABreak.model.pojo.FileType;
import takeABreak.model.pojo.Size;

@NoArgsConstructor
@Getter
@Setter
@Component
public class AddingResponseContentDTO {

    private int contentId;
    private int fileTypeId;
    private int sizeId;
    private String url;

    public AddingResponseContentDTO(int id, FileType fileType, Size size, String url) {
        contentId = id;
        fileTypeId = fileType.getId();
        sizeId = size.getId();
        this.url = url;

    }
}
