package com.metrocarpool.gateway.security;

// package com.metrocarpool.gateway.filter;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class AuthorizationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    // A list of URI patterns that should NOT be secured (e.g., /auth/login, /auth/register)
    public static final List<String> openApiEndpoints = List.of(
            "/auth/register",
            "/auth/login"
    );

    @Autowired
    public AuthorizationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        final String path = request.getURI().getPath();

        // 1. Check if the path requires authentication
        if (openApiEndpoints.stream().noneMatch(path::contains)) {

            // 2. Check for "Authorization" header
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return this.onError(exchange, "Authorization header is missing", HttpStatus.UNAUTHORIZED);
            }

            final String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            assert authHeader != null;
            String token = authHeader.substring(7); // Assuming "Bearer <token>"

            try {
                // 3. Validate the token
                jwtUtil.validateToken(token);

                // 4. Mutate the request (Add user info to headers)
                Claims claims = jwtUtil.getClaims(token);
                ServerHttpRequest mutatedRequest = request.mutate()
                        .header("X-User-ID", claims.getSubject())
                        .header("X-User-Roles", claims.get("roles", String.class))
                        .build();

                return chain.filter(exchange.mutate().request(mutatedRequest).build());

            } catch (Exception e) {
                return this.onError(exchange, "JWT Token is invalid or expired: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        }

        // Pass to the next filter in the chain (for public endpoints)
        return chain.filter(exchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        // Write the error message to the response body
        byte[] bytes = err.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        // Run this filter before any routing filters (e.g., NettyWriteResponseFilter = -1)
        return -2;
    }
}
