package com.pawelgorny.transrest.model.util;

public class SearchQueryException extends RuntimeException{
    public SearchQueryException(){
        super();
    }
    public SearchQueryException(String msg){
        super(msg);
    }
}
