package com.ge.takt.uaa.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserFilterDTO {
    private String userName;
    private Long factoryId;

    public void prepareData(){
        if(userName == null){
            userName = "";
        }
        this.userName = buildQueryField(userName);
    }

    private String buildQueryField(String field) {
        return field + "%";
    }
}
