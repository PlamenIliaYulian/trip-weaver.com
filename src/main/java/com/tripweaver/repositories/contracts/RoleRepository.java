package com.tripweaver.repositories.contracts;

import com.tripweaver.models.Role;

import java.util.List;

public interface RoleRepository {

    Role getRoleById(int id);

    Role getRoleByName(String name);

    List<Role> getAllRoles();
}
