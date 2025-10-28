package com.hanghae.common.exception;

import com.hanghae.common.dto.ErrorResponse;
import com.hanghae.common.exception.order.InvalidOrderStatusException;
import com.hanghae.common.exception.order.OrderNotFoundException;
import com.hanghae.common.exception.product.OutOfStockException;
import com.hanghae.common.exception.product.ProductNotFoundException;
import com.hanghae.common.exception.user.AddressNotFoundException;
import com.hanghae.common.exception.user.AuthCodeMismatchException;
import com.hanghae.common.exception.user.InvalidTokenException;
import com.hanghae.common.exception.user.PaymentMethodNotFoundException;
import com.hanghae.common.exception.user.TokenMismatchException;
import com.hanghae.common.exception.user.UnauthorizedException;
import com.hanghae.common.exception.user.UserAlreadyExistsException;
import com.hanghae.common.exception.user.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({AuthCodeMismatchException.class, TokenMismatchException.class,
        OutOfStockException.class, InvalidOrderStatusException.class})
    public ResponseEntity<ErrorResponse> handleAuthCodeMismatchException(RuntimeException ex) {
        log.error(ex.getClass() + " : " + ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(
            "BAD_REQUEST", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UnauthorizedException.class, InvalidTokenException.class})
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(RuntimeException ex) {
        log.error(ex.getClass() + " : " + ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(
            "UNAUTHORIZED", ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(RuntimeException ex) {
        log.error(ex.getClass() + " : " + ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(
            "FORBIDDEN", ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({UserNotFoundException.class, AddressNotFoundException.class,
        PaymentMethodNotFoundException.class, ProductNotFoundException.class, OrderNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundException(RuntimeException ex) {
        log.error(ex.getClass() + " : " + ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(
            "NOT_FOUND", ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(RuntimeException ex) {
        log.error(ex.getClass() + " : " + ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(
            "CONFLICT", ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exception(Exception ex) {
        log.error(ex.getClass() + " : " + ex.getMessage());
        return new ResponseEntity<>("시스템 내부에 오류가 발생하였습니다. " + ex.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
