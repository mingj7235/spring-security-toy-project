package com.mj.springsecuritytoyproject.security.service;

import com.mj.springsecuritytoyproject.domain.Account;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class AccountContext extends User {

    private final Account account;

    public AccountContext(Account account, final Collection<? extends GrantedAuthority> authorities) {
        super(account.getUsername(), account.getPassword(), authorities);
        this.account = account;
    }

}
