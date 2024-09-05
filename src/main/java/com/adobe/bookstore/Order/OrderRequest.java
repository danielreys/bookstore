package com.adobe.bookstore.order;

import java.util.List;
import java.time.LocalDateTime;
import com.adobe.bookstore.orderitem.OrderItem;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.adobe.bookstore.orderitem.dto.OrderItemDto;

public class OrderRequest {

    private List<OrderItemDto> items;

    public OrderRequest() {
        this.orderDate = LocalDateTime.now();
    }
    
    public List<OrderItemDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDto> items) {
        this.items = items;
    }
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime orderDate;

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
}
