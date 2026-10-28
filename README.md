# Employee Management System (Java + Hibernate + JDBC + MySQL)

## Architecture
Browser (index.html)  --HTTP/JSON-->  Java Server (MainServer)  --Hibernate / JDBC-->  MySQL (employee_db)

| Feature                    | Technology |
|----------------------------|------------|
| Add / View / Update / Delete | Hibernate (EmployeeDAO) |
| Search by name, Statistics | JDBC (EmployeeJdbcDAO) |

## Requirements
- Java 17 (or 11 / 8+)
- Maven
- MySQL running on localhost:3306

## Steps to run
1. Start MySQL. The database `employee_db` and table `employees` are created automatically.
2. Open `src/main/resources/hibernate.cfg.xml` and `EmployeeJdbcDAO.java`
   and set your MySQL username/password (default: root / root).
3. In this folder run:  `mvn compile exec:java`
4. Open http://localhost:8080 in your browser.

## Project files
- Employee.java         - entity (table)
- HibernateUtil.java    - Hibernate setup
- EmployeeDAO.java      - Hibernate CRUD
- EmployeeJdbcDAO.java  - JDBC search + stats
- MainServer.java       - server (client <-> database connection)
- index.html            - client UI
