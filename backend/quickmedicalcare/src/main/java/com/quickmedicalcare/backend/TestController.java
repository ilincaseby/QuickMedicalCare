package com.quickmedicalcare.backend;

import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/api/v1")
public class TestController {

    @GetMapping("/sayHelloToMe")
    public String sayHelloToMe() {
        //System.out.println("Hello World");
        return "Hell!";
    }
}
