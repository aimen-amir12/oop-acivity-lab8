package university.people;

public class UndergraduateStudent extends Student {

    public UndergraduateStudent(String id, String name, String email, String phone) {
        super(id, name, email, phone);
    }

    @Override
    public String getRole() {
        return "Undergraduate Student";
    }

    @Override
    public void register() {
        System.out.println("Undergraduate student registered: " + getName());
        saveToDatabase();
    }

    @Override
    public double calculatePayment() {
        return getCourseCount() * 20000;
    }

    @Override
    public void displayDashboard() {
        System.out.println("=== UG Dashboard for " + getName() + " ===");
        System.out.println("Courses: " + getCourseCount());
        System.out.println("GPA: " + getGpa());
        System.out.println("Balance: " + getFinancialSummary());
    }
}