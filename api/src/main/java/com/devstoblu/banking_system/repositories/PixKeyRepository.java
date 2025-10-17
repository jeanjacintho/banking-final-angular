package com.devstoblu.banking_system.repositories;

import com.devstoblu.banking_system.enums.PixKeyType;
import com.devstoblu.banking_system.models.PixKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PixKeyRepository extends JpaRepository<PixKey, Long> {
    
    Optional<PixKey> findByKeyTypeAndKeyValue(PixKeyType keyType, String keyValue);
    
    boolean existsByKeyTypeAndKeyValue(PixKeyType keyType, String keyValue);
    
    List<PixKey> findByAccountId(Long accountId);
    
    @Query("SELECT COUNT(p) FROM PixKey p WHERE p.account.id = :accountId")
    long countKeysByAccountId(@Param("accountId") Long accountId);
}
