package com.tripweaver.services.contracts;

import com.tripweaver.models.Role;

import java.util.List;

public interface RoleService {

    Role getRoleById(int id);

    Role getRoleByName(String name);

    List<Role> getAllRoles();
}
