package com.example.AuthwithFirebase.FirebaseSpringboot.dummy.exceptionHandler;

public class FirebaseError extends RuntimeException{
    private String msg;
    public FirebaseError(String msg){
        this.msg=msg;
    }
}
