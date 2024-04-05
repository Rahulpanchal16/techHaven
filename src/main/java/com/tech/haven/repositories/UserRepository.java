package com.tech.haven.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tech.haven.models.User;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    @Query("select u from User u where u.name= :username")
    Optional<User> findByUsername(@Param(value = "username") String userName);

    Optional<List<User>> findByNameContaining(String keyword);

    @Query("select u from User u where u.email= :email")
    Optional<User> getUserByEmail(@Param(value = "email") String email);

    Optional<List<User>> findByGenderContaining(String gender);

}
