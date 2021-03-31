package takeABreak.model.dto.categorory;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import takeABreak.model.pojo.Category;

import java.util.ArrayList;
import java.util.List;

@Component
@NoArgsConstructor
@Setter
@Getter
public class AllCategoryResponseDTO {

    private List<SingleCategoryResponseDTO> categories;

    public AllCategoryResponseDTO(List<Category> categories) {
        this.categories = new ArrayList<>();
        for (Category c : categories){
            this.categories.add(new SingleCategoryResponseDTO(c));
        }
    }
}
