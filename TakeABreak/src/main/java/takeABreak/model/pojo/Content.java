package takeABreak.model.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@Component
public class Content {

    private long id;
    private FileType fileType;
    private List<MediaStorageLocation> mediaStorageLocations;
}
