import java.io.*;
import java.util.ArrayList;

/**
 * Store.java
 * <p>
 * Stores data for the stores
 *
 * @author Yash Ashtekar
 * @version 18/05/23
 */
public class Store implements Serializable {
    private String storeName;
    private ArrayList<Product> products;
    private double revenue;

    private ArrayList<Sale> saleHistory;

    public Store(String storeName, ArrayList<Product> products, ArrayList<Sale> saleHistory, double revenue) {
        this.storeName = storeName;
        this.revenue = revenue;
        this.products = products;
        this.saleHistory = saleHistory;
    }

    public Store(String storeName) {
        this.storeName = storeName;
        this.products = new ArrayList<>();
        this.saleHistory = new ArrayList<>();
        this.revenue = 0;
    }

    public ArrayList<Sale> getSaleHistory() {
        return saleHistory;
    }

    public void setSaleHistory(ArrayList<Sale> saleHistory) {
        this.saleHistory = saleHistory;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    /**
     * run with addToStore method from sellers
     */
    public void addProduct(Product product) {
        boolean inStore = false;

        if (products != null) {
            for (Product productInStore : products) { //check if product is already in store
                if (productInStore.equals(product)) {
                    inStore = true;
                }
            }
            if (!inStore) { //if product doesnt already exist in products then add it to list
                products.add(product);
            }
        } else {
            products = new ArrayList<Product>();
            products.add(product);
        }
    }

    public void addProductList(ArrayList<Product> productList) {
        for (Product product : productList) {
            addProduct(product);
        }
    }

    public void deleteProduct(Product product) {
        for (int i = 0; i < products.size(); i++) { //cycle through products & remove instances w/same name, storename
            if (product.equals(products.get(i))) products.remove(i);
        }
    }

    /**
     * run whenever a product is sold
     */
    public void addSale(Product product, String customerName, int quantity) {
        saleHistory.add(new Sale(customerName, product.getName(), quantity * product.getPrice(), quantity));
    }

    //Calculate Revenue
    public void calcStoreRevenue() {
        double tempRevenue = 0;
        for (Product product : products) {
            tempRevenue += product.getRevenue();
        }
        revenue = tempRevenue;
    }

    /*
    Reads store save file in format of toString
     */
    public static ArrayList<Store> readStore(String storeFile, ArrayList<Product> productList)
            throws FileFormatException {
        ArrayList<Store> storeListTemp = new ArrayList<>();
        try (BufferedReader bf = new BufferedReader(new FileReader(new File(storeFile)))) {
            String line = bf.readLine();
            while (line != null) {
                ArrayList<Product> productsToAdd = new ArrayList<>(); //line 1 of toString format
                String[] storeParams = line.split(","); // index 0 = store name, index 1 = revenue

                bf.readLine(); //Reads product line (functionality done later)

                line = bf.readLine(); //line 2 of toString format
                String[] sales = line.split(",");

                ArrayList<Sale> salesHistTemp = new ArrayList<>();

                //If there are sales
                if (sales.length > 0 && !(sales[0].equals(""))) {
                    for (String sale : sales) {
                        String[] saleParams = sale.split(";");
                        if (saleParams.length == 4) {
                            salesHistTemp.add(new Sale(saleParams[1], saleParams[0],
                                    Double.parseDouble(saleParams[2]), Integer.parseInt(saleParams[3])));
                        } else {
                            throw new FileFormatException("Error Reading Store");
                        }
                    }
                }

                //add products
                for (Product product : productList) {
                    if (product.getStoreName().equals(storeParams[0])) {
                        productsToAdd.add(product); //adds product if equal storeName
                    }
                }
                storeListTemp.add(new Store(storeParams[0], productsToAdd, salesHistTemp,
                        Double.parseDouble(storeParams[1])));
                line = bf.readLine();
            }
            return storeListTemp;
        } catch (IOException e) {
            throw new FileFormatException("Error Reading Store!");
        }
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof Store) {
            Store s = (Store) o;
            return (s.getStoreName().equals(storeName));
        } else {
            return false;
        }

    }

    @Override
    public String toString() { 
        /*
        intended toString format: (line 3 is a list of sale history)
        storeName,revenue
 salehistory:       product1;customername;revenue,product2sold,customername,revenue
         */

        String storeString = storeName + "," + revenue + "\n";

        if (products != null && products.size() > 0) {
            for (int i = 0; i < products.size() - 1; i++ ) {
                storeString += products.get(i).getName() + ",";
            }
            storeString += products.get(products.size() - 1).getName() + "\n";
        } else {
            storeString += "\n";
        }

        if (saleHistory != null && saleHistory.size() > 0) {
            for (int i = 0; i < saleHistory.size() - 1; i++) {
                storeString += saleHistory.get(i).getProductName() + ";" + saleHistory.get(i).getCustomerName() + ";" +
                        saleHistory.get(i).getRevenue() + ";" + saleHistory.get(i).getQuantity() + ",";
            }
            storeString += saleHistory.get(saleHistory.size() - 1).getProductName() + ";" +
                    saleHistory.get(saleHistory.size() - 1).getCustomerName() + ";" +
                    saleHistory.get(saleHistory.size() - 1).getRevenue() + ";" +
                    saleHistory.get(saleHistory.size() - 1).getQuantity();
        }

        return storeString;

    }

    public static void writeStoreFile(ArrayList<Store> stores) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(new FileOutputStream("Store.txt", false));
        for (int i = 0; i < stores.size(); i++) {
            pw.println(stores.get(i).toString());
        }
        pw.close();

    }

}