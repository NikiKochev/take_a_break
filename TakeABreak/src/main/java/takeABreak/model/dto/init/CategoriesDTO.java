package takeABreak.model.dto.init;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Setter
@Getter
@NoArgsConstructor
public class CategoriesDTO {

    public static class Category{
        public String name;
    }

    ArrayList<CategoriesDTO.Category> categories;
}
