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

@WebServlet(urlPatterns = {"/Customer"})
public class CustomerServletAPI extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        try {
//            Class.forName("com.mysql.jdbc.Driver");
//            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/posapi", "root", "1234");
//            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM Customer");
//            ResultSet rst = pstm.executeQuery();
//            JsonArrayBuilder allCustomers = Json.createArrayBuilder();
//
//            while (rst.next()) {
//                JsonObjectBuilder customer = Json.createObjectBuilder();
//                customer.add("id", rst.getString("cusID"));
//                customer.add("name", rst.getString("cusName"));
//                customer.add("address", rst.getString("cusAddress"));
//                customer.add("salary", rst.getDouble("cusSalary"));
//                allCustomers.add(customer.build());
//            }
//            resp.addHeader("Content-Type","application/json");
//            resp.addHeader("Access-Control-Allow-Origin","*");
//
//            JsonObjectBuilder job = Json.createObjectBuilder();
//            job.add("state","OK");
//            job.add("message","Successfully Loaded..!");
//            job.add("data",allCustomers.build());
//            resp.getWriter().print(job.build());
//
//        }catch (ClassNotFoundException | SQLException e){
//            JsonObjectBuilder rjo = Json.createObjectBuilder();
//            rjo.add("state","Error");
//            rjo.add("message",e.getLocalizedMessage());
//            rjo.add("data","");
//            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            resp.getWriter().print(rjo.build());
//        }
//    }

        try {
            /*<!--when the response received catch it and set it to the table-->*/

            forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/posapi", "root", "1234");

            String option = req.getParameter("option");

            switch (option) {
                case "GetAll":
                    PreparedStatement pstm = connection.prepareStatement("select * from Customer");
                    ResultSet rst = pstm.executeQuery();
                    resp.addHeader("Access-Control-Allow-Origin", "*");


                    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                    while (rst.next()) {
                        String id = rst.getString(1);
                        String name = rst.getString(2);
                        String salary = rst.getString(3);
                        String address = rst.getString(4);

                        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                        objectBuilder.add("id", id);
                        objectBuilder.add("name", name);
                        objectBuilder.add("salary", salary);
                        objectBuilder.add("address", address);

                        arrayBuilder.add(objectBuilder.build());
                    }
                    resp.setContentType("application/json");
                    resp.getWriter().print(arrayBuilder.build());
                    break;

                case "GetIds":
                    PreparedStatement pstm2 = connection.prepareStatement("SELECT cusID FROM customer ORDER BY cusID DESC LIMIT 1;");
                    ResultSet rst2 = pstm2.executeQuery();
                    resp.addHeader("Access-Control-Allow-Origin", "*");

                    //System.out.println("GetIds"+pstm2);

                    JsonArrayBuilder arrayBuilder2 = Json.createArrayBuilder();
                    while (rst2.next()) {
                        String id = rst2.getString("id");
                        int newCustomerId=Integer.parseInt(id.replace("C0-",""))+1;
                        arrayBuilder2.add(newCustomerId);
                    }
                    resp.setContentType("application/json");
                    resp.getWriter().print(arrayBuilder2.build());

                    break;

                case "search":
                    PreparedStatement pstm3 = connection.prepareStatement("select * from customer where cusID=?");
                    pstm3.setObject(1, req.getParameter("cusID"));
                    ResultSet rst3 = pstm3.executeQuery();
                    resp.addHeader("Access-Control-Allow-Origin", "*");

                    JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                    if (rst3.next()) {
                        String id = rst3.getString(1);
                        String name = rst3.getString(2);
                        String salary= rst3.getString(3);
                        String address  = rst3.getString(4);

                        objectBuilder.add("id", id);
                        objectBuilder.add("name", name);
                        objectBuilder.add("salary", salary);
                        objectBuilder.add("address", address);

                    }
                    resp.setContentType("application/json");
                    resp.getWriter().print(objectBuilder.build());
                    break;

            }


        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    //    query string
//    JSON
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String cusID = req.getParameter("cusID");
        String cusName = req.getParameter("cusName");
        String cusAddress = req.getParameter("cusAddress");
        String cusSalary = req.getParameter("cusSalary");
        resp.addHeader("Access-Control-Allow-Origin","*");

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/posapi", "root", "1234");

            PreparedStatement pstm = connection.prepareStatement("insert into Customer values(?,?,?,?)");
           pstm.setObject(1,cusID);
            pstm.setObject(2,cusName);
            pstm.setObject(3,cusAddress);
            pstm.setObject(4,cusSalary);
            boolean b = pstm.executeUpdate() > 0;
            if (b){
                JsonObjectBuilder responseObject = Json.createObjectBuilder();
                responseObject.add("state","Ok");
                responseObject.add("message","Successfully added..!");
                responseObject.add("data","");
                resp.getWriter().print(responseObject.build());
            }
        } catch (ClassNotFoundException e) {
            JsonObjectBuilder error = Json.createObjectBuilder();
            error.add("state","Ok");
            error.add("message",e.getLocalizedMessage());
            error.add("data","");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().print(error.build());
        }catch (SQLException e) {
            JsonObjectBuilder error = Json.createObjectBuilder();
            error.add("state","Error");
            error.add("message",e.getLocalizedMessage());
            error.add("data","");

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print(error.build());
        }
    }

    //    query string
//    JSON
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String cusID = req.getParameter("cusID");
        resp.setContentType("application/json");
        resp.addHeader("Access-Control-Allow-Origin","*");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/posapi", "root", "1234");
            PreparedStatement pstm = connection.prepareStatement("delete from Customer where cusID=?");
            pstm.setObject(1,cusID);
            boolean b = pstm.executeUpdate() > 0;
            if (b) {
                JsonObjectBuilder rjo = Json.createObjectBuilder();
                rjo.add("state","Ok");
                rjo.add("message","Successfully Deleted..!");
                rjo.add("data","");
                resp.getWriter().print(rjo.build());
            }else {
                throw new RuntimeException("There is no Customer for that ID..!");
            }
        } catch (RuntimeException e) {
            JsonObjectBuilder rjo = Json.createObjectBuilder();
            rjo.add("state","Error");
            rjo.add("message",e.getLocalizedMessage());
            rjo.add("data","");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print(rjo.build());
        }catch (ClassNotFoundException | SQLException e){
            JsonObjectBuilder rjo = Json.createObjectBuilder();
            rjo.add("state","Error");
            rjo.add("message",e.getLocalizedMessage());
            rjo.add("data","");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().print(rjo.build());
        }
    }

    //    query string
//    JSON
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonReader reader = Json.createReader(req.getReader());
        JsonObject customer = reader.readObject();
        String cusID = customer.getString("cusID");
        String cusName = customer.getString("cusName");
        String cusAddress = customer.getString("cusAddress");
        String cusSalary = customer.getString("cusSalary");
        resp.addHeader("Access-Control-Allow-Origin","*");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/posapi", "root", "1234");
            PreparedStatement pstm = connection.prepareStatement("update Customer set cusName=?,cusAddress=?,cusSalary=? where cusID=?");
            pstm.setObject(4,cusID);
            pstm.setObject(1,cusName);
            pstm.setObject(2,cusAddress);
            pstm.setObject(3,cusSalary);
            boolean b = pstm.executeUpdate() > 0;
            if (b){
                JsonObjectBuilder responseObject = Json.createObjectBuilder();
                responseObject.add("state","Ok");
                responseObject.add("message","Successfully Updated..!");
                responseObject.add("data","");
                resp.getWriter().print(responseObject.build());
            }else{
                throw new RuntimeException("Wrong ID, Please check the ID..!");
            }

        } catch (RuntimeException e) {
            JsonObjectBuilder rjo = Json.createObjectBuilder();
            rjo.add("state","Error");
            rjo.add("message",e.getLocalizedMessage());
            rjo.add("data","");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print(rjo.build());
        }catch (ClassNotFoundException | SQLException e){
            JsonObjectBuilder rjo = Json.createObjectBuilder();
            rjo.add("state","Error");
            rjo.add("message",e.getLocalizedMessage());
            rjo.add("data","");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().print(rjo.build());
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Access-Control-Allow-Origin","*");
        resp.addHeader("Access-Control-Allow-Methods","DELETE,PUT");
        resp.addHeader("Access-Control-Allow-Headers","content-type");
    }
}
