package com.company.ems;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** JDBC part: Search employees by name + simple statistics (plain SQL). */
public class EmployeeJdbcDAO {

    // CHANGE username/password to match hibernate.cfg.xml
    private static final String URL  = "jdbc:mysql://localhost:3306/employee_db?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASS = "Meka@6154";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // SEARCH by name using JDBC
    public List<Employee> searchByName(String name) throws SQLException {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT id, name, email, department, salary FROM employees WHERE name LIKE ? AND deleted = false";
        
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Employee e = new Employee();
                e.setId(rs.getInt("id"));
                e.setName(rs.getString("name"));
                e.setEmail(rs.getString("email"));
                e.setDepartment(rs.getString("department"));
                e.setSalary(rs.getDouble("salary"));
                list.add(e);
            }
        }
        return list;
    }

    // STATS using JDBC: total employees and average salary
    public String getStatsJson() throws SQLException {
    	String sql = "SELECT COUNT(*) AS total, IFNULL(AVG(salary), 0) AS average FROM employees WHERE deleted = false";

        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            rs.next();
            return "{\"total\":" + rs.getInt("total") +
                   ",\"averageSalary\":" + Math.round(rs.getDouble("average")) + "}";
        }
    }
}
