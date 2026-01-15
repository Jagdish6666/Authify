package com.Authentication.demo.Repository;

import com.Authentication.demo.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Long>
{

    Optional<UserEntity> findByEmail(String Email);

    Boolean existsByEmail(String email);


}