package org.brightinhere.xyz_retail.infrastructure.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.brightinhere.xyz_retail.application.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private ErrorResponse buildError(
            HttpStatus status,
            String errorCode,
            String message,
            HttpServletRequest request
    ) {
        return new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                errorCode,
                message,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleOrderNotFound(
            OrderNotFoundException ex,
            HttpServletRequest request
    ) {
        log.warn("Order not found: {}", request.getRequestURI());
        return buildError(HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND", ex.getMessage(), request);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleProductNotFound(
            ProductNotFoundException ex,
            HttpServletRequest request
    ) {
        log.warn("Product not found: {}", request.getRequestURI());
        return buildError(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND", ex.getMessage(), request);
    }

    @ExceptionHandler(InventoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleInventoryNotFound(
            InventoryNotFoundException ex,
            HttpServletRequest request
    ) {
        log.warn("Inventory not found: {}", request.getRequestURI());
        return buildError(HttpStatus.NOT_FOUND, "INVENTORY_NOT_FOUND", ex.getMessage(), request);
    }

    @ExceptionHandler(InsufficientStockException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleInsufficientStock(
            InsufficientStockException ex,
            HttpServletRequest request
    ) {
        log.warn("Insufficient stock: {}", request.getRequestURI());
        return buildError(HttpStatus.CONFLICT, "INSUFFICIENT_STOCK", ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidOrderStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleInvalidOrderState(
            InvalidOrderStateException ex,
            HttpServletRequest request
    ) {
        log.warn("Invalid order state: {}", request.getRequestURI());
        return buildError(HttpStatus.CONFLICT, "INVALID_ORDER_STATE", ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + " " + e.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        log.warn("Validation error: {} - {}", request.getRequestURI(), message);
        return buildError(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        log.warn("Illegal argument: {} - {}", request.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), request);
    }

}