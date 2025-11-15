package university.people;

import university.interfaces.Researchable;

public class PhDStudent extends Student implements Researchable {

    public PhDStudent(String id, String name, String email, String phone) {
        super(id, name, email, phone);
    }

    @Override
    public String getRole() {
        return "PhD Student";
    }

    @Override
    public void register() {
        System.out.println("PhD student registered: " + getName());
    }

    @Override
    public double calculatePayment() {
        return getCourseCount() * 30000;
    }

    @Override
    public void displayDashboard() {
        System.out.println("PhD Dashboard for " + getName());
    }

    @Override
    public void publishPaper(String title) {
        System.out.println(getName() + " published paper: " + title);
    }

    @Override
    public void conductResearch() {
        System.out.println(getName() + " is conducting research...");
    }

    @Override
    public void applyForGrant(double amount) {
        System.out.println(getName() + " applied for grant: " + amount);
    }
}
