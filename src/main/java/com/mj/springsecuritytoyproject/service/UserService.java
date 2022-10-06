package com.mj.springsecuritytoyproject.service;

import com.mj.springsecuritytoyproject.domain.Account;
import com.mj.springsecuritytoyproject.domain.dto.AccountDto;

import java.util.List;

public interface UserService {

    void createUser(Account account);

    void modifyUser (AccountDto accountDto);

    List<Account> getUsers();

    AccountDto getUser(Long id);

    void deleteUser (Long id);

}
