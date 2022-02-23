package com.fruits.auth.controller;

import com.fruits.jwt.JWTUtil;
import com.fruits.jwt.LoginUserModel;
import com.fruits.common.Response;
import java.util.Arrays;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final long DURATION_SECONDS = 120;


    @PostMapping("/getToken")
    private Response getToken(@RequestBody Map loginUser) {
        String username = MapUtils.getString(loginUser, "username");
        String password = MapUtils.getString(loginUser, "password");

        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return Response.successBuilder().msg("账号或密码 不能为空！").build();
        }

        if ("admin".equals(username) && "123".equals(password)) {
            LoginUserModel userModel = new LoginUserModel();
            userModel.setUsername(username);
            userModel.setAccount("001");
            userModel.setPermissions(Arrays.asList("fruits:banana:edit", "fruits:banana:delete"));

            String token = JWTUtil.createToken(userModel, DURATION_SECONDS);

            return Response.success(token);
        } else {
            return Response.successBuilder().msg("密码错误！").build();
        }
    }


}
