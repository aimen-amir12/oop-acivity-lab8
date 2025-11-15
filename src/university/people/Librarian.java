package university.people;

public class Librarian extends Staff {

    public Librarian(String id, String name, String email, String phone, double salary) {
        super(id, name, email, phone, salary);
    }

    @Override
    public String getRole() {
        return "Librarian";
    }

    @Override
    public void register() {
        System.out.println("Librarian registered: " + getName());
    }

    @Override
    public double calculatePayment() {
        return getSalary();
    }

    @Override
    public void displayDashboard() {
        System.out.println("Librarian Dashboard for " + getName());
        System.out.println("Tasks: Manage books, help students, organize library inventory.");
    }
}
