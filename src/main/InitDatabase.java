package main;
import javax.persistence.*;
import model.Employee;

public class InitDatabase {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("objects/users.odb");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            
            Employee admin = new Employee(123, "Admin", "test");
            
            em.persist(admin);
            
            em.getTransaction().commit();
            System.out.println("Archivo users.odb creado exitosamente en la carpeta objects.");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
}