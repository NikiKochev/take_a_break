package takeABreak.model.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Setter
@Getter
@Component
public class MediaStorageLocation {

    private Content content;
    private Size size;
    private String url;
}
