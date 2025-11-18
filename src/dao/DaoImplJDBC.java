package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import model.Amount;
import model.Employee;
import model.Product;

public class DaoImplJDBC implements Dao {
	Connection connection;

	@Override
	public void connect() {

		String url = "jdbc:mysql://localhost:3306/shop";
		String user = "root";
		String pass = "";
		try {
			this.connection = DriverManager.getConnection(url, user, pass);
		} catch (SQLException e) {
	
			e.printStackTrace();
		}

	}

	@Override
	public void disconnect() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
			
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public Employee getEmployee(int employeeId, String password) {
		Employee employee = null;
		String query = "select * from employee where employeeId= ? and password = ? ";
		
		try (PreparedStatement ps = connection.prepareStatement(query)) { 
    		ps.setInt(1,employeeId);
    	  	ps.setString(2,password);
    	  	//System.out.println(ps.toString());
            try (ResultSet rs = ps.executeQuery()) {
            	if (rs.next()) {
            		employee = new Employee(rs.getInt(1), rs.getString(2), rs.getString(3));
            	}
            }
        } catch (SQLException e) {
			// in case error in SQL
			e.printStackTrace();
		}
    	return employee;
	}	
	
	@Override
    public ArrayList<Product> getInventory() {
        ArrayList<Product> inventory = new ArrayList<>();
        String query = "SELECT id, name, wholesalerPrice, available, stock FROM inventory";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
               
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double wholesalerPrice = rs.getDouble("wholesalerPrice");
                int stock = rs.getInt("stock");
                boolean available = rs.getBoolean("available");

                
                Amount amount = new Amount(wholesalerPrice);
                
                
                Product p = new Product(name, amount, available, stock);
                p.setId(id); 
                
                inventory.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null; 
        }
        
        return inventory;
    }
	

    @Override
    public boolean writeInventory(ArrayList<Product> inventory) {
        return false; 
    }
    
 
    @Override
    public boolean addProduct(Product p) {
        return false;
    }
    
    @Override
    public boolean updateProduct(Product p) {
        return false;
    }
    
    @Override
    public boolean deleteProduct(int id) {
        return false;
    }

}
