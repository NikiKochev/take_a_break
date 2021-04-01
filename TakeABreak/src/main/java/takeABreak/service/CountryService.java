package takeABreak.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import takeABreak.exceptions.BadRequestException;
import takeABreak.model.pojo.Country;
import takeABreak.model.repository.CountryRepository;

import java.util.Optional;

@Service
public class CountryService {
    @Autowired
    private CountryRepository countryRepository;

    public Country findById(int countryId) {
        Optional<Country> country = countryRepository.findById(countryId);
        if(country.isPresent()){
            return country.get();
        }
        throw  new BadRequestException("No such country");
    }
}
