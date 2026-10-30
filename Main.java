
import java.io.*;
import java.util.*;

abstract class Employee {
    protected int id;
    protected String name;

    public Employee(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public abstract double calculateSalary();

    public abstract String toCSV();

    public void display() {
        System.out.printf(
            "ID: %d | Name: %s | Salary: Rs. %.2f%n",
            id, name, calculateSalary()
        );
    }
}

class FullTimeEmployee extends Employee {
    private double monthlySalary;

    public FullTimeEmployee(int id, String name, double monthlySalary) {
        super(id, name);
        this.monthlySalary = monthlySalary;
    }

    @Override
    public double calculateSalary() {
        return monthlySalary;
    }

    @Override
    public String toCSV() {
        return id + "," + name + ",FULLTIME," + monthlySalary + ",0";
    }
}

class PartTimeEmployee extends Employee {
    private double hourlyRate;
    private int hoursWorked;

    public PartTimeEmployee(int id, String name,
                            double hourlyRate, int hoursWorked) {
        super(id, name);
        this.hourlyRate = hourlyRate;
        this.hoursWorked = hoursWorked;
    }

    @Override
    public double calculateSalary() {
        return hourlyRate * hoursWorked;
    }

    @Override
    public String toCSV() {
        return id + "," + name + ",PARTTIME,"
                + hourlyRate + "," + hoursWorked;
    }
}

public class Main {
    private static final String FILE_NAME = "employees.csv";
    private static final ArrayList<Employee> employees = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);

    private static Employee findEmployee(int id) {
        for (Employee employee : employees) {
            if (employee.getId() == id) {
                return employee;
            }
        }

        return null;
    }

    private static void loadEmployees() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(file))) {

            String line = reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);

                if (data.length != 5) {
                    continue;
                }

                try {
                    int id = Integer.parseInt(data[0].trim());
                    String name = data[1].trim();
                    String type = data[2].trim();
                    double pay = Double.parseDouble(data[3].trim());
                    int hours = Integer.parseInt(data[4].trim());

                    if (id <= 0 || name.isEmpty() || pay < 0
                            || hours < 0 || findEmployee(id) != null) {
                        continue;
                    }

                    if (type.equalsIgnoreCase("FULLTIME") && hours == 0) {
                        employees.add(
                            new FullTimeEmployee(id, name, pay)
                        );
                    } else if (type.equalsIgnoreCase("PARTTIME")) {
                        employees.add(
                            new PartTimeEmployee(id, name, pay, hours)
                        );
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Skipping invalid record: " + line);
                }
            }

        } catch (IOException e) {
            System.out.println("Unable to read employee file.");
        }
    }

    private static boolean saveEmployees() {
        try (PrintWriter writer =
                     new PrintWriter(new FileWriter(FILE_NAME))) {

            writer.println("ID,Name,Type,Pay,Hours");

            for (Employee employee : employees) {
                writer.println(employee.toCSV());
            }

            return true;

        } catch (IOException e) {
            System.out.println("Unable to save employee records.");
            return false;
        }
    }

    private static int readInt(String message) {
        while (true) {
            System.out.print(message);

            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid whole number.");
            }
        }
    }

    private static double readDouble(String message) {
        while (true) {
            System.out.print(message);

            try {
                double value = Double.parseDouble(scanner.nextLine().trim());

                if (Double.isFinite(value)) {
                    return value;
                }

                System.out.println("Enter a valid finite number.");
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid amount.");
            }
        }
    }

    private static void addEmployee() {
        int id = readInt("Enter employee ID: ");

        if (id <= 0 || findEmployee(id) != null) {
            System.out.println("ID must be positive and unique.");
            return;
        }

        System.out.print("Enter employee name: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty() || name.contains(",")) {
            System.out.println("Enter a valid name without commas.");
            return;
        }

        System.out.println("1. Full-Time Employee");
        System.out.println("2. Part-Time Employee");

        int type = readInt("Choose employee type: ");
        Employee employee;

        if (type == 1) {
            double salary = readDouble("Enter monthly salary: Rs. ");

            if (salary < 0) {
                System.out.println("Salary cannot be negative.");
                return;
            }

            employee = new FullTimeEmployee(id, name, salary);

        } else if (type == 2) {
            double rate = readDouble("Enter hourly rate: Rs. ");
            int hours = readInt("Enter hours worked: ");

            if (rate < 0 || hours < 0) {
                System.out.println("Rate and hours cannot be negative.");
                return;
            }

            employee = new PartTimeEmployee(id, name, rate, hours);

        } else {
            System.out.println("Invalid employee type.");
            return;
        }

        employees.add(employee);

        if (saveEmployees()) {
            System.out.println("Employee added successfully!");
        } else {
            employees.remove(employee);
        }
    }

    private static void viewEmployees() {
        if (employees.isEmpty()) {
            System.out.println("No employee records found.");
            return;
        }

        System.out.println("\n===== EMPLOYEE RECORDS =====");

        for (Employee employee : employees) {
            employee.display();
        }
    }

    private static void searchEmployee() {
        int id = readInt("Enter employee ID: ");
        Employee employee = findEmployee(id);

        if (employee == null) {
            System.out.println("Employee not found.");
        } else {
            employee.display();
        }
    }

    private static void payrollReport() {
        if (employees.isEmpty()) {
            System.out.println("No employees available.");
            return;
        }

        double totalSalary = 0;

        System.out.println("\n===== PAYROLL REPORT =====");

        for (Employee employee : employees) {
            employee.display();
            totalSalary += employee.calculateSalary();
        }

        System.out.printf(
            "\nTotal payroll: Rs. %.2f%n", totalSalary
        );
    }

    public static void main(String[] args) {
        loadEmployees();

        while (true) {
            System.out.println("\n===== EMPLOYEE PAYROLL SYSTEM =====");
            System.out.println("1. Add Employee");
            System.out.println("2. View Employees");
            System.out.println("3. Search Employee");
            System.out.println("4. Generate Payroll Report");
            System.out.println("5. Exit");

            int choice = readInt("Enter choice: ");

            switch (choice) {
                case 1:
                    addEmployee();
                    break;
                case 2:
                    viewEmployees();
                    break;
                case 3:
                    searchEmployee();
                    break;
                case 4:
                    payrollReport();
                    break;
                case 5:
                    System.out.println("Payroll system closed.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}
