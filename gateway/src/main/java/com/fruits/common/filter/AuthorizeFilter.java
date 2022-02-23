package com.fruits.common.filter;

import static com.fruits.common.ResponseCodeEnum.INVALID_TOKEN;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fruits.jwt.JWTUtil;
import com.fruits.common.Response;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {

    protected static Logger log = LoggerFactory.getLogger(AuthorizeFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        //token 请求放行
        String path = request.getPath().toString();
        if (path.contains("getToke")) {
            return chain.filter(exchange);
        }

        String token = request.getHeaders().getFirst("token");

        if (StringUtils.isBlank(token)) {
            return getVoidMono(response, Response.successBuilder().msg("token 不能为空！").build());
        }

        if (!JWTUtil.verifyToken(token)) {
            return getVoidMono(response, Response.successBuilder().msg(INVALID_TOKEN.getDescription()).build());
        }

        String permissions = null;
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> list = JWTUtil.getLoginUser().getPermissions();
        try {
            if (CollectionUtils.isNotEmpty(list)) {
                permissions = objectMapper.writeValueAsString(list);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.error("AuthorizeFilter::filter error：", e);
        }
        ServerHttpRequest serverHttpRequest = request.mutate().header("User-Permissions", permissions).build();

        return chain.filter(exchange.mutate().request(serverHttpRequest).build());
    }

    private Mono<Void> getVoidMono(ServerHttpResponse serverHttpResponse, Response response) {
        serverHttpResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        DataBuffer dataBuffer = null;
        try {
            dataBuffer = serverHttpResponse.bufferFactory().wrap(mapper.writeValueAsBytes(response));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return serverHttpResponse.writeWith(Flux.just(dataBuffer));
    }


    @Override
    public int getOrder() {
        return 0;
    }
}
