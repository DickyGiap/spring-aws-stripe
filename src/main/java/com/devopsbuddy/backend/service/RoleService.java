package com.devopsbuddy.backend.service;

import com.devopsbuddy.backend.persistence.domain.backend.Plan;
import com.devopsbuddy.backend.persistence.domain.backend.Role;
import com.devopsbuddy.backend.persistence.repositories.PlanRepository;
import com.devopsbuddy.backend.persistence.repositories.RoleRepository;
import com.devopsbuddy.enums.PlansEnum;
import com.devopsbuddy.enums.RolesEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by tedonema on 01/05/2016.
 */
@Service
@Transactional(readOnly = true)
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Returns the role for the given id or null if it couldn't find one.
     *
     * @param roleId The role id
     * @return The role id for the given id or null if it couldn't find one.
     */
    public Role findRoleById(int roleId) {
        return roleRepository.findById(roleId).orElse(null);
    }

    /**
     * It creates a Basic or a Pro plan.
     *
     * @param roleId The plan id
     * @return the created Plan
     * @throws IllegalArgumentException If the plan id is not 1 or 2
     */
    @Transactional
    public Role createRole(int roleId) {

        Role role = null;
        if (roleId == 1) {
            role = roleRepository.save(new Role(RolesEnum.BASIC));
        } else if (roleId == 2) {
            role = roleRepository.save(new Role(RolesEnum.PRO));
        } else if (roleId == 3) {
            role = roleRepository.save(new Role(RolesEnum.ADMIN));
        } else {
            throw new IllegalArgumentException("Role id " + roleId + " not recognised.");
        }

        return role;
    }
}