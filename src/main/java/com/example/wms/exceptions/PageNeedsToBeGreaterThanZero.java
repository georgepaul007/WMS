package com.example.wms.exceptions;

public class PageNeedsToBeGreaterThanZero extends Exception {
    public PageNeedsToBeGreaterThanZero() {
        super("Pageno and page size needs to be greater than zero");
    }
}
