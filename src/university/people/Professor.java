package university.people;

public class Professor extends Faculty {

    public Professor(String id, String name, String email, String phone, double salary) {
        super(id, name, email, phone, salary);
    }

    @Override
    public String getRole() {
        return "Professor";
    }

    @Override
    public void register() {
        System.out.println("Professor registered: " + getName());
    }

    @Override
    public double calculatePayment() {
        return getSalary();
    }

    @Override
    public void displayDashboard() {
        System.out.println("Professor Dashboard for " + getName());
    }
}
