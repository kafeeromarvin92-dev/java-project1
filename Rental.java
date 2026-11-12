// Stores one booking between a customer and a vehicle.
public class Rental {

    private int id;
    private Vehicle vehicle;
    private Customer customer;
    private int days;
    private double totalCost;
    private boolean active;

    // Create a rental, mark its vehicle as taken, and calculate its cost.
    public Rental(int id, Vehicle vehicle, Customer customer, int days) {
        this.id = id;
        this.vehicle = vehicle;
        this.customer = customer;
        this.days = days;
        this.active = true;
        vehicle.setAvailable(false);
        this.totalCost = computeCost(vehicle, days);
    }

    // Calculate the price and apply a discount for longer rentals.
    public static double computeCost(Vehicle vehicle, int days) {
        double cost = vehicle.calculateCost(days);
        int weeks = days / 7;

        switch (weeks) {
            case 0:
                break;
            case 1:
                cost = cost * 0.95;
                break;
            default:
                cost = cost * 0.90;
                break;
        }
        return cost;
    }

    // Finish the rental and make the vehicle available again.
    public void returnVehicle() {
        active = false;
        vehicle.setAvailable(true);
    }

    // Getters provide access to rental information.
    public int getId() {
        return id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Customer getCustomer() {
        return customer;
    }

    public int getDays() {
        return days;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public boolean isActive() {
        return active;
    }
}
