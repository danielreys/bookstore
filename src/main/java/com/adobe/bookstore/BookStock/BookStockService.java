package com.adobe.bookstore.bookstock;

import com.adobe.bookstore.order.OrderRequest;
import com.adobe.bookstore.bookstock.BookStockRepository;
import com.adobe.bookstore.orderitem.dto.OrderItemDto;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import com.adobe.bookstore.exception.BookNotFoundException;

@Service
public class BookStockService {

    private final BookStockRepository bookStockRepository;

    @Autowired
    public BookStockService(BookStockRepository bookStockRepository) {
        this.bookStockRepository = bookStockRepository;
    }

    @Async
    public CompletableFuture<Void> updateStock(OrderRequest orderRequest) {
        try {
            for (OrderItemDto itemDTO : orderRequest.getItems()) {
                BookStock bookStock = bookStockRepository.findById(itemDTO.getBookId())
                    .orElseThrow(() -> new CompletionException(new BookNotFoundException("Book with ID " + itemDTO.getBookId() + " not found")));                if (bookStock != null) {
                    bookStock.setQuantity(bookStock.getQuantity() - itemDTO.getQuantity());
                    bookStockRepository.save(bookStock);
                }
            }
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
           return CompletableFuture.failedFuture(e);
        }
    }
}