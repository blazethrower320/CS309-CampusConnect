package coms309.people;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Provides the Definition/Structure for the people row
 *
 * @author Vivek Bengre
 */
@Getter // Lombok Shortcut for generating getter methods (Matches variable names set ie firstName -> getFirstName)
@Setter // Similarly for setters as well
@NoArgsConstructor // Default constructor
public class Product {

    private String id;

    private String name;

    private int quantity;

    private double price;

//    public Person(){
//
//    }

    public Product(String id, String name, int quantity, Double price){
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }


    /**
     * Getter and Setters below are technically redundant and can be removed.
     * They will be generated from the @Getter and @Setter tags above class
     */

    public String getid() {
        return this.id;
    }

    public void setid(String id) {
        this.id = id;
    }

    public String name() {
        return this.name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public int quantity() {
        return this.quantity;
    }

    public void setquantity(int quantity) {
        this.quantity = quantity;
    }

    public double price() {
        return this.price;
    }

    public void setprice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return id + " "
               + name + " "
               + quantity + " "
               + price;
    }
}
