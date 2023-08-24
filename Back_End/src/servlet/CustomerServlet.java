package servlet;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
@WebServlet(urlPatterns = {"/pages/customer"})
public class CustomerServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/posapi", "root", "1234");
            PreparedStatement pstm = connection.prepareStatement("select * from Customer");
            ResultSet rst = pstm.executeQuery();
            JsonArrayBuilder allCustomers = Json.createArrayBuilder();
            while (rst.next()) {
                JsonObjectBuilder customerObject = Json.createObjectBuilder();
                customerObject.add("id", rst.getString(1));
                customerObject.add("name", rst.getString(2));
                customerObject.add("address", rst.getString(3));
                customerObject.add("salary", rst.getString(4));
                allCustomers.add(customerObject.build());
            }
            resp.getWriter().print(allCustomers.build());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String cusID = req.getParameter("cusID");
        String cusName = req.getParameter("cusName");
        String cusAddress = req.getParameter("cusAddress");
        String cusSalary = req.getParameter("cusSalary");
        String option = req.getParameter("option");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/posapi", "root", "1234");
            switch (option) {
                case "add":
                    PreparedStatement pstm = connection.prepareStatement("insert into Customer values(?,?,?,?)");
                    pstm.setObject(1, cusID);
                    pstm.setObject(2, cusName);
                    pstm.setObject(3, cusAddress);
                    pstm.setObject(4, cusSalary);
                    resp.addHeader("Content-Type", "application/json");
                    if (pstm.executeUpdate() > 0) {
                        JsonObjectBuilder response = Json.createObjectBuilder();
                        response.add("state", "Ok");
                        response.add("message", "Successfully Added.!");
                        response.add("data", "");
                        resp.getWriter().print(response.build());
                    }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            JsonObjectBuilder response = Json.createObjectBuilder();
            response.add("state", "Error");
            response.add("message", e.getMessage());
            response.add("data", "");
            resp.setStatus(400);
            resp.getWriter().print(response.build());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/posapi", "root", "1234");
            PreparedStatement pstm2 = connection.prepareStatement("delete from Customer where cusID=?");
            pstm2.setObject(1, id);
            resp.addHeader("Content-Type", "application/json");
            if (pstm2.executeUpdate() > 0) {
                JsonObjectBuilder response = Json.createObjectBuilder();
                response.add("state", "Ok");
                response.add("message", "Successfully Deleted.!");
                response.add("data", "");
                resp.getWriter().print(response.build());
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            JsonObjectBuilder response = Json.createObjectBuilder();
            response.add("state", "Error");
            response.add("message", e.getMessage());
            response.add("data", "");
            resp.setStatus(400);
            resp.getWriter().print(response.build());
        }
    }


    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String cusId = req.getParameter("id");
        String cusName = req.getParameter("name");
        String cusAddress = req.getParameter("address");
        String cusSalary = req.getParameter("salary");

        PrintWriter writer = resp.getWriter();
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/posapi", "root", "1234");
            PreparedStatement pstm3 = connection.prepareStatement("update Customer set cusName=?,cusAddress=?,cusSalary=? where cusID=?");
            pstm3.setObject(4, cusId);
            pstm3.setObject(1, cusName);
            pstm3.setObject(2, cusAddress);
            pstm3.setObject(3, cusSalary);
            if (pstm3.executeUpdate() > 0) {
                resp.addHeader("Content-Type", "application/json");
                JsonObjectBuilder cussAdd = Json.createObjectBuilder();
                cussAdd.add("state", "200");
                cussAdd.add("massage", " Customer Updated Sucsess");
                cussAdd.add("data", "");
                resp.setStatus(200);
                writer.print(cussAdd.build());
            } else {
                throw new SQLException();
            }

        } catch (SQLException | ClassNotFoundException e) {
            resp.addHeader("Content-Type", "application/json");
            JsonObjectBuilder obj = Json.createObjectBuilder();
            obj.add("state", "");
            obj.add("massage", e.getMessage());
            obj.add("data", "");
            resp.setStatus(400);
            writer.print(obj.build());

        }
    }
}