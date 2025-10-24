package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;

import model.Product;
import model.Amount;
import model.Employee;

public class DaoImplFile implements Dao {

    @Override
    public void connect() {
        
    }

    @Override
    public void disconnect() {
       
    }

    @Override
    public Employee getEmployee(int employeeId, String password) {
       
        return null;
    }

   
    @Override
    public ArrayList<Product> getInventory() {
        ArrayList<Product> inventory = new ArrayList<>();

        File f = new File(System.getProperty("user.dir") + File.separator + "files/inputInventory.txt");

        try (FileReader fr = new FileReader(f);
             BufferedReader br = new BufferedReader(fr)) {

            String line = br.readLine();

            while (line != null) {
                String[] sections = line.split(";");
                String name = null;
                double wholesalerPrice = 0.0;
                int stock = 0;

                for (String sec : sections) {
                    sec = sec.trim();

                    if (sec.toLowerCase().startsWith("product:")) {
                        name = sec.substring(sec.indexOf(":") + 1).trim();

                    } else if (sec.toLowerCase().startsWith("wholesaler price:")) {
                        String val = sec.substring(sec.indexOf(":") + 1).replace(",", ".").trim();
                        try {
                            wholesalerPrice = Double.parseDouble(val);
                        } catch (NumberFormatException e) {
                            wholesalerPrice = 0.0;
                        }

                    } else if (sec.toLowerCase().startsWith("stock:")) {
                        String val = sec.substring(sec.indexOf(":") + 1).trim();
                        try {
                            stock = Integer.parseInt(val);
                        } catch (NumberFormatException e) {
                            stock = 0;
                        }
                    }
                }

                if (name != null) {
                    inventory.add(new Product(name, new Amount(wholesalerPrice), true, stock));
                }

                line = br.readLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return inventory;
    }
    
  
    @Override
    public boolean writeInventory(ArrayList<Product> inventory) {
        LocalDate now = LocalDate.now();
        String fileName = "inventory_" + now.toString() + ".txt";

        File f = new File(System.getProperty("user.dir") + File.separator + "files" + File.separator + fileName);

        try (FileWriter fw = new FileWriter(f);
             PrintWriter pw = new PrintWriter(fw)) {

            int counter = 1;
            for (Product p : inventory) {
                pw.print(counter + ";");
                pw.print("Product:" + p.getName() + ";");
                pw.print("Stock:" + p.getStock() + ";");
                pw.println();
                counter++;
            }

            pw.println("Total number of products:" + (inventory == null ? 0 : inventory.size()) + ";");
            pw.flush();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
