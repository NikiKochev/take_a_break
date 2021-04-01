package takeABreak.model.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import takeABreak.model.pojo.FileType;

@Component
@NoArgsConstructor
@Setter
@Getter
public class FileTypeResponseDTO {

    private long id;
    private String name;

    public FileTypeResponseDTO(FileType fileType){
        id= fileType.getId();
        name = fileType.getType();
    }
}
