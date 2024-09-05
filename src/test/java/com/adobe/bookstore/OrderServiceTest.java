package com.adobe.bookstore;

import com.adobe.bookstore.bookstock.BookStock;
import com.adobe.bookstore.bookstock.BookStockRepository;
import com.adobe.bookstore.exception.BookNotFoundException;
import com.adobe.bookstore.exception.InsufficientStockException;
import com.adobe.bookstore.order.Order;
import com.adobe.bookstore.order.OrderRequest;
import com.adobe.bookstore.order.OrderService;
import com.adobe.bookstore.orderitem.dto.OrderItemDto;
import com.adobe.bookstore.status.OrderStatus;
import com.adobe.bookstore.status.Status;
import com.adobe.bookstore.status.StatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private BookStockRepository bookStockRepository;

    @Autowired
    private StatusRepository statusRepository;

    @BeforeEach
    public void setUp() {
        bookStockRepository.deleteAll();
        statusRepository.deleteAll();
    }

    @Test
    public void testCreateOrderSuccess() {
        BookStock bookStock = new BookStock();
        bookStock.setId("1");
        bookStock.setName("Book A");
        bookStock.setQuantity(10);
        bookStockRepository.save(bookStock);

        Status confirmedStatus = new Status();
        confirmedStatus.setName(OrderStatus.CONFIRMED);
        statusRepository.save(confirmedStatus);

        OrderRequest orderRequest = new OrderRequest();
        List<OrderItemDto> items = new ArrayList<>();
        OrderItemDto itemDto = new OrderItemDto();
        itemDto.setBookId("1");
        itemDto.setQuantity(5);
        items.add(itemDto);
        orderRequest.setItems(items);

        Order order = orderService.createOrder(orderRequest);

        assertNotNull(order);
        assertEquals(OrderStatus.CONFIRMED, order.getStatus().getName());
    }

    @Test
    public void testCreateOrderInsufficientStock() {
        BookStock bookStock = new BookStock();
        bookStock.setId("1");
        bookStock.setName("Book A");
        bookStock.setQuantity(1); 
        bookStockRepository.save(bookStock);

        Status confirmedStatus = new Status();
        confirmedStatus.setName(OrderStatus.CONFIRMED);
        statusRepository.save(confirmedStatus);

        OrderRequest orderRequest = new OrderRequest();
        List<OrderItemDto> items = new ArrayList<>();
        OrderItemDto itemDto = new OrderItemDto();
        itemDto.setBookId("1");
        itemDto.setQuantity(5);
        items.add(itemDto);
        orderRequest.setItems(items);

        InsufficientStockException thrown = assertThrows(InsufficientStockException.class, () -> {
            orderService.createOrder(orderRequest);
        });

        assertEquals("Insufficient stock", thrown.getMessage());
    }

    @Test
    public void testCreateOrderBookNotFound() {
        // Setup
        Status confirmedStatus = new Status();
        confirmedStatus.setName(OrderStatus.CONFIRMED);
        statusRepository.save(confirmedStatus);

        OrderRequest orderRequest = new OrderRequest();
        List<OrderItemDto> items = new ArrayList<>();
        OrderItemDto itemDto = new OrderItemDto();
        itemDto.setBookId("1");
        itemDto.setQuantity(5);
        items.add(itemDto);
        orderRequest.setItems(items);

        BookNotFoundException thrown = assertThrows(BookNotFoundException.class, () -> {
            orderService.createOrder(orderRequest);
        });

        assertEquals("Book with ID 1 not found", thrown.getMessage());
    }
}
