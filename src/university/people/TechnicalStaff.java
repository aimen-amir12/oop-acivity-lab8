package university.people;

public class TechnicalStaff extends Staff {

    public TechnicalStaff(String id, String name, String email, String phone, double salary) {
        super(id, name, email, phone, salary);
    }

    @Override
    public String getRole() {
        return "Technical Staff";
    }

    @Override
    public void register() {
        System.out.println("Technical Staff registered: " + getName());
    }

    @Override
    public double calculatePayment() {
        return getSalary();
    }

    @Override
    public void displayDashboard() {
        System.out.println("Technical Staff Dashboard for " + getName());
        System.out.println("Tasks: Maintain labs, fix technical issues, support IT infrastructure.");
    }
}
