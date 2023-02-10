package com.sakura.reggieApi.common.utils;

import com.sakura.reggieApi.common.exception.TokenNotFoundException;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 *  用于返回 token 的工具类
 * @author sakura
 * @className TokenUtils
 * @createTime 2023/2/8
 */
public class TokenUtils {

    /**
     * 两个常量： 过期时间；秘钥
     */
    private static final long EXPIRE = 1000*60*60*24;
    private static final String SECRET = "reggie!";
    /**
     * 存储在redis 中 黑名单的 key
     */
    private static final String TOKEN_BLACK_REDIS_KEY = "token:black";

    private RedisUtils redisUtils;

    private Long expireTime = 1L;
    private String secret = SECRET;


    /**
     * 生成token字符串的方法
     * @param id 用户的 id
     * @param nickname 用户的昵称
     * @return 令牌
     */
    public  String getJwtToken(String id,String nickname){

        JwtBuilder builder = Jwts.builder();
        builder
                .setHeaderParam("type", "JWT")
                .setHeaderParam("alg", "HS256")
                .setSubject("login_user")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (EXPIRE * expireTime)))
                // 设置token 主体
                .claim("id", id)
                .claim("username", nickname)
                .signWith(SignatureAlgorithm.HS256, SECRET);

        return builder.compact();
    }

    /**
     * 判断token是否存在与有效
     * @Param jwtToken
     */
    public  boolean checkToken(String jwtToken){
        if (ObjectUtils.isEmpty(jwtToken)){
            return false;
        }
        try{
            //验证token
            Jwts.parser().setSigningKey(getSecret()).parseClaimsJws(jwtToken);
        }catch (Exception e){
            throw new TokenNotFoundException("token 未找到 或者 已过期");
        }

        if (checkRedisTokenBlack(jwtToken)) {
            throw new TokenNotFoundException("token 已经过期");
        }

        return true;
    }

    /**
     * 判断token是否存在与有效
     * @Param request
     */
    public  boolean checkToken (HttpServletRequest request){
        try {
            String token = request.getHeader("token");
            if (ObjectUtils.isEmpty(token)){
                return false;
            }
            Jwts.parser().setSigningKey(getSecret()).parseClaimsJws(token);
        }catch (Exception e){
            throw new TokenNotFoundException("token 未找到 或者 已过期");
        }


        return true;
    }

    /**
     * 校验 redis 存储的 token 的黑名单
     * @param token 令牌
     * @return
     */
    public boolean checkRedisTokenBlack(String token) {
        return redisUtils.sIsMember(TOKEN_BLACK_REDIS_KEY, token);
    }

    /**
     * 根据token获取会员id
     * @Param request
     */
    public  String getMemberIdByJwtToken(String token){

        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(getSecret()).parseClaimsJws(token);
        Claims body = claimsJws.getBody();
        return (String) body.get("id");
    }

    /**
     * 根据token获取 用户的姓名
     * @Param request
     */
    public  String getMemberUsernameByJwtToken(HttpServletRequest request){
        String token = request.getHeader("token");
        if (ObjectUtils.isEmpty(token)){
            return "";
        }
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(getSecret()).parseClaimsJws(token);
        Claims body = claimsJws.getBody();
        return (String) body.get("username");
    }

    @Autowired
    public void setRedisUtils(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
