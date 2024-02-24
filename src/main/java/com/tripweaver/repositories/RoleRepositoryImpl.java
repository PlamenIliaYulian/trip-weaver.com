package com.tripweaver.repositories;

import com.tripweaver.models.Role;
import com.tripweaver.repositories.contracts.RoleRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class RoleRepositoryImpl implements RoleRepository {
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
