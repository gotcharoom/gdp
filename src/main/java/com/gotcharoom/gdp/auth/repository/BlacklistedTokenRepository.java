package com.gotcharoom.gdp.auth.repository;

import com.gotcharoom.gdp.auth.entity.BlacklistedToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlacklistedTokenRepository extends CrudRepository<BlacklistedToken, String> {

    boolean existsByToken(String token);
}
