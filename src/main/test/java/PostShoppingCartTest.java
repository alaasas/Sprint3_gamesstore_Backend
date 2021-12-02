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

class PostShoppingCartTest extends Mockito {

    PostShoppingCart servlet = new PostShoppingCart();

    @Test
    void doPost() throws ServletException, IOException, SQLException, ClassNotFoundException {
        // mock request and response
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        //Adding game to the cart
        Connection con = DatabaseConnection.initializeDatabase();
        Statement stmt = con.createStatement();
        stmt.executeUpdate("delete from shoppingcart where idshoppingCart='test@test.com'");
        stmt.executeUpdate("insert into orders values('test2','test@test.com','Super Smash Bros',20)");

        // test 1 - if game is already in cart (game is already in shoppingCart table)

        request.addParameter("idshoppingCart", "test@test.com");
        request.addParameter("gamename", "Super Smash Bros");
        request.addParameter("fees", "20");

        servlet.doPost(request, response);

        assertEquals("Game is already in cart/checked out!", response.getContentAsString());

        // test 2 - success scenario

        stmt.executeUpdate("delete from shoppingcart where idshoppingCart='test@test.com'");
        stmt.executeUpdate("delete from orders where user_id='test@test.com'");

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        request.addParameter("idshoppingCart", "test@test.com");
        request.addParameter("gamename", "Super Smash Bros");
        request.addParameter("fees", "20");

        servlet.doPost(request, response);

        assertEquals("Successfully added to cart", response.getContentAsString());
    }
}