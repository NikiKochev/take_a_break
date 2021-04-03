package takeABreak.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import takeABreak.model.dto.init.InitResponseDTO;
import takeABreak.service.InitService;

@RestController
public class DbInitController extends AbstractController{
//    // todo този клас трябва да го премахнем преди презентацията
//    @Autowired
//    InitService initService;
//
//    @Autowired
//    InitResponseDTO initResponseDTO;
//
//    @GetMapping("/init/countries")
//    public InitResponseDTO initCountriesInDb(){
//        return initService.initCountries();
//    }
//
//
//    @GetMapping("/init/categories")
//    public InitResponseDTO initCategoriesInDb(){
//        return initService.initCategories();
//    }
}
