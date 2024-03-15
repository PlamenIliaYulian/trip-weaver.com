package com.tripweaver.services;

import com.tripweaver.helpers.TestHelpers;
import com.tripweaver.models.Role;
import com.tripweaver.repositories.contracts.RoleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.tripweaver.services.helpers.ConstantHelper.ROLE_MEMBER_ID;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTests {

    @Mock
    RoleRepository roleRepository;

    @InjectMocks
    RoleServiceImpl roleService;

    @Test
    public void getRoleById_Should_CallRepository() {
        roleService.getRoleById(Mockito.anyInt());

        Mockito.verify(roleRepository, Mockito.times(1))
                .getRoleById(Mockito.anyInt());
    }

    @Test
    public void getRoleByName_Should_ReturnRole_When_MethodCalled() {
        Mockito.when(roleRepository.getRoleByName(Mockito.anyString()))
                .thenReturn(TestHelpers.createMockRoleMember());

        Role role = roleService.getRoleByName("ROLE_MEMBER");

        Assertions.assertEquals(ROLE_MEMBER_ID, role.getRoleId());
    }

    @Test
    public void getAllRoles_Should_CallRepository() {
        roleService.getAllRoles();

        Mockito.verify(roleRepository, Mockito.times(1))
                .getAllRoles();
    }
}
