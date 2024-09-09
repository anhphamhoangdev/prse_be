package com.hcmute.prse_be;

import com.hcmute.prse_be.service.LogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GreetingsController {
    @GetMapping("")
    public String index() {
        return "index";
    }
}
