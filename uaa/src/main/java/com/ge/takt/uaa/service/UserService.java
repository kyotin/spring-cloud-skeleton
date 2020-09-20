package com.ge.takt.uaa.service;

import com.ge.takt.common.dto.PageDto;
import com.ge.takt.common.exceptions.SystemConfigurationException;
import com.ge.takt.uaa.dto.UserDTO;

import java.util.List;

public interface UserService {

	List<UserDTO> findAllUsers(Long factoryId) throws SystemConfigurationException;

	UserDTO findUserById(Long id, Long factoryId) throws SystemConfigurationException;
	
	UserDTO createUser(UserDTO userDTO, Long factoryId) throws SystemConfigurationException;

	Boolean deleteUserById(Long id, Long factoryId) throws SystemConfigurationException;

	UserDTO updateUser(UserDTO userDTO, Long factoryId) throws SystemConfigurationException;

	PageDto<UserDTO> filterUsers(Long factoryId, String filter, Integer page, Integer size, String sortBy, String sortOrder) throws SystemConfigurationException;

	UserDTO getUserDetails(String username);
}


