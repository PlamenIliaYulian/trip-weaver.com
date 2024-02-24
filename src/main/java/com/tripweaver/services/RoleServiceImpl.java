package com.tripweaver.services;

import com.tripweaver.models.Role;
import com.tripweaver.services.contracts.RoleService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Override
    public Role getRoleById(int id) {
        return null;
    }

    @Override
    public Role getRoleByName(String name) {
        return null;
    }

    @Override
    public List<Role> getAllRoles() {
        return null;
    }
}
