package takeABreak.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import takeABreak.model.pojo.User;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class SearchForUsersResponseDTO {

    private List<User> users;

}
