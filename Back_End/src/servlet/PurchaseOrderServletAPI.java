package servlet;

import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

import static java.lang.Class.forName;

@WebServlet(urlPatterns = {"/placeOrder"})
public class PurchaseOrderServletAPI extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonReader reader = Json.createReader(req.getReader());
        JsonObject jsonObject = reader.readObject();

        resp.addHeader("Access-Control-Allow-Origin", "*");

        String orderId = jsonObject.getString("order_ID");
        String orderDate = jsonObject.getString("date");
        String customerId = jsonObject.getString("customer_ID");
        String itemCode = jsonObject.getString("ItemCode");
        String qty = jsonObject.getString("qty");
        String unitPrice = jsonObject.getString("unitPrice");
        JsonArray orderDetails = jsonObject.getJsonArray("orderDetails");

        for (int i = 0; i < orderDetails.size(); i++) {

            System.out.println(orderDetails.getJsonArray(i).getString(0));
        }

        try {
            forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/posapi", "root", "1234");
            connection.setAutoCommit(false);

            PreparedStatement orderStatement = connection.prepareStatement("INSERT INTO orders VALUES(?,?,?)");
            orderStatement.setString(1, orderId);
            orderStatement.setString(2, orderDate);
            orderStatement.setString(3, customerId);

            int affectedRows = orderStatement.executeUpdate();
            if (affectedRows == 0) {
                connection.rollback();
                throw new RuntimeException("Failed to save the order");
            }else {
                System.out.println("Order Saved");

            }


            PreparedStatement orderDetailStatement = connection.prepareStatement("INSERT INTO order_detail VALUES(?,?,?,?)");
            orderDetailStatement.setString(1,itemCode );
            orderDetailStatement.setString(2, orderId);
            orderDetailStatement.setString(3, qty);
            orderDetailStatement.setString(4, unitPrice);

            affectedRows = orderDetailStatement.executeUpdate();
            if (affectedRows == 0) {
                connection.rollback();
                throw new RuntimeException("Failed to save the order details");
            }else {
                System.out.println("Order Details Saved");
            }



            connection.commit();
            resp.setStatus(HttpServletResponse.SC_OK);
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("message", "Successfully Purchased Order.");
            objectBuilder.add("status", resp.getStatus());
            resp.getWriter().print(objectBuilder.build());


        }catch (ClassNotFoundException e){
            throw new RuntimeException(e);
        } catch (SQLException e) {
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("message", "Failed to save the order.");
            objectBuilder.add("status", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().print(objectBuilder.build());

        }

    }


    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Access-Control-Allow-Origin", "*");
        resp.addHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.addHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE");
    }
}

