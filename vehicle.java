// Parent class for all vehicles in the rental system.
public class Vehicle {

    // Keep the vehicle information private and use methods to access it.
    private int id;
    private String make;
    private String model;
    private String type;
    private double dailyRate;
    private boolean available;
    private String imagePath;

    // Set the starting information when a vehicle is created.
    public Vehicle(int id, String make, String model, String type,
                   double dailyRate, String imagePath) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.type = type;
        this.dailyRate = dailyRate;
        this.imagePath = imagePath;
        this.available = true;
    }

    // Getters allow other classes to read the private fields.
    public int getId() {
        return id;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getType() {
        return type;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public boolean isAvailable() {
        return available;
    }

    public String getImagePath() {
        return imagePath;
    }

    // Setters change values that may need updating.
    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setDailyRate(double dailyRate) {
        if (dailyRate > 0) {
            this.dailyRate = dailyRate;
        }
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    // Child classes can change this method for their own pricing.
    public double calculateCost(int days) {
        return dailyRate * days;
    }

    // Describe the vehicle for the fleet summary.
    public String getDetails() {
        String status = available ? "AVAILABLE" : "RENTED";
        return "#" + id + "  " + make + " " + model + " (" + type + ")  UGX "
                + String.format("%,.0f", dailyRate) + "/day  [" + status + "]";
    }

    // Show useful vehicle information in a dropdown list.
    @Override
    public String toString() {
        return make + " " + model + " (" + type + ") - UGX "
                + String.format("%,.0f", dailyRate) + "/day";
    }
}

// A luxury car is a vehicle with an extra service fee.
class LuxuryCar extends Vehicle {

    private double luxuryServiceFee;

    // Reuse the Vehicle constructor and store the extra fee.
    public LuxuryCar(int id, String make, String model, double dailyRate,
                     double luxuryServiceFee, String imagePath) {
        super(id, make, model, "Luxury Car", dailyRate, imagePath);
        this.luxuryServiceFee = luxuryServiceFee;
    }

    public double getLuxuryServiceFee() {
        return luxuryServiceFee;
    }

    // Override the normal cost calculation to include the service fee.
    @Override
    public double calculateCost(int days) {
        return super.calculateCost(days) + luxuryServiceFee;
    }

    // Add the fee to the vehicle's normal fleet description.
    @Override
    public String getDetails() {
        return super.getDetails() + "  Premium service fee: UGX "
                + String.format("%,.0f", luxuryServiceFee);
    }
}
