package university.people;

public class AssistantProfessor extends Faculty {

    public AssistantProfessor(String id, String name, String email, String phone, double salary) {
        super(id, name, email, phone, salary);
    }

    @Override
    public String getRole() {
        return "Assistant Professor";
    }

    @Override
    public void register() {
        System.out.println("Assistant Professor registered: " + getName());
    }

    @Override
    public double calculatePayment() {
        return getSalary();
    }

    @Override
    public void displayDashboard() {
        System.out.println("Assistant Professor Dashboard");
    }
}
