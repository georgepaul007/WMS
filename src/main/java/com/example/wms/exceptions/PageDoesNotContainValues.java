package com.example.wms.exceptions;

public class PageDoesNotContainValues extends Exception {
    public PageDoesNotContainValues() {
        super("Page is empty");
    }
}
