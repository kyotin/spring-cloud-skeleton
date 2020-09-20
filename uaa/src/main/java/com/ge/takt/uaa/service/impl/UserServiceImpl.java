
package com.ge.takt.uaa.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.takt.common.constant.Constants;
import com.ge.takt.common.converter.PageConverter;
import com.ge.takt.common.dto.PageDto;
import com.ge.takt.common.exceptions.SystemConfigurationException;
import com.ge.takt.common.models.ApiResponse;
import com.ge.takt.system.configuration.dto.FactoryDto;
import com.ge.takt.uaa.dto.UserFilterDTO;
import com.ge.takt.uaa.feignClient.FactoryClient;
import com.ge.takt.uaa.model.Role;
import com.ge.takt.uaa.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.ge.takt.uaa.dto.UserDTO;
import com.ge.takt.uaa.mapper.UserMapper;
import com.ge.takt.uaa.model.User;
import com.ge.takt.uaa.repository.UserRepository;
import com.ge.takt.uaa.service.UserService;

import lombok.extern.slf4j.Slf4j;

import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

import static com.ge.takt.common.constant.Constants.*;
import static com.ge.takt.common.constant.MessageConstant.*;
import static com.ge.takt.common.exceptions.HttpStatusCode.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FactoryClient factoryClient;

    /**
     * Validate infor User for update /create
     *
     * @param dto: dto
     * @throws SystemConfigurationException:SystemConfigurationException
     */
    private void validateInfoUser(UserDTO dto) throws SystemConfigurationException {
        if (Objects.isNull(dto.getActive())) {
            dto.setActive(true);
        }
        if (Objects.isNull(dto.getFactoryId()) && !dto.getRoleId().equals(ROLE_ID_GLOBAL_ADMIN)) {
            throw new SystemConfigurationException(BAD_REQUEST, MESSAGE_FACTORY_ID_INVALID);
        }
        if (Objects.isNull(dto.getUserName())) {
            throw new SystemConfigurationException(BAD_REQUEST, MESSAGE_USER_NAME_INVALID);
        }
        if (Objects.isNull(dto.getFirstName())) {
            throw new SystemConfigurationException(BAD_REQUEST, MESSAGE_FIRST_NAME_INVALID);
        }
        if (Objects.isNull(dto.getLastName())) {
            throw new SystemConfigurationException(BAD_REQUEST, MESSAGE_LAST_NAME_INVALID);
        }
        if (Objects.isNull(dto.getPassword())) {
            throw new SystemConfigurationException(BAD_REQUEST, MESSAGE_PASSWORD_INVALID);
        }
        if (Objects.isNull(dto.getRoleId())) {
            throw new SystemConfigurationException(BAD_REQUEST, MESSAGES_ROLE_ID_INVALID);
        }
    }

    /**
     * Create new User
     *
     * @param userDto: Dto
     * @return UserDTO: dto
     * @throws SystemConfigurationException: SystemConfigurationException
     */
    @Override
    public UserDTO createUser(UserDTO userDto, Long factoryId) throws SystemConfigurationException {
        try {
            checkAuthority(factoryId, userDto);
            log.info("Create new User start");
            validateInfoUser(userDto);
            //Check if create role GLOBAL_ADMIN
            if (userDto.getRoleId() == ROLE_ID_GLOBAL_ADMIN && Objects.nonNull(userRepository.findUserByRoleId(ROLE_ID_GLOBAL_ADMIN))) {
                throw new SystemConfigurationException(BAD_REQUEST, MESSAGES_ROLE_GLOBAL_ADMIN_EXISTED);
            }

            User user = userRepository.findUserByUserName(userDto.getUserName());

            //Check userName existed or not
            if (Objects.nonNull(user)) {
                throw new SystemConfigurationException(BAD_REQUEST, MESSAGES_USER_NAME_EXISTED);
            }
            user = userMapper.userDtoToUser(userDto);
            user.setId(null);

            //Check info Factory
            ApiResponse<List<FactoryDto>> allFactories = factoryClient.getFactories();
            FactoryDto factoryDto = null;
            for (FactoryDto dto: allFactories.getData()) {
                if(dto.getId().equals(user.getFactoryId())){
                    factoryDto = dto;
                    break;
                }
            }
            if (Objects.nonNull(factoryDto)) {
                user.setFactoryId(userDto.getFactoryId());
            } else {
                throw new SystemConfigurationException(NOT_FOUND, MESSAGE_FACTORY_ID_INVALID);
            }

            //Check roleId existed or not
            Role role = roleRepository.findRoleById(userDto.getRoleId());
            if (Objects.isNull(role)) {
                throw new SystemConfigurationException(NOT_FOUND, MESSAGES_ROLE_ID_INVALID);
            }

            user.setRole(role);
            userRepository.save(user);
            UserDTO result = userMapper.userToUserDto(user);
            result.setFactoryName(factoryDto.getFactoryName());
            return result;
        } catch (SystemConfigurationException e) {
            log.error("Create User failed cause: {}", e.getMessage());
            throw new SystemConfigurationException(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("Create User error cause: {}", e.getMessage());
            throw new SystemConfigurationException(INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Get All Users Active
     *
     * @return All userDto: dto
     * @throws SystemConfigurationException: SystemConfigurationException
     */
    @Override
    public List<UserDTO> findAllUsers(Long factoryId) throws SystemConfigurationException {
        try {
            log.info("Get all users active");
            List<User> users = userRepository.findByActiveTrue(factoryId);
            ApiResponse<List<FactoryDto>> allFactories = factoryClient.getFactories();
            List<UserDTO> results = new ArrayList<>();
            users.stream()
                    .filter(Objects::nonNull)
                    .map(user -> {

                        UserDTO dto = userMapper.userToUserDto(user);
                        for (FactoryDto fac : allFactories.getData()) {
                            if (Objects.nonNull(dto.getFactoryId()) && fac.getId().equals(dto.getFactoryId())) {
                                dto.setFactoryName(fac.getFactoryName());
                                results.add(dto);
                                break;
                            } else if (Objects.isNull(dto.getFactoryId())) {
                                dto.setFactoryName(null);
                                results.add(dto);
                                break;
                            }
                        }
                        return user;
                    })
                    .collect(Collectors.toList());
            return results;
        } catch (Exception e) {
            log.error("Get Users error cause: {}", e.getMessage());
            throw new SystemConfigurationException(INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Get user By ID
     *
     * @param id: user ID
     * @return UserDto
     * @throws SystemConfigurationException: SystemConfigurationException
     */
    @Override
    public UserDTO findUserById(Long id, Long factoryId) throws SystemConfigurationException {
        try {
            log.info("Get user by ID: {}", id);
            User user = userRepository.findUserById(id);
            if (Objects.isNull(user) || !user.getActive()) {
                throw new SystemConfigurationException(NOT_FOUND, MESSAGES_USER_NOT_AVAILABLE);
            }

            if (Objects.isNull(user.getFactoryId())) {
                return userMapper.userToUserDto(user);
            }
            ApiResponse<List<FactoryDto>> allFactories = factoryClient.getFactories();
            FactoryDto factoryDto = null;
            for (FactoryDto dto: allFactories.getData()) {
                if(dto.getId().equals(user.getFactoryId())){
                    factoryDto = dto;
                    break;
                }
            }
            if (Objects.nonNull(factoryDto)) {
                user.setFactoryId(factoryDto.getId());
            } else {
                throw new SystemConfigurationException(NOT_FOUND, MESSAGE_FACTORY_ID_INVALID);
            }
            //check authority
            if (Objects.nonNull((factoryId)) && !factoryId.equals(user.getFactoryId())){
                throw new SystemConfigurationException(UNAUTHORIZED, UNAUTHORIZED.getMessage());
            }

            UserDTO result = userMapper.userToUserDto(user);
            result.setFactoryName(factoryDto.getFactoryName());
            return result;
        } catch (SystemConfigurationException e) {
            log.error("Get User failed cause: {}", e.getMessage());
            throw new SystemConfigurationException(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("Get User error cause: {}", e.getMessage());
            throw new SystemConfigurationException(INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }

    /**
     * Soft delete User
     *
     * @param id: Dto
     * @return boolean
     * @throws SystemConfigurationException: SystemConfigurationException
     */
    @Override
    public Boolean deleteUserById(Long id, Long factoryId) throws SystemConfigurationException {
        try {
            log.info("Delete User username: {} start", id);
            User user = userRepository.findUserById(id);

            if (Objects.isNull(user) || !user.getActive()) {
                throw new SystemConfigurationException(NOT_FOUND, MESSAGES_USER_NOT_AVAILABLE);
            }
            //check authority
            if (Objects.nonNull((factoryId)) && !factoryId.equals(user.getFactoryId())){
                throw new SystemConfigurationException(UNAUTHORIZED, UNAUTHORIZED.getMessage());
            }
            if (user.getRole().getRoleName().equals(Constants.ROLE_GLOBAL_ADMIN)) {
                throw new SystemConfigurationException(BAD_REQUEST, MESSAGES_NOT_DELETE_ROLE_GLOBAL_ADMIN);
            }
            user.setActive(false);
            userRepository.save(user);
            return true;
        } catch (SystemConfigurationException e) {
            log.error("Delete User failed cause: {}", e.getMessage());
            throw new SystemConfigurationException(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("Delete User error cause: {}", e.getMessage());
            throw new SystemConfigurationException(INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Update user info
     *
     * @param userDto: Dto
     * @return User
     * @throws SystemConfigurationException: SystemConfigurationException
     */
    @Override
    public UserDTO updateUser(UserDTO userDto, Long factoryId) throws SystemConfigurationException {
        try {
            checkAuthority(factoryId, userDto);
            log.info("Update user id: {} start", userDto.getId());
            validateInfoUser(userDto);
            User user = userRepository.findUserByUserName(userDto.getUserName());
            if (Objects.isNull(user) || !user.getActive()) {
                throw new SystemConfigurationException(NOT_FOUND, MESSAGES_USER_NOT_AVAILABLE);
            }
            user.setFirstName(userDto.getFirstName());
            user.setLastName(userDto.getLastName());
            user.setEmail(userDto.getEmail());


            user.setSso(userDto.getSso());

            //Check info Factory
            ApiResponse<List<FactoryDto>> allFactories = factoryClient.getFactories();
            FactoryDto factoryDto = null;
            for (FactoryDto dto: allFactories.getData()) {
                if(dto.getId().equals(user.getFactoryId())){
                    factoryDto = dto;
                    break;
                }
            }
            if (Objects.nonNull(factoryDto)) {
                user.setFactoryId(factoryDto.getId());
            } else {
                throw new SystemConfigurationException(NOT_FOUND, MESSAGE_FACTORY_ID_INVALID);
            }

            //Check roleId existed or not
            Role role = roleRepository.findRoleById(userDto.getRoleId());
            if (Objects.isNull(role)) {
                throw new SystemConfigurationException(NOT_FOUND, MESSAGES_ROLE_ID_INVALID);
            }

            user.setRole(role);
            if(!userDto.getPassword().equals(user.getPassword())){
                user.setPassword(userDto.getPassword());
                userRepository.save(user);
            }else{
                userRepository.updateUserWithOutPwd(user.getRole(), user.getEmail(), user.getFactoryId(), user.getFirstName()
                        , user.getLastName(), user.getSso(), user.getUserName());
            }
            UserDTO result = userMapper.userToUserDto(user);
            result.setFactoryName(factoryDto.getFactoryName());
            return result;
        } catch (SystemConfigurationException e) {
            log.error("Delete User failed cause: {}", e.getMessage());
            throw new SystemConfigurationException(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("Delete User error cause: {}", e.getMessage());
            throw new SystemConfigurationException(INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Filter Users
     *
     * @param filter:    filter encoded
     * @param page:      page number
     * @param size:      size of page
     * @param sortBy:    sortBy
     * @param sortOrder: sortOrder
     * @return PageDto<UserDto>
     * @throws SystemConfigurationException: SystemConfigurationException
     */
    @Override
    public PageDto<UserDTO> filterUsers(Long factoryId, String filter, Integer page, Integer size, String sortBy, String sortOrder) throws SystemConfigurationException {
        try {
            PageDto<UserDTO> result = new PageDto<>();
            if (size == -1 && filter.equals(Constants.JSON_EMPTY_STRING)) {
                List<UserDTO> userDtos = findAllUsers(factoryId);
                result.setContent(userDtos);
                result.setFirst(true);
                result.setLast(true);
                result.setNumber(0);
                result.setNumberOfElements(userDtos.size());
                result.setSize(size);
                result.setTotalPages(0);
            } else {
                if (size == -1) {
                    size = Integer.MAX_VALUE;
                }
                log.info("Filter users");
                UserFilterDTO filterDTO = objectMapper.readValue(URLDecoder.decode(filter, "UTF-8"), UserFilterDTO.class);
                filterDTO.prepareData();
                Pageable pageable = new PageRequest(page, size, new Sort((Constants.SORT_ASC.equals(sortOrder)) ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy.split(COMMA)));
                Long factoryQuery = factoryId;
                if(Objects.isNull(factoryId)){
                    factoryQuery = filterDTO.getFactoryId();
                }
                Page<User> pageUser = userRepository.filterUser(factoryQuery, filterDTO.getUserName(), pageable);
                result = PageConverter.convertToPageDto(pageUser);
                ApiResponse<List<FactoryDto>> allFactories = factoryClient.getFactories();
                List<UserDTO> userDTOs = new ArrayList<>();
                pageUser.getContent().stream()
                        .filter(Objects::nonNull)
                        .map(user -> {

                            UserDTO dto = userMapper.userToUserDto(user);
                            for (FactoryDto fac : allFactories.getData()) {
                                if (Objects.nonNull(dto.getFactoryId()) && fac.getId().equals(dto.getFactoryId())) {
                                    dto.setFactoryName(fac.getFactoryName());
                                    userDTOs.add(dto);
                                    break;
                                } else if (Objects.isNull(dto.getFactoryId())) {
                                    dto.setFactoryName(null);
                                    userDTOs.add(dto);
                                    break;
                                }
                            }
                            return user;
                        })
                        .collect(Collectors.toList());
                result.setContent(userDTOs);
            }
        return result;
    } catch(Exception e) {
        log.error("Filter User error cause = {}", e.getMessage());
        throw new SystemConfigurationException(INTERNAL_SERVER_ERROR, e.getMessage());
    }

}

    /**
     * get information of User to authorize
     *
     * @param username: username
     * @return user dto
     */
    public UserDTO getUserDetails(String username) {
        Collection<GrantedAuthority> grantedAuthoritiesList = new ArrayList<>();
        UserDTO userDTO = null;
        Optional<User> user = Optional.ofNullable(userRepository.findUserByUserName(username));
        if (user.isPresent()) {
            userDTO = userMapper.userToUserDto(user.get());
            GrantedAuthority grantedAuthority =
                    new SimpleGrantedAuthority("ROLE_" + user.get().getRole().getRoleName());
            grantedAuthoritiesList.add(grantedAuthority);
            userDTO.setGrantedAuthoritiesList(grantedAuthoritiesList);
        }
        return userDTO;
    }

    private void checkAuthority(Long factoryId, UserDTO dto) throws SystemConfigurationException {
        if(Objects.nonNull(factoryId) && !factoryId.equals(dto.getFactoryId())){
            throw new SystemConfigurationException(UNAUTHORIZED, UNAUTHORIZED.getMessage());
        }
    }
}
