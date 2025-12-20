package university.people;

public class GraduateStudent extends Student {
    private String advisor;

    public GraduateStudent(String id, String name, String email, String phone, String advisor) {
        super(id, name, email, phone);
        this.advisor = advisor;
    }

    @Override
    public String getRole() {
        return "Graduate Student";
    }

    @Override
    public void register() {
        System.out.println("Graduate student registered: " + getName());
        saveToDatabase();
    }

    @Override
    public double calculatePayment() {
        return getCourseCount() * 25000;
    }

    @Override
    public void displayDashboard() {
        System.out.println("=== Graduate Dashboard for " + getName() + " ===");
        System.out.println("Advisor: " + advisor);
        System.out.println("Courses: " + getCourseCount());
        System.out.println("Balance: " + getFinancialSummary());
    }

   // @Override
    protected String getAdvisor() {
        return this.advisor;
    }
}