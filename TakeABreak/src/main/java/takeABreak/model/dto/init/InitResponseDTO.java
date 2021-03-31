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
    // todo този клас трябва да го премахнем преди презентацията
    private String entityType;
    private int enteredEntities;

}
