package takeABreak.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import takeABreak.model.pojo.Country;

@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {
}
