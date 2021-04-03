package takeABreak.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import takeABreak.model.pojo.FormatType;
import takeABreak.model.pojo.Size;

@Component
@NoArgsConstructor
@Setter
@Getter
public class FormatTypeResponseDTO {
    private String size;
    private String url;

    public FormatTypeResponseDTO(FormatType f) {
        size = f.getSize().getSize();
        url = f.getUrl();
    }
}
