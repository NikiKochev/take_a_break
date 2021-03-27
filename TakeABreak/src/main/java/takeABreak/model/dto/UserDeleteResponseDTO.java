package takeABreak.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import takeABreak.model.pojo.User;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class UserDeleteResponseDTO {

    private String msg;

    public UserDeleteResponseDTO(User user) {
        msg = "User deleted at: "+ user.getDeletedAt();
    }
}
