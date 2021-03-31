package takeABreak.model.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import takeABreak.exceptions.NotFoundException;
import takeABreak.model.dto.user.SearchForUsersRequestDTO;
import takeABreak.model.pojo.User;
import takeABreak.model.repository.UserRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
@NoArgsConstructor
@Setter
@Getter
public class UserDao {
    @Autowired
    private UserRepository repository;

    public List<User> findBy(SearchForUsersRequestDTO searchDTO){
        List<User> users = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT id FROM users WHERE ");
        if(searchDTO.getFirstName() != null){
            query = add(query , "first_name", searchDTO.getFirstName());
        }
        if(searchDTO.getLastName() != null){
            query = add(query, "last_name", searchDTO.getLastName());
        }
        if(searchDTO.getEmail() != null){
            query = add(query, "email", searchDTO.getEmail());
        }
        if(searchDTO.getCity() != null){
            query = add(query, "city", searchDTO.getCity());
        }
        if(searchDTO.getCountry() != null){
            query = add(query, "country", searchDTO.getCountry());
        }
        if(searchDTO.getAge() != 0){
            query.append(" AND age = "+searchDTO.getAge());
        }
        String find = query.toString();
        System.out.println(find);
        try(Connection connection = DriverManager.getConnection("jdbc:mysql://84.238.145.199:7777/takeabreak", "cadet", "survivor2021");
            PreparedStatement statement = connection.prepareStatement(find)) {
            System.out.println("да видя на кой ред гърми 1");
            ResultSet result = statement.executeQuery();
            System.out.println("да видя на кой ред гърми 2");
            result.next();
            System.out.println("да видя на кой ред гърми 3");
            while (result.next()){
                users.add(repository.findById(result.getInt("id")).get());
            }
        } catch (SQLException e) {
            throw new NotFoundException("no connection"+ e.getMessage());
        }
        return users;
    }

    private StringBuilder add(StringBuilder query, String colName, String value) {
        System.out.println(colName + " : " + value);
        if(query.compareTo(new StringBuilder("SELECT id FROM users WHERE ")) != 0){
            query.append(" AND ");
        }
        query.append(colName + " = " + value);
        return query;
    }
}
