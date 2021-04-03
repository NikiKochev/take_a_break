package takeABreak.model.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import takeABreak.exceptions.InternalServerErrorException;

import java.sql.*;

@Component
@NoArgsConstructor
@Setter
@Getter
public class FormatTypeDAO {

    @Autowired
    private DBCredentials credentials;

    public void saveFormatType(int sizeId, String url, int contentId) {

        String sqlQuery = "INSERT INTO format_type (size, url, content_id) values (?, ?, ?)";

        try(Connection connection = DriverManager.getConnection(credentials.getUrl(), credentials.getUsername(), credentials.getPassword());
            PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setInt(1, sizeId);
            statement.setString(2, url);
            statement.setInt(3, contentId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException("The server experienced some difficulties, try again later.");
        }
    }

}
