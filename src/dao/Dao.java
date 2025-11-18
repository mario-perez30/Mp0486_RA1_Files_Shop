package dao;

import model.Employee;
import java.util.ArrayList;
import java.util.List;

import model.Product;

public interface Dao {
	
	public void connect();

	public void disconnect();

	public Employee getEmployee(int employeeId, String password);
	
	public ArrayList<Product> getInventory();
	
	public boolean writeInventory(ArrayList<Product> inventory);
	
	public boolean addProduct(Product p);
	
	public boolean updateProduct(Product p);
	
	public boolean deleteProduct(int id);
}
