import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.time.*;
import java.time.temporal.ChronoUnit;


/**
 *
 *    MarketplaceServer
 *
 *    A program that connects to clients and allows them to interact with the marketplace
 *
 *    @author Yash Ashtekar
 *
 *    @version 20/05/23
 *
 */
@SuppressWarnings("unchecked")
public class MarketplaceServer implements Runnable {

    private static ArrayList<Customer> customers;
    private static ArrayList<Seller> sellers;
    private static ArrayList<Store> stores;
    private static ArrayList<Product> products;

    //Prevent race conditions
    private static Object lock = new Object();

    private User currentUser;

    private Socket clientConnection;

    //Whether the server should continue running
    private static boolean serverBoolean;

    //JFrame and document for displaying messages
    private static JFrame mainFrame;
    private static Document textDocument;

    //Constructor- one per client connected
    public MarketplaceServer(Socket clientConnection) {
        this.clientConnection = clientConnection;
    }

    public void run() {

        //Set up connections between server and client
        ObjectInputStream clientInput;
        ObjectOutputStream serverOutput;
        try {
            clientInput = new ObjectInputStream(clientConnection.getInputStream());
            serverOutput = new ObjectOutputStream(clientConnection.getOutputStream());
        } catch (IOException e) {
            clientInput = null;
            serverOutput = null;
            displayText("Client " + clientConnection.getInetAddress() + " disconnected.");
            return; //Blank return ends this local thread of server
        }

        //While client is connected- accept new requests and send them
        while (clientConnection.isConnected() && serverBoolean) {

            String request;
            try {
                request = (String) clientInput.readObject();
            } catch (Exception e) {
                request = null;
            }

            if (request == null) {
                LocalTime time = LocalTime.now();
                time = time.truncatedTo(ChronoUnit.SECONDS);
                displayText(time.toString() + "\n===> Connection ended: Client " +
                        clientConnection.getInetAddress() + " disconnected.\n");
                return;
            }

            //For each request- read the latest data, perform the request, and write data
            //(Data is backed up consistently)
            //Synchronized to prevent others accessing invalid data or messing with the global arraylists
            synchronized (lock) {
                boolean readSuccess = readData();

                if (!readSuccess) {
                    LocalTime time = LocalTime.now();
                    time = time.truncatedTo(ChronoUnit.SECONDS);
                    displayText(time.toString() + "\n===> Connection ended: Client " +
                            clientConnection.getInetAddress() + " unable to read files.\n");
                    return;
                }

                //Switch statement for requests- returns whatever client needs
                switch (request) {

                    case "signUpSeller":

                        String nameSignUpSeller;
                        String emailSignUpSeller;
                        String passwordSignUpSeller;

                        try {
                            nameSignUpSeller = (String) clientInput.readObject();
                            emailSignUpSeller = (String) clientInput.readObject();
                            passwordSignUpSeller = (String) clientInput.readObject();

                            boolean b = signUpSeller(nameSignUpSeller, emailSignUpSeller, passwordSignUpSeller);

                            serverOutput.writeObject(b);


                        } catch (Exception e) {
                            return;
                        }

                        break;

                    case "signUpCustomer":

                        String nameSignUpCustomer;
                        String emailSignUpCustomer;
                        String passwordSignUpCustomer;

                        try {
                            nameSignUpCustomer = (String) clientInput.readObject();
                            emailSignUpCustomer = (String) clientInput.readObject();
                            passwordSignUpCustomer = (String) clientInput.readObject();

                            boolean b = signUpCustomer(nameSignUpCustomer, emailSignUpCustomer,
                                    passwordSignUpCustomer);

                            serverOutput.writeObject(b);


                        } catch (Exception e) {
                            return;
                        }

                        break;

                    case "logIn":
                        String emailLI;
                        String passwordLI;

                        try {
                            emailLI = (String) clientInput.readObject();
                            passwordLI = (String) clientInput.readObject();

                            boolean b = logIn(emailLI, passwordLI);

                            boolean isCustomer;
                            if (currentUser instanceof Customer) {
                                isCustomer = true;
                            } else {
                                isCustomer = false;
                            }
                            serverOutput.writeObject(b);

                            serverOutput.writeObject(isCustomer);

                        } catch (Exception e) {
                            return;
                        }

                        break;

                    case "getProducts":

                        try {
                            serverOutput.writeObject(products);
                        } catch (Exception e) {
                            return;
                        }
                        break;

                    case "searchProducts":

                        try {
                            String keyword = (String) clientInput.readObject();

                            ArrayList<Product> searcheProducts = getSearchedProducts(keyword);

                            serverOutput.writeObject(searcheProducts);
                        } catch (Exception e) {
                            return;
                        }

                        break;

                    case "getProfileInfo":

                        try {
                            ArrayList<String> details = getAccountDetails();

                            serverOutput.writeObject(details);
                        } catch (Exception e) {
                            return;
                        }

                        break;

                    case "getSortedProducts":

                        try {

                            int sortTypeProducts = (int) clientInput.readObject();

                            ArrayList<Product> sortedProducts = getSortedProducts(sortTypeProducts);

                            serverOutput.writeObject(sortedProducts);

                        } catch (Exception e) {
                            return;
                        }
                        break;

                    case "addToCart":

                        try {

                            String productNameATC = (String) clientInput.readObject();
                            String storeNameATC = (String) clientInput.readObject();

                            //Get product
                            Product productToAdd = null;
                            for (Product p : products) {
                                if (p.getName().equals(productNameATC) && p.getStoreName().equals(storeNameATC))
                                    productToAdd = p;
                            }

                            boolean successATC = addToCart(productToAdd);

                            serverOutput.writeObject(successATC);

                        } catch (Exception e) {
                            return;
                        }

                        break;

                    case "getAllStores":

                        try {
                            serverOutput.writeObject(stores);

                        } catch (Exception e) {
                            return;
                        }
                        break;

                    case "productsInCart":

                        try {
                            Customer currentCustomerPIC = (Customer) currentUser;
                            serverOutput.writeObject(currentCustomerPIC.getShoppingCart());
                        } catch (Exception e) {
                            return;
                        }

                        break;

                    case "getPurchaseHistory":

                        try {
                            Customer currentCustomerPIC = (Customer) currentUser;
                            serverOutput.writeObject(currentCustomerPIC.getPurchaseHistory());
                        } catch (Exception e) {
                            return;
                        }

                        break;

                    case "removeProductCart":

                        try {

                            String productNameRP = (String) clientInput.readObject();
                            String storeNameRP = (String) clientInput.readObject();

                            Customer c = (Customer) currentUser;

                            //Find product
                            Product productToRemoveFromCart = null;
                            for (Product p : c.getShoppingCart()) {
                                if (p.getName().equals(productNameRP) && p.getStoreName().equals(storeNameRP))
                                    productToRemoveFromCart = p;
                            }

                            removeFromCart(productToRemoveFromCart);

                        } catch (Exception e) {
                            return;
                        }

                        break;

                    case "purchaseCart":
                        boolean success = purchaseCart();

                        try {
                            serverOutput.writeObject(success);
                        } catch (IOException e) {
                            return;
                        }

                        break;

                    case "exportHistory":

                        try {
                            ArrayList<String> historyLines = exportHistory();

                            serverOutput.writeObject(historyLines);

                        } catch (Exception e) {
                            return;
                        }

                        break;

                    case "getStore":

                        try {
                            String storeNameSI = (String) clientInput.readObject();

                            //Storename should be valid
                            Store chosenStore = null;

                            //Find the store
                            for (Store s: stores) {
                                if (s.getStoreName().equals(storeNameSI))
                                    chosenStore = s;
                            }

                            serverOutput.writeObject(chosenStore);


                        } catch (Exception e) {
                            return;
                        }

                        break;

                    case "getAllCartsInfo":

                        try {
                            int numProductsInCart = numItemsInShoppingCart();

                            ArrayList<Product> productsInAllCarts = productsInShoppingCarts();

                            serverOutput.writeObject(numProductsInCart);

                            serverOutput.writeObject(productsInAllCarts);

                        } catch (Exception e) {
                            return;
                        }

                        break;

                    case "addStore":

                        try {
                            String storeNameAS = (String) clientInput.readObject();

                            boolean successAS = createStore(storeNameAS);

                            serverOutput.writeObject(successAS);

                        } catch (Exception e) {
                            return;
                        }

                        break;

                    case "updateProfile":

                        try {
                            String nameUpPr = (String) clientInput.readObject();
                            String emailUpPr = (String) clientInput.readObject();
                            String passwordUpPr = (String) clientInput.readObject();

                            boolean successUpPr = editAccount(emailUpPr, passwordUpPr, nameUpPr);
                            serverOutput.writeObject(successUpPr);

                        } catch (Exception e) {
                            return;
                        }

                        break;

                    case "deleteAccount":

                        deleteAccount();

                        break;

                    case "getProductDetails":

                        try {
                            String productNameGPD = (String) clientInput.readObject();
                            String storeNameGPD = (String) clientInput.readObject();

                            //Find product
                            Product productGPD = null;
                            for (Product p : products) {
                                if (p.getName().equals(productNameGPD) && p.getStoreName().equals(storeNameGPD))
                                    productGPD = p;
                            }

                            ArrayList<String> detailsGPD = getDetails(productGPD);

                            serverOutput.writeObject(detailsGPD);

                        } catch (Exception e) {
                            return;
                        }

                        break;

                    case "importProducts":

                        try {
                            String storeNameIP = (String) clientInput.readObject();
                            ArrayList<String> csvLines = (ArrayList<String>) clientInput.readObject();

                            boolean successIP = importProducts(storeNameIP, csvLines);

                            serverOutput.writeObject(successIP);

                        } catch (Exception e) {
                            return;
                        }

                        break;

                    case "exportProducts":

                        try {
                            String storeNameEP = (String) clientInput.readObject();

                            ArrayList<String> csvLines = exportProducts(storeNameEP);

                            serverOutput.writeObject(csvLines);

                        } catch (Exception e) {
                            return;
                        }

                        break;

                    case "saleHistory":

                        try {

                            String storeNameSH = (String) clientInput.readObject();

                            for (int i = 0; i < stores.size(); i++) {
                                Store s = stores.get(i);
                                if (s.getStoreName().equals(storeNameSH)) {
                                    serverOutput.writeObject(s.getSaleHistory());
                                    break;
                                }
                            }

                        } catch (Exception e) {
                            return;
                        }

                        break;

                    case "deleteStore":

                        try {

                            Seller currentSeller = (Seller) currentUser;

                            String storeNameDS = (String) clientInput.readObject();

                            for (int i = stores.size() - 1; i >= 0; i--) {
                                Store s = stores.get(i);
                                if (s.getStoreName().equals(storeNameDS)) {
                                    currentSeller.removeStore(s);
                                    stores.remove(s);
                                }
                            }

                            for (int i = products.size() - 1; i >= 0; i--) {
                                if (products.get(i).getStoreName().equals(storeNameDS)) {
                                    products.remove(i);
                                }
                            }

                        } catch (Exception e) {
                            return;
                        }

                        break;

                    case "customerPurchaseDashboard":

                        try {

                            String storeNameCPD = (String) clientInput.readObject();
                            ArrayList<String> customersCPD = customersWhoPurchasedFromStore(storeNameCPD, true,
                                    false);
                            ArrayList<String> productsNumCPD = customersWhoPurchasedFromStore(storeNameCPD, true,
                                    true);
                            ArrayList<String> productsCPD = productsForDashboard(storeNameCPD, true, false);
                            ArrayList<String> salesCPD = productsForDashboard(storeNameCPD, true, true);

                            serverOutput.writeObject(customersCPD);
                            serverOutput.writeObject(productsNumCPD);
                            serverOutput.writeObject(productsCPD);
                            serverOutput.writeObject(salesCPD);

                        } catch (Exception e) {
                            return;
                        }

                        break;

                    case "updateProduct":

                        try {

                            String productNameUP = (String) clientInput.readObject();
                            String storeNameUP = (String) clientInput.readObject();
                            double priceUP = (double) clientInput.readObject();
                            int quantityUP = (int) clientInput.readObject();
                            String descriptionUP = (String) clientInput.readObject();

                            Product editingProduct = null;
                            for (int i = 0; i < products.size(); i++) {
                                Product c = products.get(i);
                                if (c.getName().equals(productNameUP) && c.getStoreName().equals(storeNameUP)) {
                                    editingProduct = c;
                                    break;
                                }
                            }

                            editProduct(editingProduct, productNameUP, priceUP, quantityUP, descriptionUP);

                        } catch (Exception e) {
                            return;
                        }

                        break;

                    case "deleteProduct":

                        try {

                            String productNameDP = (String) clientInput.readObject();
                            String storeNameDP = (String) clientInput.readObject();

                            Product removingProduct = null;
                            for (int i = 0; i < products.size(); i++) {
                                Product c = products.get(i);
                                if (c.getName().equals(productNameDP) && c.getStoreName().equals(storeNameDP)) {
                                    removingProduct = c;
                                    break;
                                }
                            }

                            removeProduct(removingProduct);

                        } catch (Exception e) {
                            return;
                        }

                        break;

                    case "sellerHome":

                        try {

                            Seller currentSeller = (Seller) currentUser;
                            serverOutput.writeObject(currentSeller.getStores());

                        } catch (Exception e) {
                            return;
                        }

                        break;

                    case "addProductToStore":

                        try {

                            String productNameAPTS = (String) clientInput.readObject();
                            String storeNameAPTS = (String) clientInput.readObject();
                            double priceAPTS = (double) clientInput.readObject();
                            int quantityAPTS = (int) clientInput.readObject();
                            String descriptionAPTS = (String) clientInput.readObject();

                            serverOutput.writeObject(createNewProduct(storeNameAPTS, productNameAPTS,
                                    descriptionAPTS, priceAPTS, quantityAPTS));

                        } catch (Exception e) {
                            return;
                        }

                        break;

                    case "getSortedCustomers":

                        try {

                            String storeNameCustomers = (String) clientInput.readObject();
                            boolean sortTypeCustomers = (boolean) clientInput.readObject();

                            ArrayList<String> sortedCustomers = customersWhoPurchasedFromStore(storeNameCustomers,
                                    sortTypeCustomers, false);
                            ArrayList<String> sortedPB = customersWhoPurchasedFromStore(storeNameCustomers,
                                    sortTypeCustomers, true);

                            serverOutput.writeObject(sortedCustomers);
                            serverOutput.writeObject(sortedPB);

                        } catch (Exception e) {
                            return;
                        }

                        break;

                    case "getSortedProductsDashboard":

                        try {

                            String storeNameProductsB = (String) clientInput.readObject();
                            boolean sortTypeProductsB = (boolean) clientInput.readObject();

                            ArrayList<String> sortedProductsB = productsForDashboard(storeNameProductsB,
                                    sortTypeProductsB, false);
                            ArrayList<String> sortedPBSales = productsForDashboard(storeNameProductsB,
                                    sortTypeProductsB, true);

                            serverOutput.writeObject(sortedProductsB);
                            serverOutput.writeObject(sortedPBSales);
                        } catch (Exception e) {
                            return;
                        }

                        break;

                    case "getSortedStores":

                        try {

                            int sortTypeStores = (int) clientInput.readObject();

                            ArrayList<Store> sortedStores = viewSortedStores(sortTypeStores);
                            ArrayList<Integer> sortedProductsSold = viewSortedStoresProducts(sortTypeStores);
                            ArrayList<String> sellerNameList = viewSortedStoresSellers(sortTypeStores);

                            serverOutput.writeObject(sortedStores);
                            serverOutput.writeObject(sortedProductsSold);
                            serverOutput.writeObject(sellerNameList);

                        } catch (Exception e) {
                            return;
                        }

                        break;

                    default:
                        //Invalid or blank request

                }

                boolean writeSuccess = writeData();

                if (!writeSuccess) {
                    LocalTime time = LocalTime.now();
                    time = time.truncatedTo(ChronoUnit.SECONDS);
                    displayText(time.toString() + "\n===> Connection ended: Client " +
                            clientConnection.getInetAddress() + " unable to write to files.\n");
                    return;
                }
            }

        }

        //After client disconnects
        if (currentUser == null) {
            LocalTime time = LocalTime.now();
            time = time.truncatedTo(ChronoUnit.SECONDS);
            displayText(time.toString() + "\n===> Connection ended: Client " +
                    clientConnection.getInetAddress() + " disconnected.\n");
            return;
        } else {
            LocalTime time = LocalTime.now();
            time = time.truncatedTo(ChronoUnit.SECONDS);
            displayText(time.toString() + "\n===> Connection ended: Client " +
                    currentUser.getEmail() + " (" + clientConnection.getInetAddress() + ") disconnected.\n");
            return;
        }

    } //End of Run

    public static void main(String[] args) {

        //Loop to choose port number
        int port;
        while (true) {
            String portString = JOptionPane.showInputDialog(null, "Enter Port Number",
                    "The Marketplace", JOptionPane.QUESTION_MESSAGE);

            port = 0;
            try {
                port = Integer.parseInt(portString);

                if (port <= 1024 || port >= 65535)
                    JOptionPane.showMessageDialog(null, "Port " + port + " not in valid range!",
                            "The Marketplace", JOptionPane.ERROR_MESSAGE);
                else
                    break;

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid Port: Enter Number.",
                        "The Marketplace", JOptionPane.ERROR_MESSAGE);
            }
        }


        //Start Server
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(1000); //Timeout limits blocking of serverSocket- allows it to shut down faster.
        } catch (Exception e) {

            JOptionPane.showMessageDialog(null, "Server failed to start: Restart the program.",
                    "The Marketplalce", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(null, "Started server on port " + port + ".",
                "The Marketplace", JOptionPane.PLAIN_MESSAGE);

        //JFrame
        createGUI();

        displayText("--------------------------------\nServer Initialized. Port: " + port +
                "\n--------------------------------\n");

        //Until stopped, accept new clients and make a thread for them
        serverBoolean = true;
        while (serverBoolean) {
            Socket client = null;
            try {
                client = serverSocket.accept();

            } catch (SocketTimeoutException e1) { //Triggers every timeout
                client = null;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "An error occured connecting to a client",
                        "The Marketplace", JOptionPane.ERROR_MESSAGE);
            }

            //Start a new instance for the client
            if (client != null) {
                MarketplaceServer serverInstance = new MarketplaceServer(client);

                //Display Message
                LocalTime time = LocalTime.now();
                time = time.truncatedTo(ChronoUnit.SECONDS);
                displayText(time.toString() + "\n===> Incoming connection: Client " +
                        client.getInetAddress() + " connected.\n");

                Thread instance = new Thread(serverInstance);
                instance.start();
            }

        }
        JOptionPane.showMessageDialog(null, "Server Ended.", "The Marketplace", JOptionPane.PLAIN_MESSAGE);

        try {
            serverSocket.close();
            return;
        } catch (IOException e) {
            return;
        }


    } //End of Main

    //Basic server JFrame
    public static void createGUI() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Base Frame
                mainFrame = new JFrame("The Marketplace");
                mainFrame.setResizable(true);
                mainFrame.setSize(1000, 700);
                mainFrame.setLocationRelativeTo(null);
                mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


                //Centered Panel
                JPanel panel = new JPanel();
                panel.setLayout(new GridBagLayout());
                mainFrame.setContentPane(panel);

                //Text area displays incoming/outgoing connections
                JTextArea textArea = new JTextArea(35, 85);
                textArea.setBackground(Color.LIGHT_GRAY);
                textArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                textArea.setEditable(false);
                textArea.setSize(800, 600);

                //Scroll pane allows scrolling
                JScrollPane scrollPane = new JScrollPane(textArea);

                panel.add(scrollPane);

                //Document to add text to
                textDocument = textArea.getDocument();

                //Example setting text

                // try {
                //     textDocument.insertString(0, "Test String", null);
                // } catch (BadLocationException e) {
                //     
                // }

                mainFrame.setVisible(true);

                //Closes the server when jframe is closed
                mainFrame.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        serverBoolean = false;
                    }
                });
            }
        });
    }

    public static void displayText(String text) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    textDocument.insertString(textDocument.getLength(), text, null);
                } catch (BadLocationException e) {
                    System.out.print("");
                    //No action needed
                }
            }
        });
    }


    /*
     *
     *  Shared User Methods
     *
     */


    public boolean writeData() {

        try {
            Product.updateProductFile(products, "Products.txt");
            Store.writeStoreFile(stores);
            Customer.writeCustomerFile(customers, "Customers.txt");
            Seller.writeSellerFile(sellers);
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public boolean readData() {

        /*
         * Remember the current user's email so that when data is read,
         * you can set the current user to the matching user from the newly created users from read data
         */
        String emailCurrentUser = null;
        if (currentUser != null)
            emailCurrentUser = currentUser.getEmail();

        try {
            products = Product.readProductFile("Products.txt");
        } catch (FileNotFoundException e) {
            return false;
        }

        try {
            stores = Store.readStore("Store.txt", products);
        } catch (FileFormatException e) {
            return false;
        }

        try {
            customers = Customer.createCustomersFromFile("Customers.txt", products);
        } catch (FileFormatException e) {
            return false;
        }

        try {
            sellers = Seller.getSellers(stores);
        } catch (Exception e) {
            return false;
        }

        if (currentUser != null) {
            //Set the current user to the one in the new arraylists
            for (Seller s : sellers) {
                if (s.getEmail().equals(emailCurrentUser))
                    currentUser = s;
            }
            for (Customer c: customers) {
                if (c.getEmail().equals(emailCurrentUser))
                    currentUser = c;
            }
        }


        //Update Customer Carts

        for (Customer c : customers) {

            ArrayList<Product> cart = c.getShoppingCart();

            //Make sure cart does not have an impossible quantity

            //For each product in the cart
            for (int i = cart.size() - 1; i >= 0; i--) {
                Product cartProduct = cart.get(i);

                int quantityInCart = 0;
                //Add up how many of this product are in the cart
                for (int j = 0; j < cart.size(); j++) {
                    if (cart.get(j).equals(cartProduct)) {
                        quantityInCart += cart.get(j).getQuantity();
                    }
                }

                Product globalProduct = null;
                //Find the global product
                for (Product p : products) {
                    if (p.equals(cartProduct))
                        globalProduct = p;
                }

                //If the global product doesn't exsist any more
                // Or if the global quantity is less than the total in the cart
                if (globalProduct == null || globalProduct.getQuantity() < quantityInCart)
                    cart.remove(i);

            }

            //Format carts
            ArrayList<Product> formattedCart = new ArrayList<Product>();

            //For each product
            for (Product p : cart) {
                if (formattedCart.contains(p)) {
                    //If the formatted list already has something under the same name the product

                    int index = formattedCart.indexOf(p);
                    Product productToAddTo = formattedCart.get(index);

                    //Add the quantity of the product to the formatted list's product
                    productToAddTo.setQuantity(productToAddTo.getQuantity() + p.getQuantity());

                } else {
                    //Add a shallow copy
                    formattedCart.add(new Product(p.getName(), p.getStoreName(),
                            p.getDesc(), p.getQuantity(), p.getPrice(),
                            p.getSales(), p.getRevenue()));
                }
            }

            c.setShoppingCart(formattedCart);

        }

        return true;
    }

    // LOG IN AND SIGN UP

    /**
     * Allows a user to log in to their account.
     *
     * @param email email entered by returning user
     * @param password password enter by returning user
     * @return boolean for whether or not log in was successful
     */
    public boolean logIn(String email, String password) {
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getEmail().equals(email) && customers.get(i).getPassword().equals(password)) {
                currentUser = customers.get(i);
                return true;
            }
        }

        for (int i = 0; i < sellers.size(); i++) {
            if (sellers.get(i).getEmail().equals(email) && sellers.get(i).getPassword().equals(password)) {
                currentUser = sellers.get(i);
                return true;
            }
        }

        return false;
    }


    /**
     * Allows a customer account to be created.
     *
     * @param name name entered by new customer
     * @param email email entered by new customer
     * @param password password entered by new customer
     * @return boolean for whether or not sign up was successful
     */
    public boolean signUpCustomer(String name, String email, String password) {
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getEmail().equals(email)) {
                return false;
            }
        }

        for (int i = 0; i < sellers.size(); i++) {
            if (sellers.get(i).getEmail().equals(email)) {
                return false;
            }
        }

        Customer newCustomer = new Customer(name, email, password);
        customers.add(newCustomer);
        currentUser = newCustomer;

        return true;
    }

    /**
     * Allows a seller account to be created.
     *
     * @param name name entered by new seller
     * @param email email entered by new seller
     * @param password password entered by new seller
     * @return boolean for whether or not sign up was successful
     */
    public boolean signUpSeller(String name, String email, String password) {
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getEmail().equals(email)) {
                return false;
            }
        }

        for (int i = 0; i < sellers.size(); i++) {
            if (sellers.get(i).getEmail().equals(email)) {
                return false;
            }
        }

        ArrayList<Store> newStores = new ArrayList<Store>();
        Seller newSeller = new Seller(email, name, password, newStores);
        sellers.add(newSeller);
        currentUser = newSeller;

        return true;
    }

    //Account Details
    public boolean deleteAccount() {
        if (currentUser instanceof Customer) {
            for (int i = customers.size() - 1; i >= 0; i--) {
                if (customers.get(i).equals(currentUser)) {
                    customers.remove(i);
                    return true;
                }
            }
        } else {
            for (int i = sellers.size() - 1; i >= 0; i--) {
                if (sellers.get(i).equals(currentUser)) {
                    sellers.remove(i);
                    return true;
                }
            }
        }

        return false;
    }

    public boolean editAccount(String email, String password, String name) {

        //Check if email exists
        ArrayList<String> emails = new ArrayList<String>();
        for (Customer c : customers)
            emails.add(c.getEmailAddress());
        for (Seller s : sellers)
            emails.add(s.getEmail());


        if ( !email.equals(currentUser.getEmail()) && emails.indexOf(email) != -1) {
            return false;
        } else {
            currentUser.setEmail(email);
        }

        currentUser.setName(name);
        currentUser.setPassword(password);

        return true;

    }

    public ArrayList<String> getAccountDetails() {

        ArrayList<String> details = new ArrayList<String>(List.of(currentUser.getName(), currentUser.getEmail(),
                currentUser.getPassword()));

        return details;
    }


    /*
     *
     *  Seller Methods
     *
     */

    public ArrayList<Store> getSellerStores() {

        if (!(currentUser instanceof Seller)) {
            return null;
        } else {
            Seller s = (Seller) currentUser;
            return s.getStores();
        }

    }

    public ArrayList<Product> getProductFromStore(Store s) {
        return s.getProducts();
    }

    public boolean createNewProduct(String storeName, String name, String description, double price, int quantity) {

        Product newProduct = new Product(name, storeName, description, quantity, price, 0, 0);

        //Check if product exists
        for (Product p: products) {
            if (p.equals(newProduct))
                return false;
        }

        //Storename should be valid
        Store chosenStore = null;

        //Find the store
        for (Store s: stores) {
            if (s.getStoreName().equals(storeName))
                chosenStore = s;
        }

        if (chosenStore == null)
            return false;

        chosenStore.addProduct(newProduct);
        products.add(newProduct);
        return true;
    }

    //CSV will have to be read in client
    public boolean importProducts(String storeName, ArrayList<String> csvLines) {

        //To be added
        ArrayList<Product> newProducts = new ArrayList<Product>();

        //Storename should be valid
        Store chosenStore = null;
        //Find the store
        for (Store s: stores) {
            if (s.getStoreName().equals(storeName))
                chosenStore = s;
        }

        //Each csv line is a product
        for (String s : csvLines) {

            Product newProduct = null;
            try {
                newProduct = Product.parseProduct(s);
            } catch (FileFormatException e) {
                return false;
            }

            //Check if product exsists
            for (Product p: products) {
                if (p.equals(newProduct))
                    return false;
            }

            //Ensure new product from csv has correct store name
            if (!(newProduct.getStoreName().equals(chosenStore.getStoreName()))) {
                return false;
            }

            newProducts.add(newProduct);

        } //This will end when all csv lines read/no problems with it

        //Add new products
        for (Product p : newProducts) {
            products.add(p);
            chosenStore.addProduct(p);
        }
        return true;

    }

    public boolean editProduct(Product editingProduct, String newName, double newPrice,
                               int newQuantity, String newDesc) {

        //Edit in global list of products
        for (Product p : products) {
            if (p.equals(editingProduct)) {
                p.setName(newName);
                p.setPrice(newPrice);
                p.setQuantity(newQuantity);
                p.setDesc(newDesc);
            }
        }


        Store chosenStore = null;
        //Find the store
        for (Store s: stores) {
            if (s.getStoreName().equals(editingProduct.getStoreName()))
                chosenStore = s;
        }

        if (chosenStore == null) //Probably shouldn't happen
            return false;

        //Edit in the store's list of products (could be unncessesary)
        for (Product p : chosenStore.getProducts()) {
            if (p.equals(editingProduct)) {
                p.setName(newName);
                p.setPrice(newPrice);
                p.setQuantity(newQuantity);
                p.setDesc(newDesc);
            }
        }

        return true;

    }

    public boolean removeProduct(Product removingProduct) {

        //Remove in global list of products
        for (int i = products.size() - 1; i >= 0; i--) {
            if (products.get(i).equals(removingProduct)) {
                products.remove(i);
            }
        }


        Store chosenStore = null;
        //Find the store
        for (Store s: stores) {
            if (s.getStoreName().equals(removingProduct.getStoreName()))
                chosenStore = s;
        }

        if (chosenStore == null) //Probably shouldn't happen
            return false;

        //Remove in the store's list of products
        for (int i = chosenStore.getProducts().size() - 1; i >= 0; i--) {
            if (chosenStore.getProducts().get(i).equals(removingProduct)) {
                chosenStore.getProducts().remove(i);
            }
        }

        return true;
    }

    //Returns a list of strings that should be written to a CSV in the client
    public ArrayList<String> exportProducts(String storeName) {

        Store chosenStore = null;
        //Find the store
        for (Store s: stores) {
            if (s.getStoreName().equals(storeName))
                chosenStore = s;
        }

        ArrayList<String> csvLines = new ArrayList<String>();

        for (Product p : chosenStore.getProducts()) {
            csvLines.add(new String(p.toString()));
        }

        return csvLines;

    }

    public ArrayList<Sale> getStoreSaleHistory(String storeName) {

        Store chosenStore = null;
        //Find the store
        for (Store s: stores) {
            if (s.getStoreName().equals(storeName))
                chosenStore = s;
        }

        return chosenStore.getSaleHistory();
    }

    //List of customer names or number of items purchased
    public ArrayList<String> customersWhoPurchasedFromStore(String storeName, boolean lowToHigh,
                                                            boolean returnNumbers) {

        Store chosenStore = null;
        //Find the store
        for (Store s: stores) {
            if (s.getStoreName().equals(storeName))
                chosenStore = s;
        }

        ArrayList<Sale> saleHistory = chosenStore.getSaleHistory();

        ArrayList<String> customerEmails = new ArrayList<String>();
        ArrayList<Integer> productsBoughtByCustomer = new ArrayList<Integer>();

        //Adding up purchases by customer in store 
        for (Sale s : saleHistory) {
            String email = s.getCustomerEmail();
            int quantity = s.getQuantity();

            //New customer to list
            if (customerEmails.indexOf(email) == -1) {
                customerEmails.add(email);
                productsBoughtByCustomer.add(quantity);
            } else { //Exsisting Customer
                int index = customerEmails.indexOf(email);
                productsBoughtByCustomer.set(index, (productsBoughtByCustomer.get(index) + quantity));
            }

        }

        //Sorts by numbers, parallel sorting names
        for (int i = 0; i < productsBoughtByCustomer.size(); i++) {

            int min = productsBoughtByCustomer.get(i);
            int minIndex = i;
            for (int j = i; j < productsBoughtByCustomer.size(); j++) {
                if (productsBoughtByCustomer.get(j) <= min) {
                    min = productsBoughtByCustomer.get(j);
                    minIndex = j;
                }
            }

            int temp = productsBoughtByCustomer.get(i);
            productsBoughtByCustomer.set(i, productsBoughtByCustomer.get(minIndex));
            productsBoughtByCustomer.set(minIndex, temp);

            String tempName = customerEmails.get(i);
            customerEmails.set(i, customerEmails.get(minIndex));
            customerEmails.set(minIndex, tempName);
        }


        if (!lowToHigh) {

            ArrayList<Integer> tempIntList = new ArrayList<Integer>();
            ArrayList<String> tempStringList = new ArrayList<String>();

            //Reverse both lists
            for (int i = productsBoughtByCustomer.size() - 1; i >= 0; i--) {
                tempIntList.add(productsBoughtByCustomer.get(i));
                tempStringList.add(customerEmails.get(i));
            }

            productsBoughtByCustomer = tempIntList;
            customerEmails = tempStringList;

        }

        if (!returnNumbers) {
            return customerEmails;
        } else {
            ArrayList<String> productsBoughtStringList = new ArrayList<String>();
            //Convert list of ints to strings of numbers
            for (Integer i : productsBoughtByCustomer) {
                productsBoughtStringList.add(i.toString());
            }

            return productsBoughtStringList;
        }
    }

    public ArrayList<String> productsForDashboard(String storeName, boolean lowToHigh, boolean returnNumbers) {

        Store chosenStore = null;
        //Find the store
        for (Store s: stores) {
            if (s.getStoreName().equals(storeName))
                chosenStore = s;
        }

        ArrayList<Product> storeProducts = chosenStore.getProducts();

        //Sorts storeProducts by sales
        for (int i = 0; i < storeProducts.size(); i++) {

            int min = storeProducts.get(i).getSales();
            int minIndex = i;
            for (int j = i; j < storeProducts.size(); j++) {
                if (storeProducts.get(j).getSales() <= min) {
                    min = storeProducts.get(j).getSales();
                    minIndex = j;
                }
            }

            Product temp = storeProducts.get(i);
            storeProducts.set(i, storeProducts.get(minIndex));
            storeProducts.set(minIndex, temp);

        }

        if (!lowToHigh) {

            ArrayList<Product> tempList = new ArrayList<Product>();

            //Reverse both lists
            for (int i = storeProducts.size() - 1; i >= 0; i--) {
                tempList.add(storeProducts.get(i));
            }

            storeProducts = tempList;

        }

        if (returnNumbers) {
            ArrayList<String> numberList = new ArrayList<String>();
            for (Product p : storeProducts) {
                numberList.add("" + p.getSales());
            }
            return numberList;
        } else {
            ArrayList<String> list = new ArrayList<String>();
            for (Product p : storeProducts) {
                list.add(p.getName());
            }
            return list;
        }

    }

    public int numItemsInShoppingCart() {

        Seller currentSeller = (Seller) currentUser;

        ArrayList<Product> productsInCart = new ArrayList<Product>();
        //Get all products in shopping carts
        for (Customer c : customers) {
            ArrayList<Product> p = c.getShoppingCart();
            productsInCart.addAll(p);
        }

        ArrayList<Store> sellerStores = currentSeller.getStores();
        ArrayList<String> sellerStoreNames = new ArrayList<String>();

        for (Store s : sellerStores) {
            sellerStoreNames.add(s.getStoreName());
        }

        //If the product is from a store owned by seller, add the quantity in cart
        int productCount = 0;
        for (Product p : productsInCart) {
            if (sellerStoreNames.indexOf(p.getStoreName()) != -1) {
                productCount += p.getQuantity(); //Can probably be a ++ rather than getQuantity
            }
        }

        return productCount;

    }

    public ArrayList<Product> productsInShoppingCarts() {

        Seller currentSeller = (Seller) currentUser;

        ArrayList<Product> productsInCart = new ArrayList<Product>();
        //Get all products in shopping carts
        for (Customer c : customers) {
            ArrayList<Product> p = c.getShoppingCart();
            productsInCart.addAll(p);
        }

        ArrayList<Store> sellerStores = currentSeller.getStores();
        ArrayList<String> sellerStoreNames = new ArrayList<String>();

        //Get a list of stores owned by this seller
        for (Store s : sellerStores) {
            sellerStoreNames.add(s.getStoreName());
        }

        ArrayList<Product> sellerProductsInCarts = new ArrayList<Product>();
        //If the product is from a store owned by seller, add it to list
        for (Product p : productsInCart) {
            if (sellerStoreNames.indexOf(p.getStoreName()) != -1) {
                sellerProductsInCarts.add(p);
            }
        }

        //Format this list to group same products together
        ArrayList<Product> formattedList = new ArrayList<Product>();

        //For each product in a cart 
        for (Product p : sellerProductsInCarts) {
            if (formattedList.contains(p)) {
                //If the formatted list already has something under the same name the product

                int index = formattedList.indexOf(p);
                Product productToAddTo = formattedList.get(index);

                //Add the quantity of the product to the formatted list's product
                productToAddTo.setQuantity(productToAddTo.getQuantity() + p.getQuantity());

            } else {
                //Add a shallow copy
                formattedList.add(new Product(p.getName(), p.getStoreName(),
                        p.getDesc(), p.getQuantity(), p.getPrice(),
                        p.getSales(), p.getRevenue()));
            }
        }

        return formattedList;
    }

    public boolean createStore(String storeName) {

        //Check if store exists
        for (Store s : stores) {
            if (s.getStoreName().equals(storeName)) {
                return false;
            }
        }

        Store newStore = new Store(storeName);

        //Add to list of stores and Seller's personal list of stores
        stores.add(newStore);

        Seller currentSeller = (Seller) currentUser;
        currentSeller.getStores().add(newStore);

        return true;
    }

    /*
     *
     *  Customer Methods
     *
     */


    /**
     * Allows the client to have access to the products.
     *
     * @return ArrayList of products
     */
    public ArrayList<Product> getProducts() {
        return products;
    }

    /**
     * Allows the client to have access to a list of products that contain the given search.
     *
     * @param search keyword for search
     * @return ArrayList of products that contain search in either name, storeName, or desc
     */
    public ArrayList<Product> getSearchedProducts(String search) {
        ArrayList<Product> searchedProducts = new ArrayList<Product>();

        for (int i = 0; i < products.size(); i++) {
            Product check = products.get(i);
            if (check.getName().toLowerCase().indexOf(search.toLowerCase()) > -1 ||
                    check.getStoreName().toLowerCase().indexOf(search.toLowerCase()) > -1 ||
                    check.getDesc().toLowerCase().indexOf(search.toLowerCase()) > -1) {
                searchedProducts.add(check);
            }
        }

        return searchedProducts; //Client checks if empty
    }

    /**
     * Allows the client to have access to a list of sorted products.
     *
     * @param sort type of sort
     * @return ArrayList of products that is sorted based on the requested sort
     */
    public ArrayList<Product> getSortedProducts(int sort) {
        ArrayList<Product> sortedProducts = new ArrayList<Product>();

        if (products.size() > 0)
            sortedProducts.add(products.get(0));

        switch (sort) {
            case 0: //price lowtohigh 
                for (int i = 0; i < products.size(); i++) {
                    for (int j = 0; j < sortedProducts.size(); j++) {
                        if (products.get(i).getPrice() < sortedProducts.get(j).getPrice()) {
                            sortedProducts.add(j, products.get(i));
                            j = sortedProducts.size();
                        }
                    }
                    if (sortedProducts.indexOf(products.get(i)) < 0) {
                        sortedProducts.add(products.get(i));
                    }
                }
                break;
            case 1: //price hightolow
                for (int i = 0; i < products.size(); i++) {
                    for (int j = 0; j < sortedProducts.size(); j++) {
                        if (products.get(i).getPrice() > sortedProducts.get(j).getPrice()) {
                            sortedProducts.add(j, products.get(i));
                            j = sortedProducts.size();
                        }
                    }
                    if (sortedProducts.indexOf(products.get(i)) < 0) {
                        sortedProducts.add(products.get(i));
                    }
                }
                break;
            case 2: //quantity lowtohigh
                for (int i = 0; i < products.size(); i++) {
                    for (int j = 0; j < sortedProducts.size(); j++) {
                        if (products.get(i).getQuantity() < sortedProducts.get(j).getQuantity()) {
                            sortedProducts.add(j, products.get(i));
                            j = sortedProducts.size();
                        }
                    }
                    if (sortedProducts.indexOf(products.get(i)) < 0) {
                        sortedProducts.add(products.get(i));
                    }
                }
                break;
            case 3: //quantity hightolow
                for (int i = 0; i < products.size(); i++) {
                    for (int j = 0; j < sortedProducts.size(); j++) {
                        if (products.get(i).getQuantity() > sortedProducts.get(j).getQuantity()) {
                            sortedProducts.add(j, products.get(i));
                            j = sortedProducts.size();
                        }
                    }
                    if (sortedProducts.indexOf(products.get(i)) < 0) {
                        sortedProducts.add(products.get(i));
                    }
                }
                break;
        }

        return sortedProducts;
    }

    /**
     * Allows the client to access the details of a chosen product.
     *
     * @param product selected product
     * @return details of selected product
     */
    public ArrayList<String> getDetails(Product product) {
        ArrayList<String> details = new ArrayList<String>();

        details.add(product.getName());
        details.add(product.getStoreName());
        details.add(product.getPrice() + "");
        details.add(product.getDesc());
        details.add(product.getQuantity() + "");

        return details;
    }


    /**
     * Allows the client to access the purchase history of the current customer.
     *
     * @return purchase history of current customer
     */
    public ArrayList<Purchase> getHistory() {
        Customer currentCustomer = (Customer) currentUser;

        return currentCustomer.getPurchaseHistory();
    }

    /**
     * Allows the client to export the current customer's history
     *
     * @return a list of strings of purchase history
     */
    public ArrayList<String> exportHistory() {
        Customer currentCustomer = (Customer) currentUser;

        return currentCustomer.exportPurchaseHistoryStrings();
    }

    /**
     * Allows the client to access the shopping cart of the current customer.
     *
     * @return shopping cart of current customer
     */
    public ArrayList<Product> getCart() {
        Customer currentCustomer = (Customer) currentUser;

        return currentCustomer.getShoppingCart();

    }


    /**
     * Allows the client to have access to the stores.
     *
     * @return ArrayList of stores
     */
    public ArrayList<Store> getStores() {
        return stores;
    }

    /**
     * Allows the client to access a list of sorted stores.
     *
     * @param sort type of sort
     * @return ArrayList of sorted stores
     */
    public ArrayList<Store> viewSortedStores(int sort) {
        //1 total lowhigh; 2 total highlow; 3 customer lowhigh; 4 customer highlow
        Customer currentCustomer = (Customer) currentUser;

        ArrayList<Store> sortedStores = new ArrayList<Store>();
        sortedStores.add(stores.get(0));

        switch (sort) {
            case 1:
                for (int i = 0; i < stores.size(); i++) {
                    Store check = stores.get(i);
                    int checkSales = 0;
                    for (int j = 0; j < check.getSaleHistory().size(); j++) {
                        checkSales += check.getSaleHistory().get(j).getQuantity();
                    }
                    for (int k = 0; k < sortedStores.size(); k++) {
                        Store current = sortedStores.get(k);
                        int currentSales = 0;
                        for (int l = 0; l < current.getSaleHistory().size(); l++) {
                            currentSales += current.getSaleHistory().get(l).getQuantity();
                        }

                        if (checkSales < currentSales) {
                            sortedStores.add(k, check);
                            k = sortedStores.size();
                        }
                    }

                    if (sortedStores.indexOf(check) < 0) {
                        sortedStores.add(check);
                    }
                }
                break;
            case 2:
                for (int i = 0; i < stores.size(); i++) {
                    Store check = stores.get(i);
                    int checkSales = 0;
                    for (int j = 0; j < check.getSaleHistory().size(); j++) {
                        checkSales += check.getSaleHistory().get(j).getQuantity();
                    }
                    for (int k = 0; k < sortedStores.size(); k++) {
                        Store current = sortedStores.get(k);
                        int currentSales = 0;
                        for (int l = 0; l < current.getSaleHistory().size(); l++) {
                            currentSales += current.getSaleHistory().get(l).getQuantity();
                        }

                        if (checkSales > currentSales) {
                            sortedStores.add(k, check);
                            k = sortedStores.size();
                        }
                    }

                    if (sortedStores.indexOf(check) < 0) {
                        sortedStores.add(check);
                    }
                }
                break;
            case 3:
                for (int i = 0; i < stores.size(); i++) {
                    Store check = stores.get(i);
                    int checkSales = 0;
                    for (int j = 0; j < check.getSaleHistory().size(); j++) {
                        Sale saleCheck = check.getSaleHistory().get(j);
                        if (saleCheck.getCustomerEmail().equals(currentCustomer.getEmail())) {
                            checkSales += saleCheck.getQuantity();
                        }
                    }
                    for (int k = 0; k < sortedStores.size(); k++) {
                        Store current = sortedStores.get(k);
                        int currentSales = 0;
                        for (int l = 0; l < current.getSaleHistory().size(); l++) {
                            Sale saleCurrent = current.getSaleHistory().get(l);
                            if (saleCurrent.getCustomerEmail().equals(currentCustomer.getEmail())) {
                                currentSales += saleCurrent.getQuantity();
                            }
                        }

                        if (checkSales < currentSales) {
                            sortedStores.add(k, check);
                            k = sortedStores.size();
                        }
                    }

                    if (sortedStores.indexOf(check) < 0) {
                        sortedStores.add(check);
                    }
                }
                break;
            case 4:
                for (int i = 0; i < stores.size(); i++) {
                    Store check = stores.get(i);
                    int checkSales = 0;
                    for (int j = 0; j < check.getSaleHistory().size(); j++) {
                        Sale saleCheck = check.getSaleHistory().get(j);
                        if (saleCheck.getCustomerEmail().equals(currentCustomer.getEmail())) {
                            checkSales += saleCheck.getQuantity();
                        }
                    }
                    for (int k = 0; k < sortedStores.size(); k++) {
                        Store current = sortedStores.get(k);
                        int currentSales = 0;
                        for (int l = 0; l < current.getSaleHistory().size(); l++) {
                            Sale saleCurrent = current.getSaleHistory().get(l);
                            if (saleCurrent.getCustomerEmail().equals(currentCustomer.getEmail())) {
                                currentSales += saleCurrent.getQuantity();
                            }
                        }

                        if (checkSales > currentSales) {
                            sortedStores.add(k, check);
                            k = sortedStores.size();
                        }
                    }

                    if (sortedStores.indexOf(check) < 0) {
                        sortedStores.add(check);
                    }
                }
        }

        return sortedStores;
    }

    /**
     * Allows the client to access a list of sellers in order of sorted stores.
     *
     * @param sort type of sort
     * @return ArrayList of sorted seller names
     */
    public ArrayList<String> viewSortedStoresSellers(int sort) {
        //1 total lowhigh; 2 total highlow; 3 customer lowhigh; 4 customer highlow
        Customer currentCustomer = (Customer) currentUser;

        ArrayList<Store> sortedStores = new ArrayList<Store>();
        sortedStores.add(stores.get(0));

        switch (sort) {
            case 1:
                for (int i = 0; i < stores.size(); i++) {
                    Store check = stores.get(i);
                    int checkSales = 0;
                    for (int j = 0; j < check.getSaleHistory().size(); j++) {
                        checkSales += check.getSaleHistory().get(j).getQuantity();
                    }
                    for (int k = 0; k < sortedStores.size(); k++) {
                        Store current = sortedStores.get(k);
                        int currentSales = 0;
                        for (int l = 0; l < current.getSaleHistory().size(); l++) {
                            currentSales += current.getSaleHistory().get(l).getQuantity();
                        }

                        if (checkSales < currentSales) {
                            sortedStores.add(k, check);
                            k = sortedStores.size();
                        }
                    }

                    if (sortedStores.indexOf(check) < 0) {
                        sortedStores.add(check);
                    }
                }
                break;
            case 2:
                for (int i = 0; i < stores.size(); i++) {
                    Store check = stores.get(i);
                    int checkSales = 0;
                    for (int j = 0; j < check.getSaleHistory().size(); j++) {
                        checkSales += check.getSaleHistory().get(j).getQuantity();
                    }
                    for (int k = 0; k < sortedStores.size(); k++) {
                        Store current = sortedStores.get(k);
                        int currentSales = 0;
                        for (int l = 0; l < current.getSaleHistory().size(); l++) {
                            currentSales += current.getSaleHistory().get(l).getQuantity();
                        }

                        if (checkSales > currentSales) {
                            sortedStores.add(k, check);
                            k = sortedStores.size();
                        }
                    }

                    if (sortedStores.indexOf(check) < 0) {
                        sortedStores.add(check);
                    }
                }
                break;
            case 3:
                for (int i = 0; i < stores.size(); i++) {
                    Store check = stores.get(i);
                    int checkSales = 0;
                    for (int j = 0; j < check.getSaleHistory().size(); j++) {
                        Sale saleCheck = check.getSaleHistory().get(j);
                        if (saleCheck.getCustomerEmail().equals(currentCustomer.getEmail())) {
                            checkSales += saleCheck.getQuantity();
                        }
                    }
                    for (int k = 0; k < sortedStores.size(); k++) {
                        Store current = sortedStores.get(k);
                        int currentSales = 0;
                        for (int l = 0; l < current.getSaleHistory().size(); l++) {
                            Sale saleCurrent = current.getSaleHistory().get(l);
                            if (saleCurrent.getCustomerEmail().equals(currentCustomer.getEmail())) {
                                currentSales += saleCurrent.getQuantity();
                            }
                        }

                        if (checkSales < currentSales) {
                            sortedStores.add(k, check);
                            k = sortedStores.size();
                        }
                    }

                    if (sortedStores.indexOf(check) < 0) {
                        sortedStores.add(check);
                    }
                }
                break;
            case 4:
                for (int i = 0; i < stores.size(); i++) {
                    Store check = stores.get(i);
                    int checkSales = 0;
                    for (int j = 0; j < check.getSaleHistory().size(); j++) {
                        Sale saleCheck = check.getSaleHistory().get(j);
                        if (saleCheck.getCustomerEmail().equals(currentCustomer.getEmail())) {
                            checkSales += saleCheck.getQuantity();
                        }
                    }
                    for (int k = 0; k < sortedStores.size(); k++) {
                        Store current = sortedStores.get(k);
                        int currentSales = 0;
                        for (int l = 0; l < current.getSaleHistory().size(); l++) {
                            Sale saleCurrent = current.getSaleHistory().get(l);
                            if (saleCurrent.getCustomerEmail().equals(currentCustomer.getEmail())) {
                                currentSales += saleCurrent.getQuantity();
                            }
                        }

                        if (checkSales > currentSales) {
                            sortedStores.add(k, check);
                            k = sortedStores.size();
                        }
                    }

                    if (sortedStores.indexOf(check) < 0) {
                        sortedStores.add(check);
                    }
                }
        }

        //Modification- take store list and give equivalent seller list (as strings of names)

        ArrayList<String> sellerNames = new ArrayList<String>();

        for (Store s : sortedStores) {

            for (Seller seller : sellers) {

                if (seller.getStores().contains(s)) {
                    sellerNames.add(seller.getName() + " (" + seller.getEmail() + ")" );
                    break;
                }

            }

        }

        return sellerNames;
    }

    /**
     * Allows the client to have a list of the integer values associated with
     * the products sold at every given store.
     *
     * @param sort type of sort to be done
     * @return ArrayList of Integers
     */
    public ArrayList<Integer> viewSortedStoresProducts(int sort) {
        //1 total lowhigh; 2 total highlow; 3 customer lowhigh; 4 customer highlow
        Customer currentCustomer = (Customer) currentUser;

        ArrayList<Integer> productsSold = new ArrayList<Integer>();

        ArrayList<Store> sortedStores = new ArrayList<Store>();
        sortedStores.add(stores.get(0));

        switch (sort) {
            case 1:
                Store firstSort1 = stores.get(0);
                int firstSort1Sales = 0;
                for (int i = 0; i < firstSort1.getSaleHistory().size(); i++) {
                    firstSort1Sales += firstSort1.getSaleHistory().get(i).getQuantity();
                }
                productsSold.add(firstSort1Sales);

                for (int i = 0; i < stores.size(); i++) {
                    Store check = stores.get(i);
                    int checkSales = 0;
                    for (int j = 0; j < check.getSaleHistory().size(); j++) {
                        checkSales += check.getSaleHistory().get(j).getQuantity();
                    }
                    for (int k = 0; k < sortedStores.size(); k++) {
                        Store current = sortedStores.get(k);
                        int currentSales = 0;
                        for (int l = 0; l < current.getSaleHistory().size(); l++) {
                            currentSales += current.getSaleHistory().get(l).getQuantity();
                        }

                        if (checkSales < currentSales) {
                            sortedStores.add(k, check);
                            productsSold.add(k, checkSales);
                            k = sortedStores.size();
                        }
                    }

                    if (sortedStores.indexOf(check) < 0) {
                        sortedStores.add(check);
                        productsSold.add(checkSales);
                    }
                }
                break;
            case 2:
                Store firstSort2 = stores.get(0);
                int firstSort2Sales = 0;
                for (int i = 0; i < firstSort2.getSaleHistory().size(); i++) {
                    firstSort2Sales += firstSort2.getSaleHistory().get(i).getQuantity();
                }
                productsSold.add(firstSort2Sales);

                for (int i = 0; i < stores.size(); i++) {
                    Store check = stores.get(i);
                    int checkSales = 0;
                    for (int j = 0; j < check.getSaleHistory().size(); j++) {
                        checkSales += check.getSaleHistory().get(j).getQuantity();
                    }
                    for (int k = 0; k < sortedStores.size(); k++) {
                        Store current = sortedStores.get(k);
                        int currentSales = 0;
                        for (int l = 0; l < current.getSaleHistory().size(); l++) {
                            currentSales += current.getSaleHistory().get(l).getQuantity();
                        }

                        if (checkSales > currentSales) {
                            sortedStores.add(k, check);
                            productsSold.add(k, checkSales);
                            k = sortedStores.size();
                        }
                    }

                    if (sortedStores.indexOf(check) < 0) {
                        sortedStores.add(check);
                        productsSold.add(checkSales);
                    }
                }
                break;
            case 3:
                Store firstSort3 = stores.get(0);
                int firstSort3Sales = 0;
                for (int i = 0; i < firstSort3.getSaleHistory().size(); i++) {
                    Sale saleCheck = firstSort3.getSaleHistory().get(i);
                    if (saleCheck.getCustomerEmail().equals(currentCustomer.getEmail())) {
                        firstSort3Sales += firstSort3.getSaleHistory().get(i).getQuantity();
                    }
                }
                productsSold.add(firstSort3Sales);

                for (int i = 0; i < stores.size(); i++) {
                    Store check = stores.get(i);
                    int checkSales = 0;
                    for (int j = 0; j < check.getSaleHistory().size(); j++) {
                        Sale saleCheck = check.getSaleHistory().get(j);
                        if (saleCheck.getCustomerEmail().equals(currentCustomer.getEmail())) {
                            checkSales += saleCheck.getQuantity();
                        }
                    }
                    for (int k = 0; k < sortedStores.size(); k++) {
                        Store current = sortedStores.get(k);
                        int currentSales = 0;
                        for (int l = 0; l < current.getSaleHistory().size(); l++) {
                            Sale saleCurrent = current.getSaleHistory().get(l);
                            if (saleCurrent.getCustomerEmail().equals(currentCustomer.getEmail())) {
                                currentSales += saleCurrent.getQuantity();
                            }
                        }

                        if (checkSales < currentSales) {
                            sortedStores.add(k, check);
                            productsSold.add(k, checkSales);
                            k = sortedStores.size();
                        }
                    }

                    if (sortedStores.indexOf(check) < 0) {
                        sortedStores.add(check);
                        productsSold.add(checkSales);
                    }
                }
                break;
            case 4:
                Store firstSort4 = stores.get(0);
                int firstSort4Sales = 0;
                for (int i = 0; i < firstSort4.getSaleHistory().size(); i++) {
                    Sale saleCheck = firstSort4.getSaleHistory().get(i);
                    if (saleCheck.getCustomerEmail().equals(currentCustomer.getEmail())) {
                        firstSort4Sales += firstSort4.getSaleHistory().get(i).getQuantity();
                    }
                }
                productsSold.add(firstSort4Sales);

                for (int i = 0; i < stores.size(); i++) {
                    Store check = stores.get(i);
                    int checkSales = 0;
                    for (int j = 0; j < check.getSaleHistory().size(); j++) {
                        Sale saleCheck = check.getSaleHistory().get(j);
                        if (saleCheck.getCustomerEmail().equals(currentCustomer.getEmail())) {
                            checkSales += saleCheck.getQuantity();
                        }
                    }
                    for (int k = 0; k < sortedStores.size(); k++) {
                        Store current = sortedStores.get(k);
                        int currentSales = 0;
                        for (int l = 0; l < current.getSaleHistory().size(); l++) {
                            Sale saleCurrent = current.getSaleHistory().get(l);
                            if (saleCurrent.getCustomerEmail().equals(currentCustomer.getEmail())) {
                                currentSales += saleCurrent.getQuantity();
                            }
                        }

                        if (checkSales > currentSales) {
                            sortedStores.add(k, check);
                            productsSold.add(k, checkSales);
                            k = sortedStores.size();
                        }
                    }

                    if (sortedStores.indexOf(check) < 0) {
                        sortedStores.add(check);
                        productsSold.add(checkSales);
                    }
                }
        }

        return productsSold;
    }


    /**
     * Allows the current customer to add a product to their cart. 
     *
     * @param product product object to be added to the customer's cart
     */
    public boolean addToCart(Product product) {
        Customer currentCustomer = (Customer) currentUser;

        //Shallow copy in cart, quantity 1
        Product productToAdd = new Product(product.getName(), product.getStoreName(),
                product.getDesc(), 1, product.getPrice(),
                product.getSales(), product.getRevenue());

        //Check whether the quantity in cart is less than the global quantity available
        ArrayList<Product> cart = currentCustomer.getShoppingCart();

        int quantityInCart = 1;
        //Add up how many of this product are in the cart
        for (int j = 0; j < cart.size(); j++) {
            if (cart.get(j).equals(product)) {
                quantityInCart += cart.get(j).getQuantity();
            }
        }

        Product globalProduct = null;
        //Find the global product
        for (Product p : products) {
            if (p.equals(product))
                globalProduct = p;
        }

        //If the global quantity is less than the total in the cart, can't add it
        if (globalProduct == null || globalProduct.getQuantity() < quantityInCart) {
            return false;
        } else {
            currentCustomer.addProduct(productToAdd);
            return true;
        }

    }

    /**
     * Allows the current customer to remove a product from their cart.
     *
     * @param product product object to be added to the customer's cart
     */
    public void removeFromCart(Product product) {
        Customer currentCustomer = (Customer) currentUser;

        currentCustomer.removeProduct(product);
    }

    /**
     * Allows the current customer to purchase products from their cart.
     */
    public boolean purchaseCart() {
        Customer currentCustomer = (Customer) currentUser;

        ArrayList<Product> cart = currentCustomer.getShoppingCart();

        if (cart.size() == 0) {
            return false;
        }

        //Check whether purchase can be made
        for (int i = cart.size() - 1; i >= 0; i--) {
            Product cartProduct = cart.get(i);

            int quantityInCart = 0;
            //Add up how many of this product are in the cart
            for (int j = 0; j < cart.size(); j++) {
                if (cart.get(j).equals(cartProduct)) {
                    quantityInCart += cart.get(j).getQuantity();
                }
            }

            Product globalProduct = null;
            //Find the global product
            for (Product p : products) {
                if (p.equals(cartProduct))
                    globalProduct = p;
            }

            //If the global product doesn't exsist any more
            // Or if the global quantity is less than the total in the cart
            if (globalProduct == null || globalProduct.getQuantity() < quantityInCart)
                return false;

        }

        //Otherwise purchase cart
        for (int i = currentCustomer.getShoppingCart().size() - 1; i >= 0; i--) {
            Product toBuy = currentCustomer.getShoppingCart().get(i);
            Product check = null;
            for (int j = 0; j < products.size(); j++) {
                check = products.get(j);
                if (check.getName().equals(toBuy.getName()) &&
                        check.getStoreName().equals(toBuy.getStoreName())) {

                    products.get(j).recordSale(toBuy.getQuantity());

                    String storeName = check.getStoreName();
                    for (int k = 0; k < stores.size(); k++) {
                        if (stores.get(k).getStoreName().equals(storeName)) {
                            stores.get(k).addSale(check, currentCustomer.getEmail(), toBuy.getQuantity());
                        }
                    }
                    break;
                }
            }
            currentCustomer.recordPurchaseHistory(toBuy, toBuy.getQuantity(), check.getPrice());
            currentCustomer.getShoppingCart().remove(i);
        }
        return true;
    }
}