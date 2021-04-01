package takeABreak.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import takeABreak.model.dto.categorory.AllCategoryResponseDTO;
import takeABreak.service.CategoryService;

@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public AllCategoryResponseDTO getAllCategories(){
        return categoryService.getAll();
    }
}
