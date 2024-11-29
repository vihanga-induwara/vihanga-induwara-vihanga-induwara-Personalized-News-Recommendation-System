package Model;

public class Article {
    private String title;
    private String author;
    private String content;
    private String publishedDate;

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
}
