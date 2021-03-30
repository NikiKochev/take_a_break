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
public class CountriesDTO {

    public static class Country{
        public String name;
        public String code;
    }

    ArrayList<Country> countries;
}
