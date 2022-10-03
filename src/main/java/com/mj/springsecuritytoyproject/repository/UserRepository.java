package com.mj.springsecuritytoyproject.repository;

import com.mj.springsecuritytoyproject.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Account, Long> {

    Account findByUsername(String username);

}
