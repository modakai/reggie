package com.sakura.reggieApi.module.appuser.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.code.kaptcha.Producer;
import com.sakura.reggieApi.common.exception.UserLoginException;
import com.sakura.reggieApi.common.utils.JsonResponseResult;
import com.sakura.reggieApi.common.utils.RedisUtils;
import com.sakura.reggieApi.common.utils.TokenUtils;
import com.sakura.reggieApi.module.appuser.mapper.UserMapper;
import com.sakura.reggieApi.common.pojo.User;
import com.sakura.reggieApi.module.appuser.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * @author sakura
 * @className UserServiceImpl
 * @createTime 2023/2/14
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService{

    @Resource
    AuthenticationManager authenticationManager;

    @Resource
    UserMapper userMapper;

    @Resource
    RedisUtils redisUtils;
    @Resource
    TokenUtils tokenUtils;

    @Resource
    Producer producer;

    /**
     * ????????????
     * @param user ???????????????
     * @param code ?????????
     */
    @Override
    public String login(User user, String code, HttpServletResponse response) {

        String verifyCodeKey = "captcha:" + user.getPhone();
        String redisVCode = redisUtils.get(verifyCodeKey);
        if (redisVCode == null)
            throw new UserLoginException("??????????????????");

        if (!redisVCode.equalsIgnoreCase(code)) {
            throw new UserLoginException("??????????????????");
        }


        // ?????????????????? ?????????????????????
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<User>()
                .eq("phone", user.getPhone())
                .eq("status", 1);
        User userDB = userMapper.selectOne(userQueryWrapper);

        String token;
        User authReq;
        if (userDB == null) {
            // ??? null ???????????????
            user.setAvatar("/image/default_user_avatar.png");
            userMapper.insert(user);

            // ?????? role
            userMapper.insertRoleUser(user.getId());

            authReq = user;
        } else {
            // ?????? null ????????? phone
            if (!userDB.getPhone().equals(user.getPhone())) {
                throw new UsernameNotFoundException("??????????????????");
            }

            if (!userDB.getStatus())
                throw new UserLoginException("????????????????????????");


            authReq = userDB;
        }

        // ??????
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authReq.getPhone(), redisVCode);

        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        if (authenticate == null)
            throw new AuthenticationServiceException("???????????? ????????????????????????");

        authReq = (User) authenticate.getPrincipal();

        token = tokenUtils.getJwtToken(authReq.getId() + "", authReq.getPhone());

        // ??????????????? redis
        try {
            String obj = new ObjectMapper().writeValueAsString(authReq);
            redisUtils.set("login:" + (authReq.getId()), obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // ???????????????????????? ?????????
        redisUtils.delete(verifyCodeKey);

        Cookie tokenCookie = new Cookie("token", token);
        tokenCookie.setMaxAge(Integer.parseInt(60 * 60 * 24 * tokenUtils.getExpireTime() + ""));
        tokenCookie.setPath("/app/");
        response.addCookie(tokenCookie);

        return JsonResponseResult.successToken(token, user.getPhone());
    }

    /**
     * ???????????????
     */
    @Override
    public String generateVCode(String phone) throws IOException {
        String verifyCodeKey = "captcha:" + phone;
        String verifyCode = redisUtils.get(verifyCodeKey);
        if (verifyCode != null) {
            return JsonResponseResult.error("??????????????????, ??????????????????: " + verifyCode);
        }

        // 1, ????????????????????????
        verifyCode = producer.createText();
        // 2, ?????? redis
        redisUtils.set(verifyCodeKey, verifyCode);
        // ?????????
        redisUtils.expire(verifyCodeKey, 5, TimeUnit.MINUTES);
        // 3, ????????????
        BufferedImage bi = producer.createImage(verifyCode);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bi, "jpg", bos);
        // 4, ?????? Base64 ???????????????
        byte[] base64 = bos.toByteArray();
        Base64.Encoder encoder = Base64.getEncoder();

        return JsonResponseResult.success(base64);
    }

    /**
     * ??????????????????
     * @param token ??????
     */
    @Override
    public String queryDetail(String token) {

        Long uid = Long.valueOf(tokenUtils.getMemberIdByJwtToken(token));

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<User>()
                .eq("id", uid)
                .eq("status", 1);

        User user = userMapper.selectOne(userQueryWrapper);

        if (user == null)
            throw new RuntimeException("???????????????????????????");


        return JsonResponseResult.success(user);
    }

    /**
     * ??????????????????
     * @param token ??????
     * @param user ??????
     */
    @Override
    public String updateUser(String token, User user) {
        Long uid = Long.valueOf(tokenUtils.getMemberIdByJwtToken(token));

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<User>()
                .eq("id", uid)
                .eq("status", 1);

        User userDB = userMapper.selectOne(userQueryWrapper);

        if (userDB == null)
            throw new RuntimeException("???????????????????????????");

        UpdateWrapper<User> updateWrapper = new UpdateWrapper<User>()
                .eq("id", userDB.getId());

        int update = userMapper.update(user, updateWrapper);
        if (update <= 0)
            throw new RuntimeException("????????????");

        return JsonResponseResult.defaultSuccess("????????????");
    }

    @Resource
    HttpServletResponse response;
    /**
     * ??????????????????
     * @param token token
     */
    @Override
    public String logout(String token) {
        Long uid = Long.valueOf(tokenUtils.getMemberIdByJwtToken(token));

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<User>()
                .eq("id", uid)
                .eq("status", 1);

        User user = userMapper.selectOne(userQueryWrapper);

        // ?????? redis ????????????
        redisUtils.delete("login:" + user.getId());
        // ??? ????????????????????????
        redisUtils.sAdd("token:black", token);

        Cookie cookie = new Cookie("token", token);
        cookie.setPath("/app/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return JsonResponseResult.defaultSuccess("????????????");
    }

    /**
     * ????????????
     * @param token ??????
     */
    @Override
    public String del(String token) {
        Long uid = Long.valueOf(tokenUtils.getMemberIdByJwtToken(token));

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<User>()
                .eq("id", uid)
                .eq("status", 1);

        User user = userMapper.selectOne(userQueryWrapper);

        if (user == null)
            throw new UsernameNotFoundException("????????????????????????");

        UpdateWrapper<User> updateWrapper = new UpdateWrapper<User>()
                .eq("id", uid)
                .eq("status", 1)
                .set("status", 0);

        int update = userMapper.update(null, updateWrapper);
        if (update <= 0)
            throw new RuntimeException("????????????");


        // ?????? redis ????????????
        redisUtils.delete("login:" + user.getId());
        // ??? ????????????????????????
        redisUtils.sAdd("token:black", token);

        Cookie cookie = new Cookie("token", token);
        cookie.setPath("/app/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);


        return JsonResponseResult.defaultSuccess("????????????");
    }
}
