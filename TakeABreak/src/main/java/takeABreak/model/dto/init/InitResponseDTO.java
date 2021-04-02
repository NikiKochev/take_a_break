package takeABreak.model.dto.init;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@Getter
@Setter
public class InitResponseDTO {
    private String entityType;
    private int enteredEntities;

}
