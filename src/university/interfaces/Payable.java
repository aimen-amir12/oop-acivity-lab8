package university.interfaces;

public interface Payable {
    void processPayment(double amount);
    String generateInvoice();
    double getFinancialSummary();
}
