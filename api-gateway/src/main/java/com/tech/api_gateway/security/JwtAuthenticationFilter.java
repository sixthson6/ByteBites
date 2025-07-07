package com.tech.api_gateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

@Override
public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    String token = resolveToken(exchange.getRequest());

    if (StringUtils.hasText(token) && this.jwtTokenProvider.validateToken(token)) {
        return Mono.just(this.jwtTokenProvider.getUsername(token))
                .flatMap(username ->
                        Mono.just(Jwts.parserBuilder().setSigningKey(jwtTokenProvider.key()).build().parseClaimsJws(token).getBody())
                                .map(claims -> {
                                    List<String> roles = (List<String>) claims.get("roles");
                                    return new UsernamePasswordAuthenticationToken(
                                            username,
                                            null,
                                            roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                                    );
                                })
                )
                .flatMap(authentication -> chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication)));
    }
    return chain.filter(exchange);
}

    private String resolveToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
