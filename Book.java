
public class Book {

    private int bookID;
    private int ISBN;
    private String title;
    private String author;
    private int releaseYear;

    public Book (int bookID, int ISBN, String title, String author, int releaseYear) {
        this.bookID = bookID;
        this.ISBN = ISBN;
        this.title = title;
        this.author = author;
        this.releaseYear = releaseYear;
    }

    public int getBibID() {
        return bookID;
    }

    public void setBibID(int bookID) {
        this.bookID = bookID;
    }

    public int getISBN() {
        return ISBN;
    }

    public void setISBN(int ISBN) {
        this.ISBN = ISBN;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String titel) {
        this.title = titel;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }
}
