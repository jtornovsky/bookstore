package com.interview.bookstore.service.thirdparty.nytimes;

public class ReviewResult {

    private String book_title;
    private String book_author;
    private String summary;

    public String getBook_title() {
        return book_title;
    }

    public void setBook_title(String book_title) {
        this.book_title = book_title;
    }

    public String getBook_author() {
        return book_author;
    }

    public void setBook_author(String book_author) {
        this.book_author = book_author;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        return (
            "ReviewResult{" +
            "book_title='" +
            book_title +
            '\'' +
            ", book_author='" +
            book_author +
            '\'' +
            ", summary='" +
            summary +
            '\'' +
            '}'
        );
    }
}
