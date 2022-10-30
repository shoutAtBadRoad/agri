package agri.controller;

import agri.annotation.SaveAuth;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("hello")
    @SaveAuth(roles = {"geust"})
    public String hello() {
        return "hello world";
    }
}
