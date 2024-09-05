package com.adobe.bookstore.order;

import com.adobe.bookstore.bookstock.BookStock;
import com.adobe.bookstore.bookstock.BookStockRepository;
import com.adobe.bookstore.exception.BookNotFoundException;
import com.adobe.bookstore.exception.InsufficientStockException;
import com.adobe.bookstore.orderitem.OrderItem;
import com.adobe.bookstore.orderitem.dto.OrderItemDto;
import com.adobe.bookstore.status.OrderStatus;
import com.adobe.bookstore.status.Status;
import com.adobe.bookstore.status.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.adobe.bookstore.bookstock.BookStockService;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import java.util.stream.Collectors;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OrderService {

    private final BookStockRepository bookStockRepository;
    private final OrderRepository orderRepository;
    private final StatusRepository statusRepository;

    @Autowired
    private BookStockService bookStockService;

    @Autowired
    public OrderService(BookStockRepository bookStockRepository, OrderRepository orderRepository, StatusRepository statusRepository) {
        this.bookStockRepository = bookStockRepository;
        this.orderRepository = orderRepository;
        this.statusRepository = statusRepository;
    }

    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public Order createOrder(OrderRequest orderRequest) {
        List<InsufficientStockException> stockExceptions = new ArrayList<>();

        for (OrderItemDto itemDTO : orderRequest.getItems()) {
            try {
                validateAndGetBookStock(itemDTO.getBookId(), itemDTO.getQuantity());
            } catch (InsufficientStockException e) {
                stockExceptions.add(e);
            }
        }

        if (!stockExceptions.isEmpty()) {
            String errorMessage = stockExceptions.stream()
                .map(e ->e.getBookName().substring(0,1).toUpperCase() + e.getBookName().substring(1).toLowerCase())
                .collect(Collectors.joining(", "));
            throw new InsufficientStockException("Insufficient stock",  "for the books: " + errorMessage);
        }

        Order order = new Order();
        order.setOrderDate(orderRequest.getOrderDate());

        Status confirmedStatus = statusRepository.findByName(OrderStatus.CONFIRMED)
                .orElseThrow(() -> new RuntimeException("Status " + OrderStatus.CONFIRMED + " not found"));
        order.setStatus(confirmedStatus);

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemDto itemDTO : orderRequest.getItems()) {
            BookStock bookStock = bookStockRepository.findById(itemDTO.getBookId()).orElse(null);

            OrderItem item = new OrderItem();
            item.setBook(bookStock);
            item.setQuantity(itemDTO.getQuantity());
            item.setOrder(order);
            orderItems.add(item);
        }

        order.setOrderItems(orderItems);
        order = orderRepository.save(order);

        CompletableFuture<Void> future = bookStockService.updateStock(orderRequest);
        future.exceptionally(ex -> {
            log.error("Failed to update stock", ex);
            return null;
        });

        return order;
    }

    private BookStock validateAndGetBookStock(String bookId, int requestedQuantity) {
        BookStock bookStock = bookStockRepository.findById(bookId)
            .orElseThrow(() -> new BookNotFoundException("Book with ID " + bookId + " not found"));

        if (bookStock.getQuantity() < requestedQuantity) {
            throw new InsufficientStockException("Insufficient stock for book with ID " + bookId, bookStock.getName());
        }
        return bookStock;
    }
}