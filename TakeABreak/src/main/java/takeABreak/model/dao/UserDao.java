package takeABreak.model.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import takeABreak.exceptions.NotFoundException;
import takeABreak.model.dto.user.SearchForUsersRequestDTO;
import takeABreak.model.pojo.User;
import takeABreak.service.UserService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
@NoArgsConstructor
@Setter
@Getter
public class UserDao {
    @Autowired
    private UserService userService;
    @Autowired
    DBCredentials credentials;

    public List<User> findBy(SearchForUsersRequestDTO searchDTO){
        List<User> users = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT id FROM users WHERE ");
        String firsName = searchDTO.getFirstName();
        if(firsName != null && !firsName.trim().equals("")){
            query = add(query , "first_name", searchDTO.getFirstName());
        }
        String lastName = searchDTO.getLastName();
        if(lastName!= null && !lastName.trim().equals("")){
            query = add(query, "last_name", searchDTO.getLastName());
        }
        String email = searchDTO.getEmail();
        if(email!= null && !email.trim().equals("")){
            query = add(query, "email", searchDTO.getEmail());
        }
        String city = searchDTO.getCity();
        if(city!= null && !city.trim().equals("") ){
            query = add(query, "city", city);
        }
        String country = searchDTO.getCountry();
        if(country!= null && !country.trim().equals("")){
            query = add(query, "country", searchDTO.getCountry());
        }
        if(searchDTO.getAge() != 0){
            query.append(" AND age = "+searchDTO.getAge());
        }
        query.append(" LIMIT "+ (searchDTO.getPage()*searchDTO.getPerpage() - searchDTO.getPerpage())+ ", "+ searchDTO.getPage());
        String find = query.toString();
        try(Connection connection = DriverManager.getConnection(credentials.getUrl(), credentials.getUsername(), credentials.getPassword());
            PreparedStatement statement = connection.prepareStatement(find)) {
            ResultSet result = statement.executeQuery();
            while (result.next()){
                users.add(userService.findById(result.getInt(1)));
            }
        } catch (SQLException e) {
            throw new NotFoundException("no connection"+ e.getMessage());
        }
        return users;
    }

    private StringBuilder add(StringBuilder query, String colName, String value) {
        if(query.compareTo(new StringBuilder("SELECT id FROM users WHERE ")) != 0){
            query.append(" AND ");
        }
        query.append(colName + " LIKE \"%" + value + "%\"");
        return query;
    }
}
