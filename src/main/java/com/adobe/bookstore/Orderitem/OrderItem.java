package com.adobe.bookstore.orderitem;

import com.adobe.bookstore.order.Order;
import com.adobe.bookstore.bookstock.BookStock;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "order_item")
public class OrderItem {
  @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, foreignKey = @ForeignKey(name = "fk_order_item_order"))
    private Order order;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false, foreignKey = @ForeignKey(name = "fk_book_id"))
    private BookStock book;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @JsonProperty(value = "bookId")
    public String getBookId() {
        return (this.book != null) ? this.book.getId() : null;
    }

    @JsonProperty(value = "orderId")
    public Long getOrderId() {
        return (this.order != null) ? this.order.getId() : null;
    }

 // Getter and Setter for id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getter and Setter for order
    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    // Getter and Setter for book
    public BookStock getBook() {
        return book;
    }

    public void setBook(BookStock book) {
        this.book = book;
    }

    // Getter and Setter for quantity
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
}
