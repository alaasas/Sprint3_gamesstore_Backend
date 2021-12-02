import com.database.DatabaseConnection;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class PostOrdersTest extends Mockito {

    PostOrders servlet = new PostOrders();

    @Test
    void doPost() throws ServletException, IOException, SQLException, ClassNotFoundException {
        // mock request and response
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        //Adding game to the orders table
        Connection con = DatabaseConnection.initializeDatabase();
        Statement stmt = con.createStatement();
        stmt.executeUpdate("delete from orders where user_id='test@test.com' and order_id = 'test1'");
        stmt.executeUpdate("insert into orders values ('test1','test@test.com','Super Smash Bros',20)");

        // test 1 - if game is already in cart (game is already in orders table)

        request.addParameter("order_id", "test1");
        request.addParameter("user_id", "test@test.com");
        request.addParameter("gamename", "Super Smash Bros");
        request.addParameter("fees", "20");

        servlet.doPost(request, response);

        assertEquals("You already checkout this order!", response.getContentAsString());

        // test 2 - success scenario
        stmt.executeUpdate("delete from orders where user_id='test@test.com' and order_id = 'test1'");

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        request.addParameter("order_id", "test1");
        request.addParameter("user_id", "test@test.com");
        request.addParameter("gamename", "Super Smash Bros");
        request.addParameter("fees", "20");

        servlet.doPost(request, response);

        assertEquals("Successfully Checked out", response.getContentAsString());
    }
}