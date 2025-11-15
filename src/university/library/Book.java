package university.library;

public class Book {
    private String title;
    private String author;
    private boolean isBorrowed = false;

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }

    public boolean borrow() {
        if (!isBorrowed) {
            isBorrowed = true;
            return true;
        }
        return false;
    }

    public void returnBook() {
        isBorrowed = false;
    }

    public String getTitle() { return title; }
}
