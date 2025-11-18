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
        if (inventory == null || inventory.isEmpty()) {
            System.out.println("Inventario vacío, no hay datos para exportar.");
            return true; 
        }

        String query = "INSERT INTO historical_inventory "
                + "(id_product, name, wholesaler, price, available, stock, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, NOW())"; 


        try (PreparedStatement ps = connection.prepareStatement(query)) {

            for (Product product : inventory) {
                
                double publicPrice = product.getWholesalerPrice().getValue() * 2.0;

                ps.setInt(1, product.getId()); 
                ps.setString(2, product.getName()); 
                ps.setDouble(3, product.getWholesalerPrice().getValue()); 
                ps.setDouble(4, publicPrice); 
                ps.setBoolean(5, product.isAvailable()); 
                ps.setInt(6, product.getStock()); 
             
                ps.addBatch();
            }

            int[] results = ps.executeBatch();
            
            for (int result : results) {
                if (result == PreparedStatement.EXECUTE_FAILED) {
                    System.out.println("Error: Al menos una inserción no se ha insertado");
                    return false;
                }
            }
            return true; 
        } catch (SQLException e) {
            System.err.println("Error SQL al exportar el inventario:");
            e.printStackTrace();
            return false; 
        }
    }
 
	@Override
    public boolean addProduct(Product p) { 
        String query = "INSERT INTO inventory (name, wholesalerPrice, available, stock) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, p.getName());
            ps.setDouble(2, p.getWholesalerPrice().getValue());
            ps.setBoolean(3, p.isAvailable());
            ps.setInt(4, p.getStock());
            
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        p.setId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error SQL al añadir producto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
	@Override
    public boolean updateProduct(Product p) { 
        String query = "UPDATE inventory SET stock = ?, available = ? WHERE id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            
            ps.setInt(1, p.getStock());
            ps.setBoolean(2, p.isAvailable());
            ps.setInt(3, p.getId());
            
            int rowsAffected = ps.executeUpdate();
            
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error SQL al actualizar producto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
	@Override
    public boolean deleteProduct(int id) { 
        String query = "DELETE FROM inventory WHERE id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            
            ps.setInt(1, id);
            
            int rowsAffected = ps.executeUpdate();
            
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error SQL al eliminar producto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}
