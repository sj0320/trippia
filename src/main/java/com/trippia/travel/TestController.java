package com.trippia.travel;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/select-city")
    public String test(){
        return "select-city";
    }

    @GetMapping("/calendar")
    public String calendar(){
        return "calendar";
    }

}
