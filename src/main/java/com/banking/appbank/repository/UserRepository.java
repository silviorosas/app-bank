package com.banking.appbank.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banking.appbank.entity.User;

public interface UserRepository extends JpaRepository<User,Long>{

    Boolean existsByEmail (String email);

    Boolean existsByAccountNumber(String accountNumber);

    User findByAccountNumber(String accountNumber);

    Optional<User> findByEmail(String email);
    
}
