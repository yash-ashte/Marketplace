import java.io.*;
import java.util.ArrayList;
import java.net.*;
import javax.swing.*;
import java.util.*;

/**
 * Project 5 -- MarkeplaceClient
 *
 * Class to contain the client aspect of the
 * marketplace. This is what the client will
 * run in order to complete actions within the
 * marketplace.
 *
 * @author Yash Ashtekar
 *
 * @version 20/05/23
 *
 */

@SuppressWarnings("unchecked")
public class MarketplaceClient {

    //Socket- connects to server
    private static Socket socket;

    /*
     * Printstream writes to this byte array stream.
     * PS is passed into each GUI class- each gui prints paramaters and instructions
     * to this printstream, so the main client can handle each instruction.
     */
    private static ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private static PrintStream ps = new PrintStream(baos);

    //GUIs
    //Shared
    private static LoginGUI loginGUI = new LoginGUI(ps);
    private static SignUpGUI signUpGUI = new SignUpGUI(ps);
    private static ChangeProfileGUI changeProfileGUI;
    //homepages
    private static CustomerMarketplaceGUI customerHomeGUI;
    private static SellerStoreListGUI sellerHomeGUI;
    //Seller GUIs
    private static SellerStoreGUI storeGUI;
    private static AddProductGUI addProductGUI;
    private static EditProductGUI editProductGUI;
    private static SaleHistoryGUI saleHistGUI;
    //Customer GUIs
    private static ShoppingCartGUI cartGUI;
    private static PurchaseHistoryGUI purchaseHistGUI;
    //Dashboard GUIs
    private static SellerStoreStatisticsGUI storeStatsGUI;
    private static CustomerCartInfoGUI cartsInfoGUI;
    private static StoresDashGUI storesDashGUI;

    public static void main(String[] args) {

        String hostname; //get hostname
        do {
            hostname = JOptionPane.showInputDialog(null, "Welcome to the Marketplace!\nPlease enter a host name.",
                    "The Marketplace", JOptionPane.QUESTION_MESSAGE);

            if (hostname == null) { //Pressed close button
                JOptionPane.showMessageDialog(null, "Thanks for using the Marketplace!", "The Marketplace",
                        JOptionPane.PLAIN_MESSAGE);
                return;
            } else if (hostname.equals("")) {
                JOptionPane.showMessageDialog(null, "Please enter a valid host name.", "The Marketplace",
                        JOptionPane.ERROR_MESSAGE);
            }
        } while (hostname.equals(""));

        String portString; //get port 
        int port;
        do {

            portString = JOptionPane.showInputDialog(null, "Enter the server port.", "The Marketplace",
                    JOptionPane.QUESTION_MESSAGE);
            if (portString == null) { //Pressed close button
                JOptionPane.showMessageDialog(null, "Thanks for using the Marketplace!", "The Marketplace",
                        JOptionPane.PLAIN_MESSAGE);
                return;
            }

            try {
                port = Integer.parseInt(portString);
            } catch (NumberFormatException e) {
                port = -1;
            }


            if (port < 0) {
                JOptionPane.showMessageDialog(null, "Please enter a valid port.", "The Marketplace",
                        JOptionPane.ERROR_MESSAGE);
            }
        } while (port < 0);

        //Connect to server
        try {
            socket = new Socket(hostname, port);
        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(null, "Unable to resolve host!", "The Marketplace",
                    JOptionPane.ERROR_MESSAGE);
            socket = null;
            return;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "An error occured connecting to the server!",
                    "The Marketplace", JOptionPane.ERROR_MESSAGE);
            socket = null;
            return;
        }

        //Set up input and output to server
        ObjectOutputStream clientOutput;
        ObjectInputStream serverInput;
        try {
            clientOutput = new ObjectOutputStream(socket.getOutputStream());
            serverInput = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            clientOutput = null;
            serverInput = null;
            JOptionPane.showMessageDialog(null, "Error connecting to server", "The Marketplace",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        //Create the first GUI (Login or sign up page)
        SwingUtilities.invokeLater(loginGUI);

        /*
         * Main loop- until stopped, the byte array (from global print stream)
         * is converted into an arraylist of strings, containing instructions and paramaters
         * If there is a new instruction (the size is different) the client handles this instruction
         * by requesting data from server and displaying new GUI elements.
         */
        int prevInstructionListSize = 0;
        while (true) {


            ByteArrayInputStream b = new ByteArrayInputStream(baos.toByteArray());


            ArrayList<String> instructionList = new ArrayList<String>();
            Scanner scanner = new Scanner(b);
            while (scanner.hasNext()) {
                instructionList.add(scanner.nextLine());
            }
            scanner.close();

            if (instructionList.size() > 0 && instructionList.size() != prevInstructionListSize) {
                String instruction = instructionList.get(instructionList.size() - 1);

                //System.out.println(instruction);

                switch (instruction) {

                    case "Example":
                        //do stuff
                        break;

                    case "signUpSeller":
                        String nameSignUpSeller  = instructionList.get((instructionList.size() - 1) - 3);
                        String emailSignUpSeller  = instructionList.get((instructionList.size() - 1) - 2);
                        String passwordSignUpSeller = instructionList.get((instructionList.size() - 1) - 1);

                        boolean successSUS;
                        try {
                            clientOutput.writeObject("signUpSeller"); //Request
                            clientOutput.writeObject(nameSignUpSeller);
                            clientOutput.writeObject(emailSignUpSeller);
                            clientOutput.writeObject(passwordSignUpSeller);

                            successSUS = (Boolean) serverInput.readObject();
                        } catch (Exception e) {
                            connectionLost();
                            return;
                        }

                        if (successSUS) {
                            ps.println("goToSellerHome");
                        } else {
                            JOptionPane.showMessageDialog(null, "Email is already in use!", "The Marketplace",
                                    JOptionPane.ERROR_MESSAGE);
                            SwingUtilities.invokeLater(signUpGUI);
                        }

                        break;

                    case "signUpCustomer":
                        String nameSignUpCustomer  = instructionList.get((instructionList.size() - 1) - 3);
                        String emailSignUpCustomer  = instructionList.get((instructionList.size() - 1) - 2);
                        String passwordSignUpCustomer = instructionList.get((instructionList.size() - 1) - 1);

                        boolean successSUC;
                        try {
                            clientOutput.writeObject("signUpCustomer"); //Request
                            clientOutput.writeObject(nameSignUpCustomer);
                            clientOutput.writeObject(emailSignUpCustomer);
                            clientOutput.writeObject(passwordSignUpCustomer);

                            successSUC = (Boolean) serverInput.readObject();
                        } catch (Exception e) {
                            connectionLost();
                            return;
                        }

                        if (successSUC) {
                            ps.println("goToCustomerHome");
                        } else {
                            JOptionPane.showMessageDialog(null, "Email is already in use!", "The Marketplace",
                                    JOptionPane.ERROR_MESSAGE);
                            SwingUtilities.invokeLater(signUpGUI);
                        }

                        break;

                    case "logIn":
                        String emailLI = instructionList.get(instructionList.size() - 1 - 2);
                        String passwordLI = instructionList.get(instructionList.size() - 1 - 1);

                        boolean successLI;
                        boolean isCustomer;
                        try {
                            clientOutput.writeObject("logIn");
                            clientOutput.writeObject(emailLI);
                            clientOutput.writeObject(passwordLI);

                            successLI = (Boolean) serverInput.readObject();
                            isCustomer = (Boolean)  serverInput.readObject();
                        } catch (Exception e) {
                            connectionLost();
                            return;
                        }

                        if (successLI) {

                            if (isCustomer) {
                                ps.println("goToCustomerHome");
                            } else {
                                ps.println("goToSellerHome");
                            }

                            loginGUI.frame.dispose();

                        } else {
                            JOptionPane.showMessageDialog(null, "Incorrect email or password.", "The Marketplace",
                                    JOptionPane.ERROR_MESSAGE);
                        }

                        break;

                    case "goToSignUp":
                        SwingUtilities.invokeLater(signUpGUI);
                        break;

                    case "getAllProducts":

                        ArrayList<Product> globalProducts;

                        try {
                            clientOutput.writeObject("getProducts");

                            globalProducts = (ArrayList<Product>) serverInput.readObject();
                        } catch (Exception e) {
                            connectionLost();
                            return;
                        }

                        customerHomeGUI = new CustomerMarketplaceGUI(globalProducts, ps);
                        SwingUtilities.invokeLater(customerHomeGUI);

                        break;

                    case "searchProducts":

                        String keyword = instructionList.get(instructionList.size() - 1 - 1);

                        ArrayList<Product> searchedProducts;

                        try {

                            clientOutput.writeObject("searchProducts");
                            clientOutput.writeObject(keyword);

                            searchedProducts = (ArrayList<Product>) serverInput.readObject();
                        } catch (Exception e) {
                            connectionLost();
                            return;
                        }

                        customerHomeGUI = new CustomerMarketplaceGUI(searchedProducts, ps);
                        SwingUtilities.invokeLater(customerHomeGUI);

                        break;

                    case "goToProfile":

                        ArrayList<String> profileInfo;
                        //Profileinfo - name, email, password

                        try {
                            clientOutput.writeObject("getProfileInfo");

                            profileInfo = (ArrayList<String>) serverInput.readObject();

                        } catch (Exception e) {
                            connectionLost();
                            return;
                        }

                        changeProfileGUI = new ChangeProfileGUI(profileInfo, ps);
                        SwingUtilities.invokeLater(changeProfileGUI);

                        break;

                    case "sortProducts":

                        String sortTypeSP = instructionList.get(instructionList.size() - 1 - 1);
                        int sortTypeIntSP = Integer.parseInt(sortTypeSP);
                        ArrayList<Product> sortedProducts;

                        try {
                            clientOutput.writeObject("getSortedProducts");
                            clientOutput.writeObject(sortTypeIntSP);
                            sortedProducts = (ArrayList<Product>) serverInput.readObject();
                        } catch (Exception e) {
                            connectionLost();
                            return;
                        }

                        customerHomeGUI = new CustomerMarketplaceGUI(sortedProducts, ps);
                        SwingUtilities.invokeLater(customerHomeGUI);

                        break;

                    case "addToCart":

                        String productNameATC = instructionList.get(instructionList.size() - 1 - 2);
                        String storeNameATC = instructionList.get(instructionList.size() - 1 - 1);

                        boolean successATC;

                        try {

                            clientOutput.writeObject("addToCart");
                            clientOutput.writeObject(productNameATC);
                            clientOutput.writeObject(storeNameATC);

                            successATC = (boolean) serverInput.readObject();

                        } catch (Exception e) {
                            connectionLost();
                            return;
                        }

                        if (successATC) {
                            JOptionPane.showMessageDialog(null, "Successfully added.",
                                    "The Marketplace", JOptionPane.PLAIN_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null,
                                    "Unable to add to cart. Insufficient stock available in store.",
                                    "The Marketplace", JOptionPane.INFORMATION_MESSAGE);
                        }

                        break;

                    case "goToStoreDashboard":

                        ArrayList<Store> allStores;
                        ArrayList<Integer> allStoresProducts;
                        ArrayList<String> sellerList;

                        try {
                            clientOutput.writeObject("getSortedStores");
                            clientOutput.writeObject(1);

                            allStores = (ArrayList<Store>) serverInput.readObject();
                            allStoresProducts = (ArrayList<Integer>) serverInput.readObject();
                            sellerList = (ArrayList<String>) serverInput.readObject();

                        } catch (Exception e) {
                            connectionLost();
                            return;
                        }

                        storesDashGUI = new StoresDashGUI(allStores, sellerList, allStoresProducts, ps);
                        SwingUtilities.invokeLater(storesDashGUI);

                        break;

                    case "goToCart":

                        ArrayList<Product> productsInCart;

                        try {
                            clientOutput.writeObject("productsInCart");

                            productsInCart = (ArrayList<Product>) serverInput.readObject();
                        } catch (Exception e) {
                            connectionLost();
                            return;
                        }

                        cartGUI = new ShoppingCartGUI(productsInCart, ps);
                        SwingUtilities.invokeLater(cartGUI);

                        break;

                    case "goToPurchaseHistory":

                        ArrayList<Purchase> localPurchaseHistory;

                        try {
                            clientOutput.writeObject("getPurchaseHistory");

                            localPurchaseHistory = (ArrayList<Purchase>) serverInput.readObject();
                        } catch (Exception e) {
                            connectionLost();
                            return;
                        }

                        purchaseHistGUI = new PurchaseHistoryGUI(localPurchaseHistory, ps);
                        SwingUtilities.invokeLater(purchaseHistGUI);

                        break;

                    case "goToCustomerHome":

                        ArrayList<Product> allProductsCH;

                        try {
                            clientOutput.writeObject("getProducts");

                            allProductsCH = (ArrayList<Product>) serverInput.readObject();
                        } catch (Exception e) {
                            connectionLost();
                            return;
                        }

                        customerHomeGUI = new CustomerMarketplaceGUI(allProductsCH, ps);
                        SwingUtilities.invokeLater(customerHomeGUI);

                        break;

                    case "removeProductCart":

                        String productNameRPC = instructionList.get(instructionList.size() - 1 - 2);
                        String storeNameRPC = instructionList.get(instructionList.size() - 1 - 1);

                        try {

                            clientOutput.writeObject("removeProductCart");
                            clientOutput.writeObject(productNameRPC);
                            clientOutput.writeObject(storeNameRPC);

                        } catch (Exception e) {
                            connectionLost();
                            return;
                        }

                        JOptionPane.showMessageDialog(null, "Removed " + productNameRPC + " from cart.",
                                "The Marketplace", JOptionPane.PLAIN_MESSAGE);


                        break;

                    case "purchaseCart":

                        boolean successPC;

                        try {
                            clientOutput.writeObject("purchaseCart");

                            successPC = (boolean) serverInput.readObject();
                        } catch (Exception e) {
                            connectionLost();
                            return;
                        }

                        if (!successPC) {
                            JOptionPane.showMessageDialog(null,
                                    "Unable to purchase cart: Quantity of items in cart exceed quantity available in stores.",
                                    "The Marketplace", JOptionPane.ERROR_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "Successfully purchased cart.",
                                    "The Marketplace", JOptionPane.PLAIN_MESSAGE);
                            cartGUI.disposeThis();
                            ps.println("goToCustomerHome");
                        }

                        break;

                    case "exportHistory":

                        String filenameEH = instructionList.get(instructionList.size() - 1 - 1);

                        ArrayList<String> historyLines;

                        try {
                            clientOutput.writeObject("exportHistory");

                            historyLines = (ArrayList<String>) serverInput.readObject();

                        } catch (Exception e) {
                            connectionLost();
                            return;
                        }

                        try {
                            PrintWriter writer = new PrintWriter(new File(filenameEH));
                            for (String s : historyLines) {
                                writer.println(s);
                                writer.flush();
                            }
                            writer.close();
                            JOptionPane.showMessageDialog(null, "Succesfully exported history!",
                                    "The Marketplace", JOptionPane.INFORMATION_MESSAGE);
                        } catch (FileNotFoundException e) {
                            JOptionPane.showMessageDialog(null, "Unable to write to the file!",
                                    "The Marketplace", JOptionPane.ERROR_MESSAGE);
                        }

                        break;

                    case "goToStoreInfo":

                        Store storeGTSI;

                        String storeNameSI = instructionList.get(instructionList.size() - 1 - 1);

                        try {
                            clientOutput.writeObject("getStore");
                            clientOutput.writeObject(storeNameSI);

                            storeGTSI = (Store) serverInput.readObject();
                        } catch (Exception e) {
                            connectionLost();
                            return;
                        }

                        storeGUI = new SellerStoreGUI(storeGTSI, ps);
                        SwingUtilities.invokeLater(storeGUI);

                        break;

                    case "goToCartsInfo":

                        int numProductsInCart;

                        ArrayList<Product> productsInAllCarts;

                        try {
                            clientOutput.writeObject("getAllCartsInfo");

                            numProductsInCart = (int) serverInput.readObject();

                            productsInAllCarts = (ArrayList<Product>) serverInput.readObject();

                        } catch (Exception e) {
                            connectionLost();
                            return;
                        }

                        cartsInfoGUI = new CustomerCartInfoGUI(productsInAllCarts, numProductsInCart, ps);
                        SwingUtilities.invokeLater(cartsInfoGUI);

                        break;

                    case "addStore":

                        String storeNameAS = instructionList.get(instructionList.size() - 1 - 1);
                        boolean successAS;

                        try {
                            clientOutput.writeObject("addStore");
                            clientOutput.writeObject(storeNameAS);

                            successAS = (boolean) serverInput.readObject();
                        } catch (Exception e) {
                            connectionLost();
                            return;
                        }

                        if (successAS) {
                            JOptionPane.showMessageDialog(null, "Successfully created " + storeNameAS + ".",
                                    "The Marketplace", JOptionPane.INFORMATION_MESSAGE);
                            sellerHomeGUI.disposeThis();
                            ps.println("goToSellerHome");
                        } else {
                            JOptionPane.showMessageDialog(null, "This store already exists!",
                                    "The Marketplace", JOptionPane.ERROR_MESSAGE);
                        }

                        break;

                    case "updateProfile":

                        String nameUProf = instructionList.get(instructionList.size() - 1 - 3);
                        String emailUProf = instructionList.get(instructionList.size() - 1 - 2);
                        String passwordUProf = instructionList.get(instructionList.size() - 1 - 1);

                        boolean successUpPr;

                        try {
                            clientOutput.writeObject("updateProfile");
                            clientOutput.writeObject(nameUProf);
                            clientOutput.writeObject(emailUProf);
                            clientOutput.writeObject(passwordUProf);

                            successUpPr = (boolean) serverInput.readObject();

                        } catch (Exception e) {
                            connectionLost();
                            return;
                        }

                        if (successUpPr) {
                            JOptionPane.showMessageDialog(null, "Successfully changed profile details!",
                                    "The Marketplace", JOptionPane.PLAIN_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "Unable to update profile: email is in use already.",
                                    "The Marketplace", JOptionPane.ERROR_MESSAGE);
                        }

                        break;

                    case "deleteAccount":

                        try {
                            clientOutput.writeObject("deleteAccount");
                        } catch (Exception e) {
                            connectionLost();
                            return;
                        }

                        break;

                    case "goToManageProduct":

                        String productNameGTMP = instructionList.get(instructionList.size() - 1 - 2);
                        String storeNameGTMP = instructionList.get(instructionList.size() - 1 - 1);

                        ArrayList<String> productDetails;
                        //Name, Storename, Price, Description, Quantity

                        try {
                            clientOutput.writeObject("getProductDetails");
                            clientOutput.writeObject(productNameGTMP);
                            clientOutput.writeObject(storeNameGTMP);

                            productDetails = (ArrayList<String>) serverInput.readObject();

                        } catch (Exception e) {
                            connectionLost();
                            return;
                        }

                        editProductGUI = new EditProductGUI(productDetails, ps);
                        SwingUtilities.invokeLater(editProductGUI);

                        break;

                    case "goToCreateProduct":

                        String storeName = instructionList.get(instructionList.size() - 1 - 1);

                        addProductGUI = new AddProductGUI(storeName, ps);
                        SwingUtilities.invokeLater(addProductGUI);

                        break;

                    case "importProducts":

                        String filenameIP = instructionList.get(instructionList.size() - 1 - 2);
                        String storeNameIP = instructionList.get(instructionList.size() - 1 - 1);

                        ArrayList<String> csvLines = new ArrayList<String>();

                        boolean successIP;

                        try{
                            Scanner readerIP = new Scanner(new File(filenameIP));

                            while (readerIP.hasNextLine()) {
                                csvLines.add(readerIP.nextLine());
                            }
                        } catch (FileNotFoundException e) {
                            JOptionPane.showMessageDialog(null, "Unable to locate file!",
                                    "The Marketplace", JOptionPane.ERROR_MESSAGE);
                        }

                        if (csvLines.size() > 0) {

                            try {
                                clientOutput.writeObject("importProducts");
                                clientOutput.writeObject(storeNameIP);
                                clientOutput.writeObject(csvLines);

                                successIP = (boolean) serverInput.readObject();
                            } catch (Exception e) {
                                connectionLost();
                                return;
                            }

                            if (successIP) {
                                JOptionPane.showMessageDialog(null, "Successfully imported products!",
                                        "The Marketplace", JOptionPane.PLAIN_MESSAGE);
                                storeGUI.disposeThis();
                                ps.println(storeNameIP + "\ngoToStoreInfo");

                            } else {
                                JOptionPane.showMessageDialog(null, "Unable to import products- check CSV format!",
                                        "The Marketplace", JOptionPane.ERROR_MESSAGE);
                            }

                        } else {
                            JOptionPane.showMessageDialog(null, "Unable to import products- check CSV format!",
                                    "The Marketplace", JOptionPane.ERROR_MESSAGE);
                        }

                        break;

                    case "exportProducts":

                        String fileNameEP = instructionList.get(instructionList.size() - 1 - 2);
                        String storeNameEP = instructionList.get(instructionList.size() - 1 - 1);

                        ArrayList<String> csvLinesEP;

                        try {
                            clientOutput.writeObject("exportProducts");
                            clientOutput.writeObject(storeNameEP);

                            csvLinesEP = (ArrayList<String>) serverInput.readObject();

                        } catch (Exception e) {
                            connectionLost();
                            return;
                        }

                        try {
                            PrintWriter writerEP = new PrintWriter(new File(fileNameEP));

                            for (String s : csvLinesEP) {
                                writerEP.println(s);
                                writerEP.flush();
                            }
                            writerEP.close();
                            JOptionPane.showMessageDialog(null, "Successfully exported to " + fileNameEP + ".",
                                    "The Marketplace", JOptionPane.PLAIN_MESSAGE);
                        } catch (FileNotFoundException e) {
                            JOptionPane.showMessageDialog(null, "Unable to write to file.",
                                    "The Marketplace", JOptionPane.ERROR_MESSAGE);
                        }

                        break;

                    case "goToSaleHistory":

                        String storeNameGTSH = instructionList.get(instructionList.size() - 1 - 1);
                        ArrayList<Sale> saleHistoryGTSH;

                        try {

                            clientOutput.writeObject("saleHistory");
                            clientOutput.writeObject(storeNameGTSH);

                            saleHistoryGTSH = (ArrayList<Sale>) serverInput.readObject();

                        } catch (Exception e) {
                            connectionLost();
                            return;
                        }

                        saleHistGUI = new SaleHistoryGUI(saleHistoryGTSH, ps);
                        SwingUtilities.invokeLater(saleHistGUI);

                        break;

                    case "deleteStore":

                        String storeNameDS = instructionList.get(instructionList.size() - 1 - 1);

                        try {

                            clientOutput.writeObject("deleteStore");
                            clientOutput.writeObject(storeNameDS);

                        } catch (Exception e) {
                            connectionLost();
                            return;
                        }

                        JOptionPane.showMessageDialog(null, "Successfully deleted store!", "The Marketplace",
                                JOptionPane.PLAIN_MESSAGE);

                        ps.println("goToSellerHome");

                        break;

                    case "goToCustomerPuchaseDashboard": //15

                        String storeNameGTCPD = instructionList.get(instructionList.size() - 1 - 1);
                        ArrayList<String> sortedCustomerNamesGTCPD;
                        ArrayList<String> sortedProductsBoughtNumGTCPD;
                        ArrayList<String> sortedProductsBoughtGTCPD;
                        ArrayList<String> sortedSalesGTCPD;

                        try {

                            clientOutput.writeObject("customerPurchaseDashboard");
                            clientOutput.writeObject(storeNameGTCPD);

                            sortedCustomerNamesGTCPD = (ArrayList<String>) serverInput.readObject();
                            sortedProductsBoughtNumGTCPD = (ArrayList<String>) serverInput.readObject();
                            sortedProductsBoughtGTCPD = (ArrayList<String>) serverInput.readObject();
                            sortedSalesGTCPD = (ArrayList<String>) serverInput.readObject();

                        } catch (Exception e) {
                            connectionLost();
                            return;
                        }

                        storeStatsGUI = new SellerStoreStatisticsGUI(storeNameGTCPD, sortedCustomerNamesGTCPD,
                                sortedProductsBoughtNumGTCPD,
                                sortedProductsBoughtGTCPD, sortedSalesGTCPD, ps);
                        SwingUtilities.invokeLater(storeStatsGUI);

                        break;

                    case "updateProduct":

                        String productNameUP = instructionList.get(instructionList.size() - 5 - 1);
                        String storeNameUP = instructionList.get(instructionList.size() - 4 - 1);
                        String priceUP = instructionList.get(instructionList.size() - 3 - 1);
                        double priceUPInt = Double.parseDouble(priceUP);
                        String quantityUP = instructionList.get(instructionList.size() - 2 - 1);
                        int quantityUPInt = Integer.parseInt(quantityUP);
                        String descriptionUP = instructionList.get(instructionList.size() - 1 - 1);

                        try {

                            clientOutput.writeObject("updateProduct");
                            clientOutput.writeObject(productNameUP);
                            clientOutput.writeObject(storeNameUP);
                            clientOutput.writeObject(priceUPInt);
                            clientOutput.writeObject(quantityUPInt);
                            clientOutput.writeObject(descriptionUP);

                        } catch (Exception e) {
                            connectionLost();
                            return;
                        }


                        JOptionPane.showMessageDialog(null, "Successfully edited product!", "The Marketplace",
                                JOptionPane.DEFAULT_OPTION);

                        ps.println(storeNameUP + "\ngoToStoreInfo");

                        break;

                    case "deleteProduct":

                        String productNameDP = instructionList.get(instructionList.size() - 2 - 1);
                        String storeNameDP = instructionList.get(instructionList.size() - 1 - 1);

                        try {

                            clientOutput.writeObject("deleteProduct");
                            clientOutput.writeObject(productNameDP);
                            clientOutput.writeObject(storeNameDP);

                        } catch (Exception e) {
                            connectionLost();
                            return;
                        }

                        JOptionPane.showMessageDialog(null, "Successfully deleted product!", "The Marketplace",
                                JOptionPane.DEFAULT_OPTION);

                        break;

                    case "goToSellerHome":

                        ArrayList<Store> sellersStoresGTSH;

                        try {

                            clientOutput.writeObject("sellerHome");

                            sellersStoresGTSH = (ArrayList<Store>) serverInput.readObject();

                        } catch (Exception e) {
                            connectionLost();
                            return;
                        }

                        sellerHomeGUI = new SellerStoreListGUI(sellersStoresGTSH, ps);
                        SwingUtilities.invokeLater(sellerHomeGUI);

                        break;

                    case "addProductToStore":

                        String productNameAPTS = instructionList.get(instructionList.size() - 5 - 1);
                        String storeNameAPTS = instructionList.get(instructionList.size() - 4 - 1);
                        String priceAPTS = instructionList.get(instructionList.size() - 3 - 1);
                        double priceAPTSdouble = Double.parseDouble(priceAPTS);
                        String quantityAPTS = instructionList.get(instructionList.size() - 2 - 1);
                        int quantityAPTSint = Integer.parseInt(quantityAPTS);
                        String descriptionAPTS = instructionList.get(instructionList.size() - 1 - 1);
                        boolean successAPTS;

                        try {

                            clientOutput.writeObject("addProductToStore");
                            clientOutput.writeObject(productNameAPTS);
                            clientOutput.writeObject(storeNameAPTS);
                            clientOutput.writeObject(priceAPTSdouble);
                            clientOutput.writeObject(quantityAPTSint);
                            clientOutput.writeObject(descriptionAPTS);
                            successAPTS = (boolean) serverInput.readObject();

                        } catch (Exception e) {
                            connectionLost();
                            return;
                        }


                        if (successAPTS) {
                            JOptionPane.showMessageDialog(null, "Successfully added product!", "The Marketplace",
                                    JOptionPane.DEFAULT_OPTION);
                            ps.println(storeNameAPTS + "\ngoToStoreInfo");
                        } else {
                            JOptionPane.showMessageDialog(null, "This product already exists.", "The Marketplace",
                                    JOptionPane.ERROR_MESSAGE);
                            ps.println(storeNameAPTS + "\ngoToStoreInfo");
                        }

                        break;

                    case "sortSellerCD":    // 15

                        String storeNameSC = instructionList.get(instructionList.size() - 2 - 1);
                        String sortLowHighSCS = instructionList.get(instructionList.size() - 1 - 1);
                        int sortLowHighSC = Integer.parseInt(sortLowHighSCS);
                        boolean sortLowHighSCBool;
                        if (sortLowHighSC == 0) {
                            sortLowHighSCBool = false;
                        } else {
                            sortLowHighSCBool = true;
                        }
                        ArrayList<String> sortedCustomerNamesSC;
                        ArrayList<String> sortedProductsBoughtSC;

                        ArrayList<String> sortedProductsCD;
                        ArrayList<String> sortedSalesCD;

                        try {

                            clientOutput.writeObject("getSortedCustomers");
                            clientOutput.writeObject(storeNameSC);
                            clientOutput.writeObject(sortLowHighSCBool);

                            sortedCustomerNamesSC = (ArrayList<String>) serverInput.readObject();
                            sortedProductsBoughtSC = (ArrayList<String>) serverInput.readObject();

                            //Requests default sorted in order to display
                            clientOutput.writeObject("getSortedProductsDashboard");
                            clientOutput.writeObject(storeNameSC);
                            clientOutput.writeObject(true);

                            sortedProductsCD = (ArrayList<String>) serverInput.readObject();
                            sortedSalesCD = (ArrayList<String>) serverInput.readObject();

                        } catch (Exception e) {
                            connectionLost();
                            return;
                        }

                        storeStatsGUI = new SellerStoreStatisticsGUI(storeNameSC, sortedCustomerNamesSC,
                                sortedProductsBoughtSC, sortedProductsCD,
                                sortedSalesCD, ps);
                        SwingUtilities.invokeLater(storeStatsGUI);

                        break;

                    case "sortSellerPD":    // 15

                        String storeNameSPD = instructionList.get(instructionList.size() - 2 - 1);
                        String sortLowHighSPDS = instructionList.get(instructionList.size() - 1 - 1);
                        int sortLowHighSPD = Integer.parseInt(sortLowHighSPDS);
                        boolean sortLowHighSPDSBool;
                        if (sortLowHighSPD == 0) {
                            sortLowHighSPDSBool = false;
                        } else {
                            sortLowHighSPDSBool = true;
                        }
                        ArrayList<String> sortedProductsBoughtSPD;
                        ArrayList<String> sortedSalesSPD;

                        ArrayList<String> sortedCustomerNamesPD;
                        ArrayList<String> sortedProductsPD;

                        try {

                            clientOutput.writeObject("getSortedProductsDashboard");
                            clientOutput.writeObject(storeNameSPD);
                            clientOutput.writeObject(sortLowHighSPDSBool);

                            sortedProductsBoughtSPD = (ArrayList<String>) serverInput.readObject();
                            sortedSalesSPD = (ArrayList<String>) serverInput.readObject();

                            clientOutput.writeObject("getSortedCustomers");
                            clientOutput.writeObject(storeNameSPD);
                            clientOutput.writeObject(true);

                            sortedCustomerNamesPD = (ArrayList<String>) serverInput.readObject();
                            sortedProductsPD = (ArrayList<String>) serverInput.readObject();

                        } catch (Exception e) {
                            connectionLost();
                            return;
                        }

                        storeStatsGUI = new SellerStoreStatisticsGUI(storeNameSPD, sortedCustomerNamesPD,
                                sortedProductsPD, sortedProductsBoughtSPD,
                                sortedSalesSPD, ps);
                        SwingUtilities.invokeLater(storeStatsGUI);

                        break;

                    case "sortStores": //16

                        String sortTypeSS = instructionList.get(instructionList.size() - 1 - 1);
                        int sortTypeIntSS = Integer.parseInt(sortTypeSS);
                        ArrayList<Store> sortedStores;
                        ArrayList<Integer> sortedProductsSold;
                        ArrayList<String> sellerNamesSS;

                        try {

                            clientOutput.writeObject("getSortedStores");
                            clientOutput.writeObject(sortTypeIntSS);

                            sortedStores = (ArrayList<Store>) serverInput.readObject();
                            sortedProductsSold = (ArrayList<Integer>) serverInput.readObject();
                            sellerNamesSS = (ArrayList<String>) serverInput.readObject();

                        } catch (Exception e) {
                            connectionLost();
                            return;
                        }

                        storesDashGUI = new StoresDashGUI(sortedStores, sellerNamesSS, sortedProductsSold, ps);
                        SwingUtilities.invokeLater(storesDashGUI);

                        break;

                    case "close":
                        JOptionPane.showMessageDialog(null, "Thanks for using the Marketplace!",
                                "The Marketplace", JOptionPane.PLAIN_MESSAGE);
                        return;

                    default:
                        //This probably shouldn't happen

                }
            }

            prevInstructionListSize = instructionList.size();

        } //End of while loop

    } //end of main

    public static void connectionLost() {
        // Reusable error message

        JOptionPane.showMessageDialog(null, "Connection with the server was lost!",
                "The Marketplace", JOptionPane.ERROR_MESSAGE);

        ps.println("close");
        try {
            socket.close();
        } catch (IOException e) {
            return;
        }
    }

    //Reuseable check for special characters that mess up file reading
    public static boolean invalidString(String s) {
        return (s.indexOf(",") != -1 || s.indexOf(";") != -1 || s.indexOf("<") != -1 || s.indexOf(">") != -1 ||
                s.indexOf("-") != -1 || s.indexOf(":") != -1 || s.indexOf("\\") != -1);
    }



}