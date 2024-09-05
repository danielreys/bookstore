package com.adobe.bookstore.order;

import com.adobe.bookstore.error.ErrorResponse;
import com.adobe.bookstore.exception.BookNotFoundException;
import com.adobe.bookstore.exception.InsufficientStockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import com.adobe.bookstore.order.OrderService;

import java.util.List;

@RestController
@RequestMapping("/orders")
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest) {
        try {
            Order order = orderService.createOrder(orderRequest);
            return ResponseEntity.ok(order);
        } catch (BookNotFoundException e) {
            log.warn(e.getMessage());
            ErrorResponse errorResponse = new ErrorResponse("Book not found", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (InsufficientStockException e) {
            log.warn(e.getMessage());
            String bookNameResponse = e.getBookName().substring(0, 1).toUpperCase() + e.getBookName().substring(1).toLowerCase();
            ErrorResponse errorResponse = new ErrorResponse("Out of Stock", "Insufficient stock for: " + bookNameResponse);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
        } catch (Exception e) {
            log.error("Unexpected error occurred", e);
            ErrorResponse errorResponse = new ErrorResponse("Internal server error", "An unexpected error occurred. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping
    public ResponseEntity<List<Order>> getOrders() {
        return ResponseEntity.ok(orderService.getOrders());
    }
}