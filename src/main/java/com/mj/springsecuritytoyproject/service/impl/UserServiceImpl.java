package com.mj.springsecuritytoyproject.service.impl;

import com.mj.springsecuritytoyproject.domain.Account;
import com.mj.springsecuritytoyproject.repository.UserRepository;
import com.mj.springsecuritytoyproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public void createUser(final Account account) {

        userRepository.save(account);
    }

}
