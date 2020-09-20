package com.ge.takt.uaa.mapper;


import com.ge.takt.uaa.dto.UserDTO;
import com.ge.takt.uaa.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserMapper {
	@Mappings({
			@Mapping(source = "role.id", target = "roleId"),
			@Mapping(source = "role.roleName", target = "roleName"),
	})
	UserDTO userToUserDto(User user);

	User userDtoToUser(UserDTO userDTO);

}
