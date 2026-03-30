package dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import model.Amount;
import model.Employee;
import model.Product;

public class DaoImplMongoDB implements Dao {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> inventoryCol;
    private MongoCollection<Document> usersCol;
    private MongoCollection<Document> historicalCol;

    @Override
    public void connect() {
        this.mongoClient = new MongoClient("localhost", 27017);
        this.database = mongoClient.getDatabase("shop"); 
        this.inventoryCol = database.getCollection("inventory"); 
        this.usersCol = database.getCollection("users"); 
        this.historicalCol = database.getCollection("historical_inventory"); 
    }

    @Override
    public void disconnect() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    @Override
    public ArrayList<Product> getInventory() {
        ArrayList<Product> inventory = new ArrayList<>();
        List<Document> results = inventoryCol.find().into(new ArrayList<>());

        for (Document doc : results) {
            String name = doc.getString("name");
            
            int id = doc.getInteger("id"); 
            
            int stock = doc.getInteger("stock");
            boolean available = doc.getBoolean("available");
            
            Document priceDoc = (Document) doc.get("wholesalerPrice");
            double priceValue = 0.0;
            if (priceDoc.get("value") instanceof Integer) {
                priceValue = ((Integer) priceDoc.get("value")).doubleValue();
            } else {
                priceValue = priceDoc.getDouble("value");
            }
            
            Product p = new Product(name, new Amount(priceValue), available, stock);
            
            p.setId(id);
            
            inventory.add(p);
        }
        return inventory;
    }

    @Override
    public boolean writeInventory(ArrayList<Product> inventory) { 
        try {
        	
            for (Product p : inventory) {
                Document doc = new Document("id", p.getId())
                        .append("name", p.getName())
                        .append("wholesalerPrice", new Document("value", p.getWholesalerPrice().getValue())
                                .append("currency", "€"))
                        .append("available", p.isAvailable())
                        .append("stock", p.getStock())
                        .append("created_at", new Date()); 
                
                historicalCol.insertOne(doc); 
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean addProduct(Product p) {
        try {
            if (p.getId() == 0) {
                long count = inventoryCol.countDocuments();
                p.setId((int) count + 1);
            }

            Document doc = new Document("id", p.getId())
                    .append("name", p.getName())
                    .append("wholesalerPrice", new Document("value", p.getWholesalerPrice().getValue())
                            .append("currency", "€"))
                    .append("available", p.isAvailable())
                    .append("stock", p.getStock());
            
            inventoryCol.insertOne(doc);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateProduct(Product p) { 
        try {
            inventoryCol.updateOne(Filters.eq("id", p.getId()), 
                    Updates.combine(
                        Updates.set("stock", p.getStock()),
                        Updates.set("available", p.isAvailable())
                    ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteProduct(int id) { 
        try {
            inventoryCol.deleteOne(Filters.eq("id", id)); 
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Employee getEmployee(int employeeId, String password) { 
        Document userDoc = usersCol.find(Filters.and(
                Filters.eq("employeeId", employeeId), 
                Filters.eq("password", password)
        )).first(); 

        if (userDoc != null) {
            return new Employee(userDoc.getInteger("employeeId"), "NombreEmpleado", password);
        }
        return null;
    }
}