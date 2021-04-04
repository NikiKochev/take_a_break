package takeABreak.model.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import takeABreak.exceptions.BadRequestException;
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
    private DBCredentials credentials;
    private boolean isItFirst = true;

    public List<User> findBy(SearchForUsersRequestDTO searchDTO){
        StringBuilder query = new StringBuilder("SELECT id FROM users WHERE ");
        query = checkString("first_name",query,searchDTO.getFirstName());
        query = checkString("last_name",query,searchDTO.getLastName());
        query = checkString("email",query,searchDTO.getEmail());
        query = checkString("city",query,searchDTO.getCity());
        query = checkInt("country", query,searchDTO.getCountry());
        query = checkInt("age",query,searchDTO.getAge());
        query.append(" LIMIT "+ (searchDTO.getPage()*searchDTO.getPerpage() - searchDTO.getPerpage())+ ", "+ searchDTO.getPerpage());
        if(isItFirst){
            throw new BadRequestException("no parameters");
        }
        isItFirst = true;
       return find(query.toString());
    }

    public StringBuilder checkInt(String colName, StringBuilder query, Integer name){
        if(name != null && name > 0){
            query.append((isItFirst ? " " : " AND ")+colName+" = "+ name);
            isItFirst = false;
        }
        return query;
    }

    public StringBuilder checkString(String colName, StringBuilder query, String name){
         if(name != null && !name.trim().equals("")){
             query = add(query, colName, name);
             isItFirst = false;
         }
         return query;
     }

    public List<User> find(String find) {
        List<User> users = new ArrayList<>();
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

    public StringBuilder add(StringBuilder query, String colName, String value) {
        if(!isItFirst){
            query.append(" AND ");
        }
        query.append(colName + " LIKE \"%" + value + "%\"");
        return query;
    }
}
