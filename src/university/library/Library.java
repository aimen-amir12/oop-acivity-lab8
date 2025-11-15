package university.library;

public class Library {

    private Book[] books = new Book[100];
    private int bookCount = 0;

    public void addBook(Book b) {
        if (bookCount < books.length) {
            books[bookCount] = b;
            bookCount++;
        }
    }

    public void showBooks() {
        System.out.println("Library Books:");
        for (int i = 0; i < bookCount; i++) {
            System.out.println("- " + books[i].getTitle());
        }
    }
}
