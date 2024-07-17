import java.sql.*;
import java.util.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception {
        RailwayBookingSystem railwayBookingSystem = new RailwayBookingSystem();

        Scanner sc = new Scanner(System.in);

        UserDAO userDAO = new UserDAO();
        List<User> users = userDAO.getAllUsers();
        for (User user : users) {
            railwayBookingSystem.createUser(user.getUsername(), user.getPassword(), user.getName(), user.getEmail(),
                    user.getPhoneNumber());
        }

        System.out.println("=============================================");
        System.out.println("=========== Railway Booking System ==========");
        System.out.println("=============================================");
        int choice;
        do {
            System.out.println("1. Admin Portal");
            System.out.println("2. User Portal");
            System.out.println("3. Exit The Program");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            if (choice == 1) {
                adminLogin(railwayBookingSystem, sc);
            } else if (choice == 2) {
                userPortal(railwayBookingSystem, sc);
            } else if (choice == 3) {
                System.out.println("Thank You For Using Railway Booking System.");
            } else {
                System.out.println("Invalid choice");
            }
        } while (choice != 3);
    }

    private static boolean adminLogin(RailwayBookingSystem railwayBookingSystem, Scanner sc) {
        System.out.print("Enter admin username: ");
        String username = sc.next();
        System.out.print("Enter admin password: ");
        String password = sc.next();

        if (username.equals("User12") && password.equals("User@123")) {
            System.out.println("Admin logged in successfully.");
            adminPortal(railwayBookingSystem, sc);
            return true;
        } else {
            System.out.println("Invalid admin credentials. Please try again.");
            return false;
        }
    }

    private static void adminPortal(RailwayBookingSystem railwayBookingSystem, Scanner sc) {
        int choice;
        do {
            System.out.println("--------------------");
            System.out.println("Admin Portal");
            System.out.println("1. Create Train");
            System.out.println("2. Update Train");
            System.out.println("3. Delete Train");
            System.out.println("4. View All Trains");
            System.out.println("5. View All Bookings");
            System.out.println("6. Exit");
            System.out.println("--------------------");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    createTrain(railwayBookingSystem, sc);
                    break;
                case 2:
                    updateTrain(railwayBookingSystem, sc);
                    break;
                case 3:
                    deleteTrain(railwayBookingSystem, sc);
                    break;
                case 4:
                    viewAllTrains(railwayBookingSystem);
                    break;
                case 5:
                    viewAllBookings(railwayBookingSystem);
                    break;
                case 6:
                    System.out.println("Exiting admin portal");
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        } while (choice != 6);
    }

    private static void userPortal(RailwayBookingSystem railwayBookingSystem, Scanner sc) {
        while (true) {
            System.out.println("User Portal");
            System.out.println("1. Existing Customer Login");
            System.out.println("2. Signup");
            System.out.println("3. Logout");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    login(railwayBookingSystem, sc);
                    break;
                case 2:
                    signup(railwayBookingSystem, sc);
                    break;
                case 3:
                    System.out.println("Logging out. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static boolean login(RailwayBookingSystem railwayBookingSystem, Scanner sc) {
        System.out.print("Enter username: ");
        String username = sc.next();
        System.out.print("Enter password: ");
        String password = sc.next(); // Use sc.next() instead of sc.nextLine()

        if (railwayBookingSystem.validateUser(username, password)) {
            System.out.println("Login successful!");
            userDashboard(railwayBookingSystem, sc);
            return true;
        } else {
            System.out.println("Invalid username or password. Please try again.");
            return false;
        }
    }

    private static void signup(RailwayBookingSystem railwayBookingSystem, Scanner sc) {
        System.out.print("Enter username: ");
        String username = sc.next();
        System.out.print("Enter password: ");
        String password = sc.next();
        System.out.print("Enter name: ");
        String name = sc.next();
        System.out.print("Enter email: ");
        String email = sc.next();
        System.out.print("Enter phone number: ");
        String phoneNumber = sc.next();
        System.out.print("Enter age: ");
        int age = sc.nextInt();
        System.out.print("Enter gender (M/F): ");
        String gender = sc.next();

        if (railwayBookingSystem.containsUser(username)) {
            System.out.println("Username already exists. Please choose a different username.");
            return;
        }

        User user = new User(username, password, name, email, phoneNumber);
        UserDAO userDAO = new UserDAO();
        userDAO.createUser(user);
        railwayBookingSystem.createUser(username, password, name, email, phoneNumber);

        // Add code to write user details to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true))) {
            writer.write(user.getUsername() + "," + user.getPassword() + "," + user.getName() + "," + user.getEmail()
                    + "," + user.getPhoneNumber() + "\n");
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }

        // Add code to add user to passenger database
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/railway_db", "root", "");
                PreparedStatement pstmt = conn
                        .prepareStatement("INSERT INTO passenger (name, age, gender) VALUES (?, ?, ?)")) {
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setString(3, gender);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding user to passenger database: " + e.getMessage());
        }

        System.out.println("Signup successful! You can now login.");
    }

    private static void userDashboard(RailwayBookingSystem railwayBookingSystem, Scanner sc) {
        while (true) {
            System.out.println("User Dashboard");
            System.out.println("1. Book Ticket");
            System.out.println("2. Cancel Booking");
            System.out.println("3. Search Train");
            System.out.println("4. View All Train");
            System.out.println("5. View My Bookings");
            System.out.println("6. Logout");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    bookTicket(railwayBookingSystem, sc);
                    break;
                case 2:
                    cancelBooking(railwayBookingSystem, sc);
                    break;
                case 3:
                    searchTrainByName(railwayBookingSystem, sc);
                    break;
                case 4:
                    viewAllTrains(railwayBookingSystem);
                    break;
                case 5:
                    viewMyBookings(railwayBookingSystem, sc);
                    break;
                case 6:
                    System.out.println("Logging out. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void createTrain(RailwayBookingSystem railwayBookingSystem, Scanner sc) {
        System.out.print("Enter train name: ");
        String trainName = sc.nextLine();
        sc.nextLine();
        System.out.print("Enter source: ");
        String source = sc.nextLine();
        System.out.print("Enter destination: ");
        String destination = sc.nextLine();
        System.out.print("Enter departure time (HH:MM AM/PM): ");
        String departureTime = sc.nextLine();
        System.out.print("Enter arrival time (HH:MM AM/PM): ");
        String arrivalTime = sc.nextLine();
        System.out.print("Enter fare: ");
        double fare = sc.nextDouble();
        railwayBookingSystem.createTrain(trainName, source, destination, departureTime, arrivalTime, fare);
    }

    private static void updateTrain(RailwayBookingSystem railwayBookingSystem, Scanner sc) {
        System.out.print("Enter train no: ");
        int trainNo = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter new train name: ");
        String trainName = sc.nextLine();
        System.out.print("Enter new source: ");
        String source = sc.nextLine();
        System.out.print("Enter new destination: ");
        String destination = sc.nextLine();
        System.out.print("Enter new departure time (HH:MM AM/PM): ");
        String departureTime = sc.nextLine();
        System.out.print("Enter new arrival time (HH:MM AM/PM): ");
        String arrivalTime = sc.nextLine();
        System.out.print("Enter new fare: ");
        double fare = sc.nextDouble();
        railwayBookingSystem.updateTrain(trainNo, trainName, source, destination, departureTime, arrivalTime, fare);
    }

    private static void deleteTrain(RailwayBookingSystem railwayBookingSystem, Scanner sc) {
        System.out.print("Enter train no: ");
        int trainNo = sc.nextInt();
        railwayBookingSystem.deleteTrain(trainNo);
    }

    private static void viewAllTrains(RailwayBookingSystem railwayBookingSystem) {
        if (railwayBookingSystem == null) {
            System.out.println("Railway booking system is not available");
            return;
        }

        List<Train> trains = railwayBookingSystem.getAllTrains();
        if (trains == null || trains.isEmpty()) {
            System.out.println("No trains available");
        } else {
            for (Train train : trains) {
                System.out.println("Train No: " + train.getTrainNo());
                System.out.println("Train Name: " + train.getTrainName());
                System.out.println("Source: " + train.getSource());
                System.out.println("Destination: " + train.getDestination());
                System.out.println("Departure Time: " + train.getDepartureTime());
                System.out.println("Arrival Time: " + train.getArrivalTime());
                System.out.printf("Fare: %.2f%n", train.getFare()); // Display fare with two decimal places
                System.out.println();
            }
        }
    }

    private static void viewAllBookings(RailwayBookingSystem railwayBookingSystem) {
        List<Booking> bookings = railwayBookingSystem.getAllBookings();
        if (bookings != null && !bookings.isEmpty()) {
            for (Booking booking : bookings) {
                System.out.println("Booking ID: " + booking.getBookingId());
                System.out.println("Train No: " + booking.getTrainNo());
                System.out.println("User ID: " + booking.getPassengerId());
                System.out.println("Booking Date: " + booking.getBookingDate());
                System.out.println();
            }
        } else {
            System.out.println("No bookings available");
        }
    }

    private static void bookTicket(RailwayBookingSystem railwayBookingSystem, Scanner sc) {
        System.out.print("Enter train no: ");
        int trainNo = sc.nextInt();
        System.out.print("Enter user ID: ");
        int userId = sc.nextInt();
        railwayBookingSystem.bookTicket(trainNo, userId);
    }

    private static void cancelBooking(RailwayBookingSystem railwayBookingSystem, Scanner sc) {
        System.out.print("Enter booking ID: ");
        int bookingId = sc.nextInt();
        railwayBookingSystem.cancelBooking(bookingId);
    }

    private static void viewMyBookings(RailwayBookingSystem railwayBookingSystem, Scanner sc) {
        System.out.print("Enter user ID: ");
        int userId = sc.nextInt();
        List<Booking> bookings = railwayBookingSystem.getMyBookings(userId);
        if (bookings != null && !bookings.isEmpty()) {
            for (Booking booking : bookings) {
                System.out.println("Booking ID: " + booking.getBookingId());
                System.out.println("Train No: " + booking.getTrainNo());
                System.out.println("Booking Date: " + booking.getBookingDate());
                System.out.println();
            }
        } else {
            System.out.println("No bookings available for user ID " + userId);
        }
    }

    private static void searchTrainByName(RailwayBookingSystem railwayBookingSystem, Scanner sc) {
        System.out.print("Enter train name to search: ");
        String trainName = sc.nextLine();
        List<Train> matchingTrains = railwayBookingSystem.searchTrainByName(trainName);
        if (matchingTrains.isEmpty()) {
            System.out.println("No trains found with name " + trainName);
        } else {
            System.out.println("Trains found:");
            for (Train train : matchingTrains) {
                System.out.println("Train No: " + train.getTrainNo());
                System.out.println("Train Name: " + train.getTrainName());
                System.out.println("Source: " + train.getSource());
                System.out.println("Destination: " + train.getDestination());
                System.out.println("Departure Time: " + train.getDepartureTime());
                System.out.println("Arrival Time: " + train.getArrivalTime());
                System.out.println("Fare: " + train.getFare());
                System.out.println();
            }
        }
    }
}

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// >>>>>>>>>>>>>>>>>>>>>>>RailwayBooking Class>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

class RailwayBookingSystem {
    private Map<String, User> users;
    private Map<Integer, Train> trains;
    private Map<String, User> usersMap;

    Connection conn;

    public RailwayBookingSystem() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String dburl = "jdbc:mysql://localhost:3306/railway_db";
        String dbuser = "root";
        String dbpass = "";
        this.users = new HashMap<>();
        this.trains = new HashMap<>();
        this.usersMap = new HashMap<>();

        conn = DriverManager.getConnection(dburl, dbuser, dbpass);
        if (conn != null) {
            System.out.println("Connected to the database");
        } else {
            System.out.println("Failed to connect to the database");
        }

    }

    public boolean containsUser(String username) {
        return usersMap.containsKey(username);
    }

    boolean validateUser(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return true;
        }
        return false;
    }

    void createUser(String username, String password, String name, String email, String phoneNumber) {
        User user = new User(username, password, name, email, phoneNumber);
        users.put(username, user);
    }

    private static class User {
        private String username;
        private String password;
        private String name;
        private String email;
        private String phoneNumber;

        public User(String username, String password, String name, String email, String phoneNumber) {
            this.username = username;
            this.password = password;
            this.name = name;
            this.email = email;
            this.phoneNumber = phoneNumber;
        }

        public String getPassword() {
            return password;
        }

    }

    public void close() throws Exception {
        if (conn != null) {
            conn.close();
        }
    }

    public void createTrain(String trainName, String source, String destination, String departureTime,
            String arrivalTime, double fare) {
        if (conn != null) {
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO train (train_name, source, destination, departure_time, arrival_time, fare) VALUES (?, ?, ?, ?, ?, ?)")) {
                pstmt.setString(1, trainName);
                pstmt.setString(2, source);
                pstmt.setString(3, destination);
                pstmt.setString(4, departureTime);
                pstmt.setString(5, arrivalTime);
                pstmt.setDouble(6, fare); // Set the fare value
                pstmt.executeUpdate();
                System.out.println("Train created successfully");
            } catch (SQLException e) {
                System.out.println("Error creating train: " + e.getMessage());
            }
        } else {
            System.out.println("Connection is null");
        }
    }

    public void updateTrain(int trainNo, String trainName, String source, String destination, String departureTime,
            String arrivalTime, double fare) {
        if (conn != null) {
            String query = "UPDATE train SET train_name = ?, source = ?, destination = ?, departure_time = ?, arrival_time = ?, fare = ? WHERE train_no = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, trainName);
                pstmt.setString(2, source);
                pstmt.setString(3, destination);
                pstmt.setString(4, departureTime);
                pstmt.setString(5, arrivalTime);
                pstmt.setDouble(6, fare);
                pstmt.setInt(7, trainNo);
                pstmt.executeUpdate();
                System.out.println("Train updates successfully");
            } catch (SQLException e) {
                System.out.println("Error updating train: " + e.getMessage());
            }
        } else {
            System.out.println("Connection is null");
        }
    }

    public void deleteTrain(int trainNo) {
        if (conn != null) {
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM train WHERE train_no = ?")) {
                pstmt.setInt(1, trainNo);
                pstmt.executeUpdate();
                System.out.println("Train deleted successfully");
            } catch (SQLException e) {
                System.out.println("Error deleting train: " + e.getMessage());
            }
        } else {
            System.out.println("Connection is null");
        }
    }

    public List<Train> searchTrainByName(String trainName) {
        List<Train> matchingTrains = new ArrayList<>();
        if (conn != null) {
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM train WHERE train_name LIKE ?")) {
                pstmt.setString(1, "%" + trainName + "%");
                try (ResultSet resultSet = pstmt.executeQuery()) {
                    while (resultSet.next()) {
                        Train train = new Train();
                        train.setTrainNo(resultSet.getInt("train_no"));
                        train.setTrainName(resultSet.getString("train_name"));
                        train.setSource(resultSet.getString("source"));
                        train.setDestination(resultSet.getString("destination"));
                        train.setDepartureTime(resultSet.getString("departure_time"));
                        train.setArrivalTime(resultSet.getString("arrival_time"));
                        train.setFare(resultSet.getDouble("fare"));
                        matchingTrains.add(train);
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error searching for train by name: " + e.getMessage());
            }
        } else {
            System.out.println("Connection is null");
        }
        return matchingTrains;
    }

    public List<Train> getAllTrains() {
        List<Train> trains = new ArrayList<>();
        if (conn != null) {
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM train")) {
                try (ResultSet resultSet = pstmt.executeQuery()) {
                    while (resultSet.next()) {
                        Train train = new Train();
                        train.setTrainNo(resultSet.getInt("train_no"));
                        train.setTrainName(resultSet.getString("train_name"));
                        train.setSource(resultSet.getString("source"));
                        train.setDestination(resultSet.getString("destination"));
                        train.setDepartureTime(resultSet.getString("departure_time"));
                        train.setArrivalTime(resultSet.getString("arrival_time"));
                        train.setFare(resultSet.getDouble("fare")); // Retrieve the fare value from the database
                        trains.add(train);
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error retrieving all trains: " + e.getMessage());
            }
        } else {
            System.out.println("Connection is null");
        }
        return trains;
    }

    public void bookTicket(int trainNo, int userId) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter seat number: ");
        int seatNo = sc.nextInt();

        if (conn != null) {
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO booking (train_no, user_id, booking_date, seat_no) VALUES (?, ?, NOW(), ?)")) {
                pstmt.setInt(1, trainNo);
                pstmt.setInt(2, userId);
                pstmt.setInt(3, seatNo);
                pstmt.executeUpdate();
                System.out.println("Ticket booked successfully");
            } catch (SQLException e) {
                System.out.println("Error booking ticket: " + e.getMessage());
            }
        } else {
            System.out.println("Connection is null");
        }
    }

    public void cancelBooking(int bookingId) {
        if (conn != null) {
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM booking WHERE booking_id = ?")) {
                pstmt.setInt(1, bookingId);
                pstmt.executeUpdate();
                System.out.println("Booking cancelled successfully");
            } catch (SQLException e) {
                System.out.println("Error cancelling booking: " + e.getMessage());
            }
        } else {
            System.out.println("Connection is null");
        }
    }

    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        if (conn != null) {
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM booking")) {
                try (ResultSet resultSet = pstmt.executeQuery()) {
                    while (resultSet.next()) {
                        Booking booking = new Booking();
                        booking.setBookingId(resultSet.getInt("booking_id"));
                        booking.setTrainNo(resultSet.getInt("train_no"));
                        booking.setPassengerId(resultSet.getInt("user_id"));
                        booking.setBookingDate(resultSet.getString("booking_date"));
                        bookings.add(booking);
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error retrieving all bookings: " + e.getMessage());
            }
        } else {
            System.out.println("Connection is null");
        }
        return bookings;
    }

    public List<Booking> getMyBookings(int userId) {
        List<Booking> bookings = new ArrayList<>();
        if (conn != null) {
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM booking WHERE user_id = ?")) {
                pstmt.setInt(1, userId);
                try (ResultSet resultSet = pstmt.executeQuery()) {
                    while (resultSet.next()) {
                        Booking booking = new Booking();
                        booking.setBookingId(resultSet.getInt("booking_id"));
                        booking.setTrainNo(resultSet.getInt("train_no"));
                        booking.setPassengerId(resultSet.getInt("user_id"));
                        booking.setBookingDate(resultSet.getString("booking_date"));
                        bookings.add(booking);
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error retrieving my bookings: " + e.getMessage());
            }
        } else {
            System.out.println("Connection is null");
        }
        return bookings;
    }
}

class User {
    private String username;
    private String password;
    private String name;
    private String email;
    private String phoneNumber;

    public User(String username, String password, String name, String email, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

class UserDAO {
    private static final String FILE_NAME = "users.txt";

    public void createUser(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(user.getUsername() + "," + user.getPassword() + "," + user.getName() + "," + user.getEmail()
                    + "," + user.getPhoneNumber() + "\n");
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                User user = new User(userData[0], userData[1], userData[2], userData[3], userData[4]);
                users.add(user);
            }
        } catch (IOException e) {
            System.out.println("Error reading from file: " + e.getMessage());
        }
        return users;
    }
}

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// >>>>>>>>>>>>>>>>>>>>>>>>>>>>Train Class>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
class Train {
    private int trainNo;
    private String trainName;
    private String source;
    private String destination;
    private String departureTime;
    private String arrivalTime;
    private double fare;

    public Train() {
    }

    public Train(int trainNo, String trainName, String source, String destination, String departureTime,
            String arrivalTime) {
        this.trainNo = trainNo;
        this.trainName = trainName;
        this.source = source;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.fare = fare;
    }

    public int getTrainNo() {
        return trainNo;
    }

    public void setTrainNo(int trainNo) {
        this.trainNo = trainNo;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public double getFare() {
        return fare;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }
}

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// >>>>>>>>>>>>>>>>>>>>>>>>>>>>Booking Class>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
class Booking {
    private int bookingId;
    private int passengerId;
    private int trainNo;
    private String bookingDate;
    private int seatNo;
    private double totalFare;

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public int getTrainNo() {
        return trainNo;
    }

    public void setTrainNo(int trainNo) {
        this.trainNo = trainNo;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public int getSeatNo() {
        return seatNo;
    }

    public void setSeatNo(int seatNo) {
        this.seatNo = seatNo;
    }

    public double getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(double totalFare) {
        this.totalFare = totalFare;
    }
}

/*
 * ==========================================================================
 * ========================Stack Class=======================================
 * ==========================================================================
 */
