package takeABreak.model.dto.categorory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import takeABreak.model.pojo.Category;

@Component
@NoArgsConstructor
@Setter
@Getter
public class SingleCategoryResponseDTO {
    private int id;
    private String name;

    public SingleCategoryResponseDTO(Category category) {
        id= category.getId();;
        name= category.getName();
    }
}
