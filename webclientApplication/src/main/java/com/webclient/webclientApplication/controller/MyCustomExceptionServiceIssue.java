package com.webclient.webclientApplication.controller;

public class MyCustomExceptionServiceIssue extends RuntimeException{
    public MyCustomExceptionServiceIssue(String message) {
        super(message);
    }
}
