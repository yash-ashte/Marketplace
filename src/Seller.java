import java.io.*;
import java.util.ArrayList;
/**
 * Project 4 - Seller
 *
 * A class to contain all the functionality of the Seller class
 *
 * @author Yash Ashtekar
 *
 * @version 16/05/23
 *
 */
public class Seller extends User {

    private ArrayList<Store> stores;
    private static String sellerFile = "Seller.txt";

    //constructor
    public Seller(String email, String password, ArrayList<Store> stores) {
        super(email, password);
        this.stores = stores;
    }

    public Seller(String email, String name, String password, ArrayList<Store> stores) {
        super(email, password, name);
        this.stores = stores;
    }

    //getters and setters
    public ArrayList<Store> getStores() {
        return stores;
    }


    public void setStores(ArrayList<Store> stores) {
        this.stores = stores;
    }

    public String getSellerFile() {
        return sellerFile;
    }

    public void addStore(Store s) {
        stores.add(s);
    }

    public boolean removeStore(Store s) {
        if (stores.indexOf(s) != -1) {
            stores.remove(s);
            return true;
        }
        return false;
    }

    //Adds new product to a store
    public void addToStore(String name, String storeName, String desc, int quantity,
                           double price, int sales, double revenue) {
        Product createProduct = new Product(name, storeName, desc, quantity, price, sales, revenue);
        for (int i = 0; i < stores.size(); i++) {
            if (stores.get(i).getStoreName().equals(storeName)) {
                stores.get(i).addProduct(createProduct);
            }
        }
    }

    public void addToStore(Product product) { //Adds to store that matches product's storename
        for (int i = 0; i < stores.size(); i++) {
            if (stores.get(i).getStoreName().equals(product.getStoreName())) {
                stores.get(i).addProduct(product);
            }
        }
    }

    //Deletes product to a store
    public void deleteFromStore(Product product) {
        for (int i = 0; i < stores.size(); i++) {
            if (stores.get(i).getStoreName().equals(product.getStoreName())) {
                stores.get(i).deleteProduct(product);
            }
        }
    }

    //writes seller to seller file
    public static boolean writeSellerFile(ArrayList<Seller> sellers) {
        File sellerData = new File(sellerFile);
        try {
            PrintWriter pw = new PrintWriter(sellerData);

            for (Seller s : sellers) {
                pw.println("Email:" + s.getEmail());
                pw.println("Password:" + s.getPassword());
                pw.println("Name:" + s.getName());
                pw.println("Stores: ");
                for (Store store : s.getStores()) {
                    pw.println(store.getStoreName());
                }
                pw.println();
            }

            pw.close();
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Seller) {
            Seller s = (Seller) o;

            return (s.getEmail().equals(super.getEmail()) && s.getPassword().equals(super.getPassword()) &&
                    s.getStores().equals(this.getStores()));

        } else {
            return false;
        }
    }

    //Converts a seller's info into a string
    public String toString() {
        String sellerStr = "Email: " + getEmail() + ", Password: " + getPassword() + ", ";
        ArrayList<Store> sellerStores = this.getStores();
        if (sellerStores.size() == 1) {
            sellerStr += "Store: " + sellerStores.get(0).getStoreName();
        } else {
            sellerStr += "Stores: ";
            sellerStr += (sellerStores.get(0).getStoreName() + ", ");
            for (int i = 1; i < sellerStores.size(); i++) {
                sellerStr += (sellerStores.get(i).getStoreName() + ", ");
            }
        }
        return sellerStr;
    }

    //returns an ArrayList of ALL sellers
    public static ArrayList<Seller> getSellers(ArrayList<Store> storeList)
            throws FileNotFoundException, IOException, FileFormatException {
        ArrayList<Seller> sellers = new ArrayList<>();
        BufferedReader bfr = new BufferedReader(new FileReader(new File(sellerFile)));
        String line = bfr.readLine();
        while (line != null) {

            String[] lineSplit = line.split(":");
            String email = lineSplit[1];

            line = bfr.readLine();
            String[] lineSplitTwo = line.split(":");
            String password = lineSplitTwo[1];

            line = bfr.readLine();
            String[] lineSplit3 = line.split(":");
            String name = lineSplit3[1];

            line = bfr.readLine();
            ArrayList<Store> stores = new ArrayList<>();

            line = bfr.readLine();
            while (!line.equals("")) {

                Store chosenStore = null;
                //Find store in list
                for (Store s : storeList) {
                    if (line.equals(s.getStoreName()))
                        chosenStore = s;
                }

                if (chosenStore == null) {
                    bfr.close();
                    throw new FileFormatException("Error Reading Sellers");
                }

                //add it
                stores.add(chosenStore);
                line = bfr.readLine();
            }
            sellers.add(new Seller(email, name, password, stores));
            line = bfr.readLine();
        }
        return sellers;
    }
}