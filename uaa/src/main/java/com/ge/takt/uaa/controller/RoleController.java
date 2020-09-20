package com.ge.takt.uaa.controller;

import com.ge.takt.common.models.ApiResponse;
import com.ge.takt.uaa.model.Role;
import com.ge.takt.uaa.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "Role")
@RestController
public class RoleController {

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @ApiOperation(value = "Get all roles")
    @GetMapping(value = "/all-roles")
    public ApiResponse<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return new ApiResponse<>(roles, HttpStatus.OK);
    }
}
