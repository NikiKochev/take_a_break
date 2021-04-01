package takeABreak.model.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import takeABreak.model.pojo.User;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Component
public class SearchForUsersResponseDTO {

    private List<UserResponseDTO> users;

    public SearchForUsersResponseDTO (List<User> users) {
        this.users = new ArrayList<>();
        for (User u : users){
            this.users.add(new UserResponseDTO(u));
        }
    }
}
