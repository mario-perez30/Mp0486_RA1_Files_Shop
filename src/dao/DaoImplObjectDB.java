package dao;

import javax.persistence.*;
import model.Employee;
import java.util.ArrayList;
import model.Product;

public class DaoImplObjectDB implements Dao {
    private EntityManagerFactory emf;
    private EntityManager em;

    @Override
    public void connect() {
        
        emf = Persistence.createEntityManagerFactory("objects/users.odb");
        em = emf.createEntityManager();
    }

    @Override
    public void disconnect() {
        if (em != null) em.close();
        if (emf != null) emf.close();
    }

    @Override
    public Employee getEmployee(int employeeId, String password) {
        try {
             
            TypedQuery<Employee> query = em.createQuery(
                "SELECT e FROM Employee e WHERE e.employeeId = :id AND e.password = :pw", 
                Employee.class);
            query.setParameter("id", employeeId);
            query.setParameter("pw", password);
            
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    
    @Override public ArrayList<Product> getInventory() { return null; }
    @Override public boolean writeInventory(ArrayList<Product> inventory) { return false; }
    @Override public boolean addProduct(Product p) { return false; }
    @Override public boolean updateProduct(Product p) { return false; }
    @Override public boolean deleteProduct(int id) { return false; }
}