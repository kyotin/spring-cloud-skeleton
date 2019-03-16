package com.ge.takt.uaa.config;

import com.ge.takt.uaa.dto.UserDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.User;

/**
 * Created on 1/24/2019.
 */
@Getter
@Setter
public class CustomUser extends User {
    private static final long serialVersionUID = 1L;

    private Long factoryId;

    public CustomUser(UserDTO userDTO) {
        super(userDTO.getUserName(), userDTO.getPassword(), userDTO.getGrantedAuthoritiesList());
        this.factoryId = userDTO.getFactoryId();

    }
}
