package com.company.ems;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/** HIBERNATE part: Add, View, Update, Delete employees. */
public class EmployeeDAO {

    // CREATE
    public void save(Employee emp) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.save(emp);
        tx.commit();
        session.close();
    }

    // READ (all)
    public List<Employee> getAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Employee> list = session
                .createQuery("from Employee where deleted = false", Employee.class)
                .list();
        session.close();
        return list;
    }

    // UPDATE
    public void update(Employee emp) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.update(emp);
        tx.commit();
        session.close();
    }

    // DELETE
    public void delete(int id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Employee emp = session.get(Employee.class, id);
        if (emp != null) {
            emp.setDeleted(true);      // soft delete: only mark it
            session.update(emp);
        }
        tx.commit();
        session.close();
    }
}
