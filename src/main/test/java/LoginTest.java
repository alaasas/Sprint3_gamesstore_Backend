import com.database.DatabaseConnection;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class LoginTest extends Mockito {

    Login servlet= new Login();

    @Test
    void doPost() throws ServletException, IOException, SQLException, ClassNotFoundException {
        // mock request and response
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // initialize the database
        Connection con = DatabaseConnection.initializeDatabase();

        // test 1 - if one (or more) of the params are incorrect
        request.addParameter("emailid", "");
        request.addParameter("password", "");

        servlet.doPost(request, response);

        assertEquals("Email/Password is incorrect. Enter valid credentials.", response.getContentAsString());

        // test 2 - test@gmail.com is already in database. Successful Login

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        request.addParameter("emailid", "test@test.com");
        request.addParameter("password", "test");

        servlet.doPost(request, response);

        assertEquals("Login Successful", response.getContentAsString());

    }
}