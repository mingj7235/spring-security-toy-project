package com.mj.springsecuritytoyproject.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController {

    @GetMapping ("/messages")
    public String message() {
        return "user/messages";
    }

    @GetMapping ("/api/messages")
    @ResponseBody
    public String apiMessages () {
        return "messages ok";
    }
}
