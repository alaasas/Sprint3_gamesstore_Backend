import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.database.DatabaseConnection;
import org.json.JSONException;
import org.json.JSONObject;

// Servlet Name
@WebServlet("/PostOrders")
public class PostOrders extends HttpServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS,HEAD");

        try {
            // initialize the database
            Connection con = DatabaseConnection.initializeDatabase();

            // create a SQL query to insert data into orders table
            PreparedStatement st = con.prepareStatement("insert into orders values(?,?, ?, ?)");

            StringBuffer jb = new StringBuffer();
            String line;
            try {
                BufferedReader reader = request.getReader();
                while ((line = reader.readLine()) != null) {
                    jb.append(line);
                }
            } catch (Exception e) { /*report an error*/ }

            String order_id,user_id, gamename;
            double fees;
            try {
                JSONObject jsonObject =  new JSONObject(jb.toString());
                System.out.println(jsonObject);
                order_id = jsonObject.getString("order_id");
                user_id = jsonObject.getString("user_id");
                gamename = jsonObject.getString("gamename");
                fees = jsonObject.getDouble("fees");
            } catch (JSONException e) {
                order_id = request.getParameter("order_id");
                user_id = request.getParameter("user_id");
                gamename = request.getParameter("gamename");
                fees = Double.valueOf(request.getParameter("fees"));
                System.out.println("Error parsing JSON request string");
            }

            // check if orders id already exists
            boolean orderIdAlreadyExists = false;
            String sql_res = "select * from orders where user_id='".concat(String.valueOf(user_id)).concat("'");
            ResultSet rs = st.executeQuery(sql_res);

            if(rs.next())
                orderIdAlreadyExists = true;

            if(!orderIdAlreadyExists) { // if order id doesn't already exist then create new entry in orders table
                // get data from the request object and set data to st pointer
                st.setString(1, order_id);
                st.setString(2, user_id);
                st.setString(3, gamename);
                st.setDouble(4, fees);

                //Deleting the games from cart as they are ready for check out
                sql_res = "DELETE FROM shoppingcart where idshoppingCart = '".concat(String.valueOf(user_id)).concat("'");
                st.executeUpdate(sql_res);

                // register changes in database
                st.executeUpdate();
                st.close();
                con.close();
            } else { // update games list for current order id
                // get games currently in cart
                sql_res = "SELECT gamename FROM orders WHERE user_id = '".concat(String.valueOf(user_id)).concat("'");
                rs = st.executeQuery(sql_res);
                rs.next();
                String gamesInCart = rs.getString("gamename");

                System.out.println(gamesInCart);

                // if game is already in cart then throw error
                if(gamesInCart.contains(gamename))
                    throw new Exception("You already checkout this order!");

                //Deleting the games from cart as they are ready for check out
                sql_res = "DELETE FROM shoppingcart where idshoppingCart = '".concat(String.valueOf(user_id)).concat("'");
                st.executeUpdate(sql_res);

                // update Orders
                sql_res = "UPDATE orders"
                        .concat(" SET gamename = '").concat(gamesInCart).concat(", ").concat(gamename).concat("'")
                        .concat(" , fees = fees+").concat(String.valueOf(fees)).concat("")
                        .concat(" WHERE user_id = '").concat(String.valueOf(user_id)).concat("'");
                st.executeUpdate(sql_res);
            }

            // display successfully created message
            PrintWriter out = response.getWriter();
            out.print("Successfully Checked out");

        }
        catch (Exception e) {
            // display failure message
            PrintWriter out = response.getWriter();
            out.print(e.getMessage());
            e.printStackTrace();
        }
    }
}
