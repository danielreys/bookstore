package com.adobe.bookstore.exception;

public class InsufficientStockException extends RuntimeException {
    private final String bookName;

    public InsufficientStockException(String message, String bookName) {
        super(message);
        this.bookName = bookName;
    }

    public String getBookName() {
        return bookName;
    }
}