package takeABreak.service;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import takeABreak.exceptions.InitException;
import takeABreak.model.dto.init.CategoriesDTO;
import takeABreak.model.dto.init.CountriesDTO;
import takeABreak.model.dto.init.InitResponseDTO;
import takeABreak.model.pojo.Category;
import takeABreak.model.pojo.Country;
import takeABreak.model.repository.CategoryRepository;
import takeABreak.model.repository.CountryRepository;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class InitService {
// todo този клас трябва да го премахнем преди презентацията
    private Gson gson = new Gson();

    @Autowired
    CountriesDTO countriesDTO;
    @Autowired
    CategoriesDTO categoriesDTO;
    @Autowired
    CountryRepository countryRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    InitResponseDTO initResponseDTO;

    public InitResponseDTO initCountries(){
        if(countryRepository.findAll().size()>0){
            throw new InitException("In the DB are already country entities. The country init method works only with empty DB table.");
        }

        List<Country> listCountries = new ArrayList<>();

        try(FileReader fileReader = new FileReader(
                "TakeABreak" + File.separator + "src" + File.separator + "main" + File.separator + "resources" +
                        File.separator + "initFiles" + File.separator + "allCountries.json")){

            CountriesDTO countries = gson.fromJson(fileReader, CountriesDTO.class);

            for(CountriesDTO.Country currentCountry: countries.getCountries()){
                Country country = new Country();
                country.setName(currentCountry.name);
                listCountries.add(country);
            }
            countryRepository.saveAll(listCountries);

        } catch (Exception e) {
            e.printStackTrace();
            throw new InitException("Server Error. Cannot write countries into the DB.");
        }

        initResponseDTO.setEntityType("Countries");
        initResponseDTO.setEnteredEntities(listCountries.size());
        return initResponseDTO;

    }

    public InitResponseDTO initCategories() {
        if(categoryRepository.findAll().size()>0){
            throw new InitException("In the DB are already category entities. The categories init method works only with empty DB table.");
        }

        List<Category> listCategories = new ArrayList<>();
        try(FileReader fileReader = new FileReader(
                "TakeABreak" + File.separator + "src" + File.separator + "main" + File.separator + "resources" +
                        File.separator + "initFiles" + File.separator + "allCategories.json")){

            CategoriesDTO categories = gson.fromJson(fileReader, CategoriesDTO.class);

            for(CategoriesDTO.Category currentCategory : categories.getCategories()){
                Category category = new Category();
                category.setName(currentCategory.name);
                listCategories.add(category);
            }
            categoryRepository.saveAll(listCategories);

        } catch (Exception e) {
            e.printStackTrace();
            throw new InitException("Server Error. Cannot write categories into the DB.");
        }

        initResponseDTO.setEntityType("Category");
        initResponseDTO.setEnteredEntities(listCategories.size());
        return initResponseDTO;
    }
}
