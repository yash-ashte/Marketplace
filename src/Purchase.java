import java.io.Serializable;
import java.time.*;

/**
 *
 *    Purchase
 *
 *    A class that represents a purchase to be stored in history
 *
 *    @author Yash Ashtekar
 *
 *    @version 14/05/23
 *
 */
public class Purchase implements Serializable {

    private Product productBought;

    private int quantity;

    private double price;

    private LocalDate datePurchased;

    private LocalTime timePurchased;

    public Purchase(Product product, int q, double p, LocalDate date, LocalTime time) {
        productBought = product;
        datePurchased = date;
        timePurchased = time;
        quantity = q;
        price = p;
    }

    public Purchase(Product product, int q, double p) {
        productBought = product;
        quantity = q;
        price = p;
        datePurchased = LocalDate.now();
        timePurchased = LocalTime.now();
    }

    public String purchaseHistoryToExport() { //Intended for export by the customer, more 'proper' formatting
        if (quantity == 1) {
            String formatter = "%s: 1 was purchased from %s for $%.2f on %s at %s.";
            String export = String.format(formatter, productBought.getName(),
                    productBought.getStoreName(), price,
                    datePurchased.toString(), timePurchased.toString());
            return export;
        } else {
            String formatter = "%s: %d were purchased from %s for $%.2f on %s at %s.";
            String export = String.format(formatter, productBought.getName(), quantity,
                    productBought.getStoreName(), (price * quantity),
                    datePurchased.toString(), timePurchased.toString());
            return export;
        }

    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof Purchase) {
            Purchase p = (Purchase) o;

            if (productBought.getStoreName().equals(p.getProduct().getStoreName())
                    && productBought.getName().equals(p.getProduct().getName())) {
                return true;
            }

        }

        return false;

    }

    public String purchaseToCSV() {
        return productBought.getStoreName() + "," + productBought.getName() + "," +
                quantity + "," + price + "," + datePurchased + "," + timePurchased + "\n";

    }

    public LocalDate getDate() {
        return datePurchased;
    }

    public LocalTime getTime() {
        return timePurchased;
    }

    public Product getProduct() {
        return productBought;
    }


    public Product getProductBought() {
        return this.productBought;
    }

    public void setProductBought(Product productBought) {
        this.productBought = productBought;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDate getDatePurchased() {
        return this.datePurchased;
    }

    public void setDatePurchased(LocalDate datePurchased) {
        this.datePurchased = datePurchased;
    }

    public LocalTime getTimePurchased() {
        return this.timePurchased;
    }

    public void setTimePurchased(LocalTime timePurchased) {
        this.timePurchased = timePurchased;
    }


}