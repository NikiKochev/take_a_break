package takeABreak.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import takeABreak.exceptions.NotFoundException;
import takeABreak.model.dto.categorory.AllCategoryResponseDTO;
import takeABreak.model.pojo.Category;
import takeABreak.model.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public AllCategoryResponseDTO getAll() {
        List<Category> categories = categoryRepository.findAll();
        return new AllCategoryResponseDTO(categories);
    }

    public Category findById(int categoryId) {
        Optional<Category> cat =categoryRepository.findById(categoryId);
        if(!cat.isPresent()){
            throw new NotFoundException("Not such a category");
        }
        return cat.get();
    }

}
