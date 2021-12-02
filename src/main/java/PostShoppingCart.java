import com.database.DatabaseConnection;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Servlet Name
@WebServlet("/PostShoppingCart")
public class PostShoppingCart extends HttpServlet {

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

            // create a SQL query to insert data into shopping cart table
            PreparedStatement st = con.prepareStatement("insert into shoppingcart values(?, ?, ?)");

            StringBuffer jb = new StringBuffer();
            String line;
            try {
                BufferedReader reader = request.getReader();
                while ((line = reader.readLine()) != null) {
                    jb.append(line);
                }
            } catch (Exception e) { /*report an error*/ }

            String idshoppingCart, gamename;
            double fees;
            try {
                JSONObject jsonObject =  new JSONObject(jb.toString());
                System.out.println(jsonObject);
                idshoppingCart = jsonObject.getString("idshoppingCart");
                gamename = jsonObject.getString("gamename");
                fees = jsonObject.getDouble("fees");
            } catch (JSONException e) {
                idshoppingCart = request.getParameter("idshoppingCart");
                gamename = request.getParameter("gamename");
                fees = Double.valueOf(request.getParameter("fees"));
                System.out.println("Error parsing JSON request string");
            }

            // checking if shopping cart already has the game
            boolean shoppingCartIdAlreadyExists = false;
            String sql_res = "select * from shoppingcart where idshoppingCart='".concat(String.valueOf(idshoppingCart)).concat("'");
            ResultSet rs = st.executeQuery(sql_res);
            if(rs.next())
                shoppingCartIdAlreadyExists = true;

            // get games currently in cart
            sql_res = "SELECT gamename FROM shoppingcart WHERE idshoppingCart = '".concat(String.valueOf(idshoppingCart)).concat("'");
            rs = st.executeQuery(sql_res);
            String gamesInCart = "",allgames = "";
            if(rs.next())
                gamesInCart = rs.getString("gamename");

            //get games currently checked out
            sql_res = "SELECT gamename FROM orders WHERE user_id = '".concat(String.valueOf(idshoppingCart)).concat("'");
            rs = st.executeQuery(sql_res);
            if(rs.next())
                allgames=gamesInCart.concat(rs.getString("gamename"));

            System.out.println(allgames);

            // if game is already in cart/checked out then throw error
            if(allgames.contains(gamename))
                throw new Exception("Game is already in cart/checked out!");

            if(!shoppingCartIdAlreadyExists) { // if shopping cart id doesn't already exist then create new entry in shoppingcart table
                // get data from the request object and set data to st pointer
                st.setString(1, idshoppingCart);
                st.setString(2, gamename);
                st.setDouble(3, fees);

                // register changes in database
                st.executeUpdate();
                st.close();
                con.close();
            } else {
                // update cart for current shopping cart id
                sql_res = "UPDATE shoppingcart"
                        .concat(" SET gamename = '").concat(gamesInCart).concat(", ").concat(gamename).concat("'")
                        .concat(" , fees = fees+").concat(String.valueOf(fees)).concat("")
                        .concat(" WHERE idshoppingCart = '").concat(String.valueOf(idshoppingCart)).concat("'");
                st.executeUpdate(sql_res);
            }

            // display successfully created message
            PrintWriter out = response.getWriter();
            out.print("Successfully added to cart");

        }
        catch (Exception e) {
            // display failure message
            PrintWriter out = response.getWriter();
            out.print(e.getMessage());
            e.printStackTrace();
        }
    }
}
