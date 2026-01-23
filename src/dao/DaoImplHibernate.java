package dao;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import model.Employee;
import model.Product;
import model.ProductHistory;

public class DaoImplHibernate implements Dao {
	private SessionFactory sessionFactory;

	@Override
	public void connect() {
		sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
	}

	@Override
	public void disconnect() {
		if (sessionFactory != null) {
			sessionFactory.close();
		}
	}

	@Override
	public ArrayList<Product> getInventory() {
		Session session = sessionFactory.openSession();
		List<Product> products = session.createQuery("from Product", Product.class).list();
		session.close();
		return new ArrayList<>(products);
	}

	@Override
	public boolean writeInventory(ArrayList<Product> inventory) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		try {
			for (Product p : inventory) {
				ProductHistory history = new ProductHistory(p);
				session.save(history);
			}
			tx.commit();
			return true;
		} catch (Exception e) {
			if (tx != null) tx.rollback();
			e.printStackTrace();
			return false;
		} finally {
			session.close();
		}
	}

	@Override
	public boolean addProduct(Product p) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		try {
			session.save(p); 
			tx.commit();
			return true;
		} catch (Exception e) {
			if (tx != null) tx.rollback();
			return false;
		} finally {
			session.close();
		}
	}

	@Override
	public boolean updateProduct(Product p) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		try {
			session.update(p);
			tx.commit();
			return true;
		} catch (Exception e) {
			if (tx != null) tx.rollback();
			return false;
		} finally {
			session.close();
		}
	}

	@Override
	public boolean deleteProduct(int id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		try {
			Product p = session.get(Product.class, id);
			if (p != null) {
				session.delete(p);
				tx.commit();
				return true;
			}
			return false;
		} catch (Exception e) {
			if (tx != null) tx.rollback();
			return false;
		} finally {
			session.close();
		}
	}

	@Override
	public Employee getEmployee(int employeeId, String password) {
		return null;
	}
}