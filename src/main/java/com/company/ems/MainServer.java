package com.company.ems;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * SERVER: a small web server (no Tomcat needed).
 * Browser (client)  ->  this server  ->  Hibernate/JDBC  ->  MySQL (database)
 */
public class MainServer {

    private static final EmployeeDAO hibernateDao = new EmployeeDAO();
    private static final EmployeeJdbcDAO jdbcDao = new EmployeeJdbcDAO();
    private static final Gson gson = new Gson();

    public static void main(String[] args) throws Exception {
        // touching HibernateUtil connects to MySQL and creates the table
        HibernateUtil.getSessionFactory();

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/", MainServer::showPage);                 // the web page
        server.createContext("/api/employees", MainServer::handleEmployees); // Hibernate CRUD
        server.createContext("/api/search", MainServer::handleSearch);   // JDBC search
        server.createContext("/api/stats", MainServer::handleStats);     // JDBC stats

        server.start();
        System.out.println("Server running -> open http://localhost:8080");
    }

    // ---------- Show index.html ----------
    private static void showPage(HttpExchange ex) {
        try (InputStream in = MainServer.class.getResourceAsStream("/index.html")) {
            byte[] html = in.readAllBytes();
            ex.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            ex.sendResponseHeaders(200, html.length);
            ex.getResponseBody().write(html);
            ex.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------- CRUD using Hibernate ----------
    private static void handleEmployees(HttpExchange ex) {
        try {
            String method = ex.getRequestMethod();
            String path = ex.getRequestURI().getPath();   // /api/employees  or  /api/employees/5

            if (method.equals("GET")) {
                send(ex, 200, gson.toJson(hibernateDao.getAll()));

            } else if (method.equals("POST")) {
                Employee emp = gson.fromJson(readBody(ex), Employee.class);
                String error = validate(emp);
                if (error != null) {
                    send(ex, 400, "{\"message\":\"" + error + "\"}");
                    return;
                }
                hibernateDao.save(emp);
                send(ex, 200, "{\"message\":\"Employee added\"}");

            } else if (method.equals("PUT")) {
                Employee emp = gson.fromJson(readBody(ex), Employee.class);
                String error = validate(emp);
                if (error != null) {
                    send(ex, 400, "{\"message\":\"" + error + "\"}");
                    return;
                }
                emp.setId(getIdFromPath(path));
                hibernateDao.update(emp);
                send(ex, 200, "{\"message\":\"Employee updated\"}");
            } else if (method.equals("DELETE")) {
                hibernateDao.delete(getIdFromPath(path));
                send(ex, 200, "{\"message\":\"Employee deleted\"}");

            } else {
                send(ex, 405, "{\"message\":\"Method not allowed\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            send(ex, 500, "{\"message\":\"Server error\"}");
        }
    }

    // ---------- Search using JDBC ----------
    private static void handleSearch(HttpExchange ex) {
        try {
            String query = ex.getRequestURI().getQuery();          // name=john
            String name = (query == null) ? "" : URLDecoder.decode(query.replace("name=", ""), "UTF-8");
            send(ex, 200, gson.toJson(jdbcDao.searchByName(name)));
        } catch (Exception e) {
            e.printStackTrace();
            send(ex, 500, "{\"message\":\"Server error\"}");
        }
    }
 // Checks the employee data. Returns an error message, or null if everything is OK.
    private static String validate(Employee emp) {
        if (emp == null || emp.getName() == null || emp.getName().trim().isEmpty()) {
            return "Name is required";
        }
        if (!emp.getName().matches("[A-Za-z ]{2,50}")) {
            return "Name must contain only letters and spaces (2 to 50 characters)";
        }
        if (emp.getEmail() == null || !emp.getEmail().matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$")) {
            return "Please enter a valid Gmail address";
        }
        if (emp.getDepartment() == null || emp.getDepartment().trim().isEmpty()) {
            return "Department is required";
        }
        if (emp.getSalary() <= 0) {
            return "Salary must be greater than 0";
        }
        return null;
    }

    // ---------- Stats using JDBC ----------
    private static void handleStats(HttpExchange ex) {
        try {
            send(ex, 200, jdbcDao.getStatsJson());
        } catch (Exception e) {
            e.printStackTrace();
            send(ex, 500, "{\"message\":\"Server error\"}");
        }
    }

    // ---------- small helper methods ----------
    private static int getIdFromPath(String path) {
        return Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
    }

    private static String readBody(HttpExchange ex) throws Exception {
        return new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private static void send(HttpExchange ex, int code, String json) {
        try {
            byte[] data = json.getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            ex.sendResponseHeaders(code, data.length);
            OutputStream os = ex.getResponseBody();
            os.write(data);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
