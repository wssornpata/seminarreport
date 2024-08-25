package com.exercise.seminarreport.service;

import org.springframework.stereotype.Service;

@Service
public class HelloService {
    public String sayHello() {
        return "Hello World!";
    }

    public String sayHello(String name) {
        return "Hello " + name;
    }
}
