package com.mj.springsecuritytoyproject.service.impl;

import com.mj.springsecuritytoyproject.domain.Account;
import com.mj.springsecuritytoyproject.domain.Role;
import com.mj.springsecuritytoyproject.domain.dto.AccountDto;
import com.mj.springsecuritytoyproject.repository.RoleRepository;
import com.mj.springsecuritytoyproject.repository.UserRepository;
import com.mj.springsecuritytoyproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void createUser(final Account account) {

        Role role = roleRepository.findByRoleName("ROLE_USER");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        account.setUserRoles(roles);
        userRepository.save(account);
    }

    @Override
    public void modifyUser(AccountDto accountDto) {

        ModelMapper modelMapper = new ModelMapper();
        Account account = modelMapper.map(accountDto, Account.class);

        // 유저 정보 권한 수정이 있을 경우
        if (accountDto.getRoles() != null) {
            Set<Role> roles = new HashSet<>();
            accountDto.getRoles().forEach(requestRole -> {
                Role role = roleRepository.findByRoleName(requestRole);
                roles.add(role);
            });
            account.setUserRoles(roles);
        }

        account.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        userRepository.save(account);

    }

    @Override
    @Transactional (readOnly = true)
    public AccountDto getUser(Long id) {

        Account account = userRepository.findById(id).orElse(new Account()); // ?
        ModelMapper modelMapper = new ModelMapper();
        AccountDto accountDto = modelMapper.map(account, AccountDto.class);

        List<String> roles = account.getUserRoles()
                .stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());

        accountDto.setRoles(roles);
        return accountDto;
    }

    @Override
    @Transactional (readOnly = true)
    public List<Account> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
