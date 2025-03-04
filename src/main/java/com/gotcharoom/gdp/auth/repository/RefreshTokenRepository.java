package com.gotcharoom.gdp.auth.repository;

import com.gotcharoom.gdp.auth.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    boolean existsByAuthName(String authName);
    Optional<RefreshToken> findByAuthName(String authName);
}
