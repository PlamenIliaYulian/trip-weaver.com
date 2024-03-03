package com.tripweaver.services;

import com.tripweaver.repositories.contracts.RoleRepository;
import com.tripweaver.services.contracts.RoleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTests {


    @Mock
    RoleRepository roleRepository;

    @InjectMocks
    RoleServiceImpl roleService;

    /*Ilia*/
    @Test
    public void getRoleById_Should_CallRepository() {
        roleService.getRoleById(Mockito.anyInt());

        Mockito.verify(roleRepository, Mockito.times(1))
                .getRoleById(Mockito.anyInt());
    }
}
