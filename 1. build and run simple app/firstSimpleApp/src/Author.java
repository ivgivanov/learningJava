import java.util.ArrayList;
import java.util.List;

public class Author extends Human {
    private String pseudonym;
    private List<String> books = new ArrayList<String>();

    public String getPseudonym() {
        return pseudonym;
    }

    public void setPseudonym(String pseudonym) {
        this.pseudonym = pseudonym;
    }

    public List<String> getBooks() {
        return books;
    }

    public void setBooks(List<String> books) {
        this.books = books;
    }

    public void addBook(String book) {
        this.books.add(book);
    }
    
    @Override
    public String getName() {
        return "author " + super.getName();
    }

}