package com.fruits.service.interceptor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fruits.common.Response;
import com.fruits.service.annotations.Permission;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            Permission permission = hm.getMethod().getAnnotation(Permission.class);
            if (permission != null) {
                String userPermissions = request.getHeader("User-Permissions");
                List<String> permissions = new ArrayList<>();
                if (StringUtils.isNotBlank(userPermissions)) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    permissions = objectMapper.readValue(userPermissions, new TypeReference<List<String>>() {
                    });
                }

                String value = permission.value();
                if (!permissions.contains(value)) {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        Response noPermission = Response.successBuilder().msg("对不起，您无访问权限！").build();

                        response.setCharacterEncoding("UTF-8");
                        response.setHeader("Content-type", MediaType.APPLICATION_JSON_VALUE);
                        response.getOutputStream().write(objectMapper.writeValueAsString(noPermission).getBytes(StandardCharsets.UTF_8));
                        response.getOutputStream().flush();

                        return false;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


        }

        return true;
    }
}
