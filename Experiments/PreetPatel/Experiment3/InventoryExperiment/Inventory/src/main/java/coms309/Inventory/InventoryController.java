package coms309.people;

import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Controller used to showcase Create and Read from a LIST
 *
 * @author Vivek Bengre
 */

@RestController
public class InventoryController {

    //Creates the hasmap of the inventory
    HashMap<String, Product> inventory = new  HashMap<>();

    //Returns all products in the inventory.
    @GetMapping("/inventory")
    public  HashMap<String, Product> getInventory() {
        return inventory;
    }

    //Creates a product and inputs it into the hashmap.
    @PostMapping("/inventory/create")
    public  String createProduct(@RequestBody Product product) {
        System.out.println(product);
        inventory.put(product.getid(), product);
        String s = "New product "+ product.getName() + " Saved. ID number: " + product.getid() + ".";
        return s;
    }

    //Returns the product that matches with the id.
    @GetMapping("/inventory/{id}")
    public Product getProduct(@PathVariable String id) {
        Product p = inventory.get(id);
        return p;
    }

    //Returns a list of all products containing the matching id or name sent through the as a path variable.
    @GetMapping("/inventory/contains/{param}")
    public List<Product> getProductbyParam(@PathVariable String param) {
        List<Product> list = new ArrayList<>();
        for (Product p : inventory.values()) {
            if (p.getid().contains(param) || p.getName().contains(param))
                list.add(p);
        }
        return list;
    }

    //Returns a list of all products costing the same as the price given as the path variable.
    @GetMapping("/inventory/contains/price/{param}")
    public List<Product> getProductbyParam(@PathVariable double param) {
        List<Product> list = new ArrayList<>();
        for (Product p : inventory.values()) {
            if (p.getPrice() == param)
                list.add(p);
        }
        return list;
    }

    //Returns a list of all products with the same quantity as given by the path variable.
    @GetMapping("/inventory/contains/quantity/{param}")
    public List<Product> getProductbyParam(@PathVariable int param) {
        List<Product> list = new ArrayList<>();
        for (Product p : inventory.values()) {
            if (p.getQuantity() == param)
                list.add(p);
            }
        return list;
    }

    @GetMapping("/inventory/low")
    public HashMap<String, Product> getLowProducts(){
        HashMap<String, Product> low = new HashMap<>();
        for (Product p: inventory.values()){
            if (p.getQuantity() <= 3){
                low.put(p.getid(),p);
            }
        }
        return low;
    }

    //Returns the new updated Product. Note: all parameters are updated, even if they are not updated in the JSON. Leads to Null paramters.
    @PutMapping("/inventory/{id}")
    public Product updateProduct(@PathVariable String id, @RequestBody Product p) {
        inventory.replace(id, p);
        return inventory.get(id);
    }


    //Deletes the product given by the id as a parameter in the url if it exists.
    @DeleteMapping("/inventory/delete")
    public HashMap<String, Product> deleteProduct(@RequestParam String id) {
        if (inventory.get(id) != null) {
            inventory.remove(id);
        }
        return inventory;
    }

    //Updates only the name of a product, determined by the given id parameter.
    @PatchMapping("/inventory/name")
    public Product updateProductName(@RequestParam("id") String id, @RequestBody Product p) {
        Product updatedProduct = inventory.get(id);
        updatedProduct.setname(p.getName());
        inventory.put(id, updatedProduct);
        return updatedProduct;
    }

    //Updates only the quantity of a product, determinded by the given id parameter.
    @PatchMapping("/inventory/quantity")
    public Product updateProductQuantity(@RequestParam("id") String id, @RequestBody Product p){
        Product update = inventory.get(id);
        update.setquantity(p.getQuantity());
        inventory.put(id, update);
        return update;
    }

    //Returns the total value of all the products
    @GetMapping("/inventory/net-worth")
    public double totalNetWorth(){
        double totalNetWorth = 0;
        for (Product p : inventory.values()){
            totalNetWorth += p.getQuantity() * p.getPrice();
        }
        return totalNetWorth;
    }

    //Returns the updatd Product with the new quantity given as a parameter in the URL, using the id given as a path varriable.
    @PatchMapping("/inventory/increment/{id}")
    public Product updatedQuantitybyParam(@RequestParam("quantity") int quantity, @PathVariable String id){
        Product update = inventory.get(id);
        update.setquantity(quantity);
        inventory.put(id, update);
        return update;
    }


} // end of people controller

