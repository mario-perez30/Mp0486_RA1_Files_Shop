package model;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.PostLoad;

@Entity
@Table(name = "inventory")
public class Product {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "price") 
    private double price;

    @Column(name = "stock")
    private int stock;

    @Column(name = "available")
    private boolean available;

    @Transient 
    private Amount publicPrice;
    @Transient
    private Amount wholesalerPrice;
    @Transient
    private static int totalProducts;
    
    public Product() {}
    
	public Product(String name, Amount wholesalerPrice, boolean available, int stock) {
		this.name = name;
        this.wholesalerPrice = wholesalerPrice;
        this.price = wholesalerPrice.getValue(); // Sincronización
        this.publicPrice = new Amount(wholesalerPrice.getValue() * 2);
        this.available = available;
        this.stock = stock;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	

	public Amount getPublicPrice() {
		return publicPrice;
	}

	public void setPublicPrice(Amount publicPrice) {
		this.publicPrice = publicPrice;
	}

	public Amount getWholesalerPrice() {
		return wholesalerPrice;
	}

	public void setWholesalerPrice(Amount wholesalerPrice) {
		this.wholesalerPrice = wholesalerPrice;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public static int getTotalProducts() {
		return totalProducts;
	}

	public static void setTotalProducts(int totalProducts) {
		Product.totalProducts = totalProducts;
	}
	
	@javax.persistence.PostLoad
	protected void postLoad() {
	    this.wholesalerPrice = new Amount(this.price);
	    this.publicPrice = new Amount(this.price * 2);
	}

	@Override
	public String toString() {
		return "Product [name=" + name + ", publicPrice=" + publicPrice + ", wholesalerPrice=" + wholesalerPrice
				+ ", available=" + available + ", stock=" + stock + "]";
	}

	
	
	
	
	

    

    
}
