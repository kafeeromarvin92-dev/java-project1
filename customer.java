// Stores the details of one customer.
public class Customer {

    private int id;
    private String name;
    private String phone;
    private String licenseNumber;

    // Create a customer with the supplied details.
    public Customer(int id, String name, String phone, String licenseNumber) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.licenseNumber = licenseNumber;
    }

    // Getters let the rest of the program read customer details.
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    // Setters allow the customer's name or phone number to be changed.
    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // Show the customer clearly in a dropdown list.
    @Override
    public String toString() {
        return name + " (" + phone + ")";
    }
}
