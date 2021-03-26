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
    private Size size;
    private String url;

    public FormatTypeResponseDTO(FormatType f) {
        size = f.getSize();
        url = f.getUrl();
    }
}
