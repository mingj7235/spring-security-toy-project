package com.mj.springsecuritytoyproject.controller.user;

import com.mj.springsecuritytoyproject.domain.Account;
import com.mj.springsecuritytoyproject.domain.dto.AccountDto;
import com.mj.springsecuritytoyproject.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @GetMapping ("mypage")
    public String myPage() {
        return "user/mypage";
    }

    @GetMapping ("/users")
    public String createUser () {
        return "user/login/register";
    }

    @PostMapping ("/users")
    public String createUser (AccountDto accountDto) {

        /**
         * ModelMapper -> Bean 등록
         */
        ModelMapper modelMapper = new ModelMapper();
        Account account = modelMapper.map(accountDto, Account.class);
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        userService.createUser(account);

        return "redirect:/";
    }


}
