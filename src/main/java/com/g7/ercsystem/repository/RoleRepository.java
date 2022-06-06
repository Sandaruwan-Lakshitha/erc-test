package com.g7.ercsystem.repository;

import com.g7.ercsystem.rest.auth.model.EnumRole;
import com.g7.ercsystem.rest.auth.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("RoleRepository")
public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByName(EnumRole name);
}
