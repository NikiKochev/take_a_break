package takeABreak.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class UploadAvatarDTO {

    private String url;
    private int ownerId;

    /*
    {
    image:"url"
    ownerId:5
    }
    */
}
