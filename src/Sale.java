import java.io.Serializable;

/**
 * Sale
 * <p>
 * Helper Class for storing sale info for stores
 *
 * @author Yash Ashtekar
 * @version 15/05/23
 */
public class Sale implements Serializable {
    private String customerEmail;
    private String productName;
    private int quantity;
    private double revenue;
    public Sale(String customerEmail, String productName, double revenue, int quantity) {
        this.customerEmail = customerEmail;
        this.productName = productName;
        this.revenue = revenue;
        this.quantity = quantity;
    }

    @Override
    public String toString()  {
        return customerEmail + "," + productName + "," + revenue;
    }

    public String getCustomerName() {
        return this.customerEmail;
    }

    public String getCustomerEmail() {
        return this.customerEmail;
    }

    public void setCustomerName(String customerName) {
        this.customerEmail = customerName;
    }

    public String getProductName() {
        return this.productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getRevenue() {
        return this.revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int q) {
        quantity = q;
    }


}