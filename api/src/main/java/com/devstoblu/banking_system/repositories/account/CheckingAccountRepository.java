package com.devstoblu.banking_system.repositories.account;

import com.devstoblu.banking_system.models.account.CheckingAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckingAccountRepository extends JpaRepository<CheckingAccount, Long> {
}
