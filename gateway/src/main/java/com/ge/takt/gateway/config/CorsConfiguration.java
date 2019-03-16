package com.ge.takt.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Configuration
public class CorsConfiguration {

	private static final String ALLOWED_METHODS = "GET, PUT, POST, DELETE, OPTIONS";
	private static final String ALLOWED_ORIGIN = "*";
	private static final String MAX_AGE = "3600";

	@Bean
	public WebFilter corsFilter() {
		return (ServerWebExchange ctx, WebFilterChain chain) -> {
			ServerHttpRequest request = ctx.getRequest();
			if (CorsUtils.isCorsRequest(request)) {
				ServerHttpResponse response = ctx.getResponse();
				HttpHeaders headers = response.getHeaders();
				headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ALLOWED_ORIGIN);
				headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, ALLOWED_METHODS);
				headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, MAX_AGE);
				headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type, content-type, X-Requested-With, Accept, accept, Origin, Access-Control-Request-Method, Access-Control-Request-Headers, Access-Control-Allow-Headers, Authorization, authorization, Cache-Control, Content-Language, Expires, Last-Modified, Pragma, Content-Security-Policy, Upgrade, Connection");
				if (request.getMethod() == HttpMethod.OPTIONS) {
					response.setStatusCode(HttpStatus.OK);
					return Mono.empty();
				}
			}
			return chain.filter(ctx);
		};
	}

}