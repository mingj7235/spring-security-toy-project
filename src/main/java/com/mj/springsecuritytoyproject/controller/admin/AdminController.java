package com.mj.springsecuritytoyproject.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class AdminController {

    @GetMapping ("/admin")
    public String home () {
        return "admin/home";
    }
}
