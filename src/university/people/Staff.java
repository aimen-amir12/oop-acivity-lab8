package university.people;

import university.interfaces.Payable;

public abstract class Staff extends Person implements Payable {

    private double salary;
    private double balance = 0;

    public Staff(String id, String name, String email, String phone, double salary) {
        super(id, name, email, phone);
        this.salary = salary;
    }

    public double getSalary() {
        return salary;
    }

    @Override
    public String getPersonType() {
        return "Staff";
    }

    protected void updateSalary(double salary) {
        this.salary = salary;
    }

    @Override
    public void processPayment(double amount) {
        balance += amount;
        System.out.println(getName() + " received salary payment: " + amount);
    }

    @Override
    public String generateInvoice() {
        return "Salary summary for " + getName() + ": Received = " + balance;
    }

    @Override
    public double getFinancialSummary() {
        return balance;
    }
}
