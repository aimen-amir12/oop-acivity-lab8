package university.people;

public abstract class Person {
    private String id;
    private String name;
    private String email;
    private String phone;

    public Person(String id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // Encapsulation – getters only
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }

    // Abstraction - contract
    public abstract String getRole();
    public abstract void register();
    public abstract double calculatePayment(); // Tuition OR Salary
    public abstract void displayDashboard();
}

