package com.ge.takt.uaa.controller;

import com.ge.takt.common.constant.Constants;
import com.ge.takt.common.constant.UrlConstant;
import com.ge.takt.common.dto.PageDto;
import com.ge.takt.common.exceptions.SystemConfigurationException;
import com.ge.takt.common.models.ApiResponse;
import com.ge.takt.common.utils.SecurityUtil;
import com.ge.takt.uaa.dto.UserDTO;
import com.ge.takt.uaa.mapper.UserMapper;
import com.ge.takt.uaa.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

import static com.ge.takt.common.constant.Constants.FACTORY_ID;

@Api(tags = "User")
@RestController
@RequestMapping(UrlConstant.USER_BASE_URL)
public class UserController {

    private final UserService userService;

    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @ApiOperation(value = "Get all users")
    @GetMapping
    public ApiResponse<List<UserDTO>> getAllUsers(Authentication auth) throws SystemConfigurationException{
        Object factoryIdFromToken = SecurityUtil.extractAttributeFromAuth(auth, "factory_id");
        List<UserDTO> users = factoryIdFromToken == null
                ? userService.findAllUsers(null)
                : userService.findAllUsers(new Long ((Integer) factoryIdFromToken));

        return new ApiResponse<>(users, HttpStatus.OK);
    }

    @ApiOperation(value = "Get user by id")
    @GetMapping(value = UrlConstant.ID_PATH)
    public ApiResponse<UserDTO> getUserById(@PathVariable(name = "id") Long id, Authentication auth) throws SystemConfigurationException{
        Object factoryIdFromToken = SecurityUtil.extractAttributeFromAuth(auth, "factory_id");
        UserDTO user = factoryIdFromToken == null
                ? userService.findUserById(id, null)
                : userService.findUserById(id, new Long ((Integer) factoryIdFromToken));
        return new ApiResponse<>(user, HttpStatus.OK);
    }

    @ApiOperation(value = "Create user")
    @PostMapping
    public ApiResponse<UserDTO> createUser(@RequestBody UserDTO userDTO, Authentication auth) throws SystemConfigurationException {
        Object factoryIdFromToken = SecurityUtil.extractAttributeFromAuth(auth, "factory_id");
        UserDTO user = factoryIdFromToken == null
                ? userService.createUser(userDTO, null)
                : userService.createUser(userDTO, new Long ((Integer) factoryIdFromToken));
        return new ApiResponse<>(user, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Delete user")
    @DeleteMapping(value = UrlConstant.ID_PATH)
    public ApiResponse<Boolean> deleteUserById(@PathVariable(name = "id") Long id, Authentication auth) throws SystemConfigurationException {
        Object factoryIdFromToken = SecurityUtil.extractAttributeFromAuth(auth, "factory_id");
        Boolean result = factoryIdFromToken == null
                ? userService.deleteUserById(id, null)
                : userService.deleteUserById(id, new Long((Integer) factoryIdFromToken));
        return new ApiResponse<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "Update user")
    @PutMapping
    public ApiResponse<UserDTO> updateUser(@RequestBody UserDTO userDTO, Authentication auth) throws SystemConfigurationException{
        Object factoryIdFromToken = SecurityUtil.extractAttributeFromAuth(auth, "factory_id");
        UserDTO user = factoryIdFromToken == null
                ? userService.updateUser(userDTO, null)
                : userService.updateUser(userDTO, new Long ((Integer) factoryIdFromToken));
        return new ApiResponse<>(user, HttpStatus.OK);
    }

    @ApiOperation(value = "Filter users")
    @GetMapping(value = UrlConstant.USER_FILTER_URL)
    public ApiResponse<PageDto<UserDTO>> getFilterUser(
            @RequestParam(value = "filter", required = false, defaultValue = Constants.JSON_EMPTY_STRING) String filter,
            @RequestParam(value = "page", required = false, defaultValue = Constants.DEFAULT_PAGE) int page,
            @RequestParam(value = "size", required = false, defaultValue = Constants.DEFAULT_SIZE) int size,
            @RequestParam(value = "sortBy", required = false, defaultValue = Constants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(value = "sortOrder", required = false, defaultValue = Constants.DEFAULT_SORT_ORDER) String sortOrder,
            Authentication auth
    ) throws SystemConfigurationException {
        Object factoryIdFromToken = SecurityUtil.extractAttributeFromAuth(auth, "factory_id");
        PageDto<UserDTO> result = factoryIdFromToken == null
                            ? userService.filterUsers(null, filter, page, size, sortBy, sortOrder)
                            : userService.filterUsers(new Long((Integer) factoryIdFromToken), filter, page, size, sortBy, sortOrder);
        return new ApiResponse<>(result, HttpStatus.OK);
    }
}
