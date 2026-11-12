// Stores and manages the vehicles, customers, and rentals.
public class RentalManager {

    // Fixed-size arrays hold all project data while the program is running.
    private Vehicle[] vehicles = new Vehicle[50];
    private Customer[] customers = new Customer[50];
    private Rental[] rentals = new Rental[100];
    private int vehicleCount;
    private int customerCount;
    private int rentalCount;

    // Add a regular vehicle or a LuxuryCar to the vehicle array.
    public boolean addVehicle(String make, String model, String type,
                              double dailyRate, String imagePath, double luxuryFee) {
        if (vehicleCount >= vehicles.length) {
            return false;
        }

        int id = nextVehicleId();
        if (type.equals("Luxury Car")) {
            vehicles[vehicleCount] =
                    new LuxuryCar(id, make, model, dailyRate, luxuryFee, imagePath);
        } else {
            vehicles[vehicleCount] =
                    new Vehicle(id, make, model, type, dailyRate, imagePath);
        }
        vehicleCount++;
        return true;
    }

    // Use a do-while loop and a for loop to find an unused vehicle number.
    private int nextVehicleId() {
        int id = vehicleCount;
        boolean alreadyUsed;
        do {
            id++;
            alreadyUsed = false;
            for (int i = 0; i < vehicleCount; i++) {
                if (vehicles[i].getId() == id) {
                    alreadyUsed = true;
                }
            }
        } while (alreadyUsed);
        return id;
    }

    public Vehicle getVehicle(int index) {
        return vehicles[index];
    }

    public int getVehicleCount() {
        return vehicleCount;
    }

    // Return a new array containing only vehicles that can be rented.
    public Vehicle[] getAvailableVehicles() {
        int count = 0;
        for (int i = 0; i < vehicleCount; i++) {
            if (vehicles[i].isAvailable()) {
                count++;
            }
        }

        Vehicle[] available = new Vehicle[count];
        int position = 0;
        for (int i = 0; i < vehicleCount; i++) {
            if (vehicles[i].isAvailable()) {
                available[position] = vehicles[i];
                position++;
            }
        }
        return available;
    }

    // Add a customer unless the same licence number is already registered.
    public String addCustomer(String name, String phone, String licenseNumber) {
        if (customerCount >= customers.length) {
            return "Customer list is full.";
        }
        for (int i = 0; i < customerCount; i++) {
            if (customers[i].getLicenseNumber().equalsIgnoreCase(licenseNumber)) {
                return "A customer with this licence number already exists.";
            }
        }

        customers[customerCount] =
                new Customer(customerCount + 1, name, phone, licenseNumber);
        customerCount++;
        return "OK";
    }

    public Customer getCustomer(int index) {
        return customers[index];
    }

    public int getCustomerCount() {
        return customerCount;
    }

    // Create a rental if the selected customer and vehicle are valid.
    public String rentVehicle(Vehicle vehicle, Customer customer, int days)
            throws VehicleAlreadyRentedException {
        if (vehicle == null || customer == null) {
            return "Choose a customer and a vehicle.";
        }
        if (days < 1) {
            return "The rental must be at least one day.";
        }
        if (!vehicle.isAvailable()) {
            throw new VehicleAlreadyRentedException(
                    "The " + vehicle.getMake() + " " + vehicle.getModel()
                            + " is already rented.");
        }
        if (rentalCount >= rentals.length) {
            return "Rental list is full.";
        }

        rentals[rentalCount] =
                new Rental(rentalCount + 1, vehicle, customer, days);
        rentalCount++;
        return "OK";
    }

    // Return an active rental using its position in the rental array.
    public String returnRental(int index) {
        if (index < 0 || index >= rentalCount) {
            return "Select a rental first.";
        }

        Rental rental = rentals[index];
        if (!rental.isActive()) {
            return "This vehicle has already been returned.";
        }
        rental.returnVehicle();
        return "OK";
    }

    public Rental getRental(int index) {
        return rentals[index];
    }

    public int getRentalCount() {
        return rentalCount;
    }

    // Count active rentals using a while loop.
    public int getActiveRentalCount() {
        int active = 0;
        int i = 0;
        while (i < rentalCount) {
            if (rentals[i].isActive()) {
                active++;
            }
            i++;
        }
        return active;
    }

    // Add a few example records so the application is ready to demonstrate.
    public void addSampleData() {
        addVehicle("Toyota", "Corolla", "Car", 120000, "images/car.png", 0);
        addVehicle("Toyota", "Hiace", "Van", 250000, "images/van.png", 0);
        addVehicle("Bajaj", "Boxer", "Bike", 40000, "images/bike.png", 0);
        addVehicle("Mercedes-Benz", "C-Class", "Luxury Car", 300000,
                "images/car.png", 80000);
        addCustomer("Amina Nakato", "0772000111", "DL-1001");
        addCustomer("Brian Okello", "0701222333", "DL-1002");
    }
}

// Custom error used when an already rented vehicle is requested.
class VehicleAlreadyRentedException extends Exception {

    public VehicleAlreadyRentedException(String message) {
        super(message);
    }
}
