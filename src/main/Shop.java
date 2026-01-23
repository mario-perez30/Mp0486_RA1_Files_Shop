package main;

import java.util.ArrayList;
import model.Amount; 
import model.Product;
import dao.Dao;
import dao.DaoImplHibernate;

public class Shop {
	private ArrayList<Product> inventory;
	private Amount cash; 
	private Dao dao;

	public Shop() {
		this.cash = new Amount(100.0); 
		this.inventory = new ArrayList<Product>();
		
		this.dao = new DaoImplHibernate();
		this.dao.connect();
	}

	public Amount getCash() {
		return cash;
	}

	public void setCash(Amount cash) {
		this.cash = cash;
	}

	public void loadInventory() {
		this.inventory = dao.getInventory();
	}

	public boolean writeInventory() {
		return dao.writeInventory(this.inventory);
	}

	public void addProduct(Product product) {
		if (dao.addProduct(product)) {
			this.inventory.add(product);
		}
	}

	public boolean updateProduct(Product product) {
		return dao.updateProduct(product);
	}

	public boolean deleteProduct(Product product) {
		if (dao.deleteProduct(product.getId())) {
			return this.inventory.remove(product);
		}
		return false;
	}

	public Product findProduct(String name) {
		for (Product product : inventory) {
			if (product.getName().equalsIgnoreCase(name)) {
				return product;
			}
		}
		return null;
	}
}