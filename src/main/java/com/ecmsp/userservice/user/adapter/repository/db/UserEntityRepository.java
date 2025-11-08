package com.ecmsp.userservice.user.adapter.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
interface UserEntityRepository extends JpaRepository<UserEntity, UUID> {
    @Query("SELECT u FROM UserEntity u WHERE u.login = :login")
    Optional<UserEntity> findByLogin(String login);

    @Query("SELECT u FROM UserEntity u WHERE LOWER(u.login) LIKE LOWER(CONCAT('%', :loginFilter, '%'))")
    List<UserEntity> findByLoginContaining(String loginFilter);

    @Modifying
    @Query("UPDATE UserEntity u SET u.login = :newLogin WHERE u.userId = :userId")
    void updateLogin(UUID userId, String newLogin);
}
