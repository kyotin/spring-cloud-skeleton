package com.ge.takt.uaa.dto;


import com.ge.takt.common.dto.BaseDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO extends BaseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String firstName;

    private String lastName;

    private String sso;

    private String email;

    private String password;

    private Long roleId;

    private String roleName;

    private String userName;

    private Long factoryId;

    private String factoryName;

    @ApiModelProperty(hidden = true)
    private Collection<GrantedAuthority> grantedAuthoritiesList = new ArrayList<>();
}
