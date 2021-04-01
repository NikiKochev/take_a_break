package takeABreak.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import takeABreak.model.pojo.Country;

@Component
@NoArgsConstructor
@Setter
@Getter
public class CountryResponseDTO {
    private int id;
    private String name;

    public CountryResponseDTO(Country country) {
        id= country.getId();
        name = country.getName();
    }
}
