package com.adobe.bookstore.bookstock;

import com.adobe.bookstore.order.OrderRequest;
import com.adobe.bookstore.orderitem.dto.OrderItemDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.mockito.ArgumentCaptor;
import com.adobe.bookstore.exception.BookNotFoundException;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BookStockServiceTest {

    @Mock
    private BookStockRepository bookStockRepository;

    @InjectMocks 
    private BookStockService bookStockService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpdateStockBookNotFound() {
        when(bookStockRepository.findById("1")).thenReturn(Optional.empty());

        OrderRequest orderRequest = new OrderRequest();
        OrderItemDto itemDto = new OrderItemDto();
        itemDto.setBookId("1");
        itemDto.setQuantity(5);
        orderRequest.setItems(List.of(itemDto));

        CompletableFuture<Void> future = bookStockService.updateStock(orderRequest);

        CompletionException thrown = assertThrows(CompletionException.class, future::join);
        assertTrue(thrown.getCause() instanceof BookNotFoundException);
    }

    @Test
    public void testUpdateStockExceptionHandling() {
        when(bookStockRepository.findById("1")).thenThrow(new RuntimeException("Database error"));

        OrderRequest orderRequest = new OrderRequest();
        OrderItemDto itemDto = new OrderItemDto();
        itemDto.setBookId("1");
        itemDto.setQuantity(5);
        orderRequest.setItems(List.of(itemDto));

        CompletableFuture<Void> future = bookStockService.updateStock(orderRequest);

        CompletionException thrown = assertThrows(CompletionException.class, future::join);
        assertTrue(thrown.getCause() instanceof RuntimeException);
    }
}
