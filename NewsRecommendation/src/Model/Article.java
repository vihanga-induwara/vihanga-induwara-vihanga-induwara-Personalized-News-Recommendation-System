package Model;

public class Article {
    private String title;
    private String author;
    private String content;
    private String publishedDate;
    private static int counter = 0; // Static counter shared across all Article instances
    private static final String PREFIX = "ART"; // Optional prefix for IDs

    // Constructor
    public Article(String title, String author, String content, String publishedDate) {
        this.title = title;
        this.author = author;
        this.content = content;
        this.publishedDate = publishedDate;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    // Override toString() for meaningful output
    @Override
    public String toString() {
        return String.format("Title: %s | Author: %s | Date: %s", title, author, publishedDate);
    }


    public String getId() {
        counter++; // Increment the counter for every new ID
        return String.format("%s-%04d", PREFIX, counter); // Format the ID with the prefix and zero-padded counter
    }
}
