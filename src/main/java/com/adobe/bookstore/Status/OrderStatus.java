package com.adobe.bookstore.status;

public enum OrderStatus {
    CREATED(1L),
    CONFIRMED(2L),
    REJECTED(3L);

    private final Long id;

    OrderStatus(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}