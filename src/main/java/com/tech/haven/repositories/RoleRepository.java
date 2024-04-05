package com.tech.haven.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tech.haven.models.Role;

public interface RoleRepository extends JpaRepository<Role, String> {

}
