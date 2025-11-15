package university.people;

public class Administrator extends Staff {

    public Administrator(String id, String name, String email, String phone, double salary) {
        super(id, name, email, phone, salary);
    }

    @Override
    public String getRole() {
        return "Administrator";
    }

    @Override
    public void register() {
        System.out.println("Administrator registered: " + getName());
    }

    @Override
    public double calculatePayment() {
        return getSalary();
    }

    @Override
    public void displayDashboard() {
        System.out.println("Administrator Dashboard for " + getName());
        System.out.println("Tasks: Manage records, oversee departments, handle staff issues.");
    }
}
