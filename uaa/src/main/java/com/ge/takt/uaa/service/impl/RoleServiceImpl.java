package com.ge.takt.uaa.service.impl;

import com.ge.takt.uaa.model.Role;
import com.ge.takt.uaa.repository.RoleRepository;
import com.ge.takt.uaa.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}
