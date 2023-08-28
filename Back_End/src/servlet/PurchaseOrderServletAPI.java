package servlet;

import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import static java.lang.Class.forName;

@WebServlet(urlPatterns = {"/placeOrder"})
public class  PurchaseOrderServletAPI extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Before database connection");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/posapi", "root", "1234");
            System.out.println("After database connection");
            PreparedStatement pstm = connection.prepareStatement("select * from orders");
            ResultSet rst = pstm.executeQuery();
            PrintWriter writer = resp.getWriter();
            resp.setHeader("Access-Control-Allow-Origin", "*");
            resp.setHeader("Content-Type", "application/json");
            JsonArrayBuilder allCustomers = Json.createArrayBuilder();


            while (rst.next()) {
                String orderID = rst.getString(1);
                String orderDate = rst.getString(3);
                String orderCusID = rst.getString(2);

                JsonObjectBuilder customer = Json.createObjectBuilder();

                customer.add("order_ID", orderID);
                customer.add("date", orderDate);
                customer.add("customer_ID", orderCusID);

                allCustomers.add(customer.build());
            }


            writer.print(allCustomers.build());


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonReader reader = Json.createReader(req.getReader());
        JsonObject jsonObject = reader.readObject();

        resp.addHeader("Content-Type", "application/json");
        resp.addHeader("Access-Control-Allow-Origin", "*");

        String orderId = jsonObject.getString("order_ID");
        String orderDate = jsonObject.getString("date");
        String customerId = jsonObject.getString("customer_ID");
        String itemCode = jsonObject.getString("ItemCode");
        String qty = jsonObject.getString("qty");
        String unitPrice = jsonObject.getString("unitPrice");
        JsonArray orderDetails = jsonObject.getJsonArray("orderDetails");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
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
            } else {
                System.out.println("Order Saved");

            }


            PreparedStatement orderDetailStatement = connection.prepareStatement("INSERT INTO order_detail VALUES(?,?,?,?)");
            orderDetailStatement.setString(1, orderId);
            orderDetailStatement.setString(2, itemCode);
            orderDetailStatement.setString(3, qty);
            orderDetailStatement.setString(4, unitPrice);

            affectedRows = orderDetailStatement.executeUpdate();
            if (affectedRows == 0) {
                connection.rollback();
                throw new RuntimeException("Failed to save the order details");
            } else {
                System.out.println("Order Details Saved");
            }


            connection.commit();
            resp.setStatus(HttpServletResponse.SC_OK);
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("message", "Successfully Purchased Order.");
            objectBuilder.add("status", resp.getStatus());
            resp.getWriter().print(objectBuilder.build());


        } catch (ClassNotFoundException e) {
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
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Origin");
    }
}