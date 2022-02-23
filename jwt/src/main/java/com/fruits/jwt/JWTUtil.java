package com.fruits.jwt;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.time.Instant;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;

public class JWTUtil {

    private static final ThreadLocal<LoginUserModel> LOCAL_USER = new ThreadLocal<>();
    private static String secret = DigestUtils.md5DigestAsHex(JWTUtil.class.getName().getBytes());


    public static String createToken(LoginUserModel user, long durationSeconds) {
        //获取 过期日期
        Date expiresAt = Date.from(Instant.now().plusSeconds(durationSeconds));

        //HS256（使用 SHA-256 的 HMAC）是一种对称算法
        //*RS256（使用 SHA-256 的 RSA 签名）是一种非对称算法
        String originalToken = JWT.create().withIssuer(user.getAccount())
                .withClaim("username", user.getUsername())
                .withClaim("permissions", user.getPermissions())
                .withExpiresAt(expiresAt)
                .sign(Algorithm.HMAC256(JWTUtil.secret));

        //使用 过期时间 MD5，给 token 做 AES 加密（一种对称加密算法）
        String expiresTime = String.valueOf(expiresAt.getTime());
        String expiresTimeMD5 = DigestUtils.md5DigestAsHex(expiresTime.getBytes());

        AES aes = SecureUtil.aes(expiresTimeMD5.getBytes());
        String aesToken = aes.encryptBase64(originalToken);

        //将 AES 加密后
        return String.format("%s.%s", aesToken, expiresTime);
    }


    public static boolean verifyToken(String token) {
        if (StringUtils.isBlank(token)) {
            return false;
        }

        try {
            int index = token.lastIndexOf(".");
            if (index <= 0) {
                return false;
            }

            String expiresTime = token.substring(index + 1);
            long expiresTimeLong = Long.parseLong(expiresTime);
            if (expiresTimeLong <= Instant.now().toEpochMilli()) {
                return false;
            }

            String expiresTimeMD5 = DigestUtils.md5DigestAsHex(expiresTime.getBytes());
            AES aes = SecureUtil.aes(expiresTimeMD5.getBytes());
            String originalToken = aes.decryptStr(token.substring(0, index));

            DecodedJWT jwt = JWT.require(Algorithm.HMAC256(JWTUtil.secret)).build().verify(originalToken);
            if (jwt.getExpiresAt().getTime() <= Instant.now().toEpochMilli()) {
                return false;
            } else {
                LoginUserModel userModel = new LoginUserModel();
                userModel.setAccount(jwt.getIssuer());
                userModel.setUsername(jwt.getClaim("username").asString());
                userModel.setPermissions(jwt.getClaim("permissions").asList(String.class));
                LOCAL_USER.set(userModel);
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public static LoginUserModel getLoginUser() {
        return LOCAL_USER.get();
    }


    @Value("${fruits.jwt.secret}")
    public void setSecret(String secret) {
        if (StringUtils.isNotEmpty(secret)) {
            JWTUtil.secret = DigestUtils.md5DigestAsHex(secret.getBytes());
        }
    }

}
