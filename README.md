# Commerce Services - Technical Interview

### Context

We are an online book store. We have an API that returns our catalog and stock. 
We want to implement two new features in our system:
- Process new orders
- Retrieve existing orders

### Functional Requirements

- **Create a new Order**:
  - The application receives orders in a JSON format through an HTTP API endpoint (POST).
  - Orders contain a list of books and the quantity.
  - Before registering the order, the system should check if there's enough stock to fulfill the order (`src/main/resources/import.sql` will set the initial stock).
  - If one of the books in the order does not have enough stock we will reject the entire order.
  - After stock validation, the order is marked as a success, and it would return a Unique Order Identifier to the caller of the HTTP API endpoint.
  - If the order was processed we need to update available stock, taking into consideration:
    - Updating stock should not be a blocker for replying to the customer.
    - If the process of updating stock fails, should not cause an error in order processing.

- **Retrieve Orders**:
  - The application has an endpoint to extract a list of existing orders. So that we can run `curl localhost:8080/orders/` and get a list of them

### Required

- Resolution needs to be fully in English
- You need to use Java 17
- This repo contains the existing bookstore code; fork or create a public repository with your solution.
- We expect you to implement tests for the requested functionalities. You decide the scope.
- **Once the code is complete, reply to your hiring person of contact.**

### How to run

Building
```shell
$ ./mvnw compile
```

Test
```shell
$ ./mvnw test
```

Start the application

```shell
$ ./mvnw spring-boot:run
```

Getting current stock for a given book 

```shell
$ curl localhost:8080/books_stock/ae1666d6-6100-4ef0-9037-b45dd0d5bb0e
{"id":"ae1666d6-6100-4ef0-9037-b45dd0d5bb0e","name":"adipisicing culpa Lorem laboris adipisicing","quantity":0}
```
## Solution Setup

1. **Create a PostgreSQL Database:**
   - Ensure PostgreSQL is installed on your machine.
   - Create a new database named `bookstore`:
     ```sql
     CREATE DATABASE bookstore;
     ```
2. **Dump data.sql inserts into your database:** You need that data to start and test the application, it is in `/resources`
3. **Configure Database Credentials:**
   - Open the `application.properties` or `application.yml` file in the `src/main/resources` directory.
   - Set your PostgreSQL database credentials (username and password) in the configuration file:
     ```properties
     spring.datasource.url=jdbc:postgresql://localhost:5432/bookstore
     spring.datasource.username=your-username
     spring.datasource.password=your-password
     ```

4. **Compile and Run the Application:**
   - Navigate to the project directory and compile the project:
     ```bash
     ./mvnw compile
     ```
   - Run the application:
     ```bash
     ./mvnw spring-boot:run
     ```
   - The application will automatically create the necessary schemas in the `bookstore` database.

5. **Test the Endpoints:**
   - Use Postman (or a similar tool) to test each endpoint.
   - The base URL for the API is `http://localhost:8080`.

### Endpoints

Here are some of the key endpoints you can test:

- **Create Order:**  
  `POST /orders/create`  
  Create a new order with the list of items.

- **Get All Orders:**  
  `GET /orders`  
  Retrieve all the orders placed in the system.

- **Get Book Stock By Id:**  
  `GET /books_stock/{id}`  
  Retrieve stock of a book using an ID.

### Entities

The application uses the following main entities:

1. **Order:**
   - Represents a customer order.
   - Contains order details such as order date, status, and associated order items.

2. **OrderItem:**
   - Represents an individual item within an order.
   - Contains details such as the book being ordered and the quantity.

3. **Status:**
   - Represents the status of an order (e.g., Created, Confirmed, Rejected).
   - Can be used to track the state of each order.

# Solution Code Explanation

## Overview

This project manages book orders by validating stock availability and updating stock levels asynchronously. The core functionalities are encapsulated in the `OrderService` and `BookStockService` classes. Below is a detailed explanation of how these components operate.

## Components

### OrderService

The `OrderService` class is responsible for handling order creation and stock validation. Key methods include:

- **`getOrders()`**: Retrieves all orders from the database.

- **`createOrder(OrderRequest orderRequest)`**:
  - **Validation**: Checks if sufficient stock is available for each book in the order request. If stock is insufficient, it collects exceptions and throws an `InsufficientStockException` with a detailed message.
  - **Order Creation**: Creates an `Order` with its items and status, and saves it to the database if all items are validated.
  - **Async Stock Update**: Uses `CompletableFuture` to update stock levels asynchronously. This allows the main thread to return quickly without waiting for the stock updates to complete.

- **`validateAndGetBookStock(String bookId, int requestedQuantity)`**: Retrieves the stock for a book and ensures the requested quantity does not exceed available stock. Throws an `InsufficientStockException` if stock is insufficient.

### BookStockService

The `BookStockService` class manages stock updates asynchronously:

- **`updateStock(OrderRequest orderRequest)`**:
  - **Stock Update**: Processes each item in the order request, adjusts stock levels, and saves the updated stock information.
  - **Async Processing**: Annotated with `@Async` to execute in a separate thread, ensuring the main application flow is not blocked. Returns a `CompletableFuture` that completes once the stock update is finished.

## Error Handling

- **`BookNotFoundException`**: Raised when a book ID in the request does not exist in the stock.
- **`InsufficientStockException`**: Raised when the requested quantity exceeds the available stock for a book.

## Transaction Management

- **`@Transactional` Annotation**: The `createOrder` method is annotated with `@Transactional` to ensure the entire process is atomic. If any part of the order creation fails, the transaction rolls back to maintain database consistency.

## Asynchronous Processing

- **`@Async` Annotation**: Used in `updateStock` to perform stock updates in a non-blocking manner. This improves performance by allowing the main thread to continue processing while stock updates occur in the background.

## Testing

- I Created unit testing for the important functionalities like the `OrderServiceTest` or `BookStockServiceTest`, simulating happy path cases and expected exception cases.
