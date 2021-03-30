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
public class CategoryResponseDTO {
    private long id;
    private String name;

    public CategoryResponseDTO(Category category) {
        id= category.getId();;
        name= category.getName();
    }
}
