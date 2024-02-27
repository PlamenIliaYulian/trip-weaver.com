package com.tripweaver.services.contracts;

import com.tripweaver.models.Role;

import java.util.List;

public interface RoleService {

    /*Ilia*/
    Role getRoleById(int id);

    /*Plamen*/
    Role getRoleByName(String name);

    /*Yuli - DONE*/
    List<Role> getAllRoles();
}
