package com.acme.ecommerce.exception;

public class NotEnoughProductsException extends RuntimeException{
    public NotEnoughProductsException() {
        super("Not enough products in stock");
    }
}
