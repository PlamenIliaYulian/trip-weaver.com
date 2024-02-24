package com.tripweaver.services.contracts;

import com.tripweaver.models.Role;

import java.util.List;

public interface RoleService {

    /*ToDo Ilia*/
    Role getRoleById(int id);

    /*ToDo Plamen*/
    Role getRoleByName(String name);

    /*ToDo Yuli*/
    List<Role> getAllRoles();
}
