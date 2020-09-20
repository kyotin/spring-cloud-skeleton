package com.ge.takt.uaa.feignClient;

import com.ge.takt.common.constant.UrlConstant;
import com.ge.takt.common.models.ApiResponse;
import com.ge.takt.system.configuration.dto.FactoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "ge-takt-system-configuration")
public interface FactoryClient {
    @GetMapping(value = UrlConstant.FACTORY_BASE_URL + UrlConstant.ID_PATH, consumes = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<FactoryDto> getFactoryById(@PathVariable(name = "id") Long id);
    @GetMapping(value = UrlConstant.FACTORY_BASE_URL + "/all")
    ApiResponse<List<FactoryDto>> getFactories();
}
