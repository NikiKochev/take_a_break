package takeABreak.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import takeABreak.model.pojo.Content;
import takeABreak.model.pojo.FormatType;

import java.util.ArrayList;
import java.util.List;

@Component
@NoArgsConstructor
@Setter
@Getter
public class ContentResponseDTO {

    private long id;
    private FileTypeResponseDTO fileType;
    private List<FormatTypeResponseDTO> formatTypes;

    public ContentResponseDTO(Content content) {
        id = content.getId();
        fileType = new FileTypeResponseDTO(content.getFileType());
        formatTypes = new ArrayList<>();
        for (FormatType f : content.getFormatTypes()){
            formatTypes.add(new FormatTypeResponseDTO(f));
        }
    }
}
