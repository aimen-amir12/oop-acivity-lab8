package university.people;

public class Lecturer extends Faculty {

    public Lecturer(String id, String name, String email, String phone, double salary) {
        super(id, name, email, phone, salary);
    }

    @Override
    public String getRole() {
        return "Lecturer";
    }

    @Override
    public void register() {
        System.out.println("Lecturer registered: " + getName());
    }

    @Override
    public double calculatePayment() {
        return getSalary();
    }

    @Override
    public void displayDashboard() {
        System.out.println("Lecturer Dashboard");
    }
}
