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
     * 登入方法
     * @param user 登入的用户
     * @param code 验证码
     */
    @Override
    public String login(User user, String code, HttpServletResponse response) {

        String verifyCodeKey = "captcha:" + user.getPhone();
        String redisVCode = redisUtils.get(verifyCodeKey);
        if (redisVCode == null)
            throw new UserLoginException("未获取验证码");

        if (!redisVCode.equalsIgnoreCase(code)) {
            throw new UserLoginException("验证码不正确");
        }


        // 判断用户是否 存在与数据库中
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<User>()
                .eq("phone", user.getPhone())
                .eq("status", 1);
        User userDB = userMapper.selectOne(userQueryWrapper);

        String token;
        User authReq;
        if (userDB == null) {
            // 为 null 则进行注册
            user.setAvatar("/image/default_user_avatar.png");
            userMapper.insert(user);

            // 添加 role
            userMapper.insertRoleUser(user.getId());

            authReq = user;
        } else {
            // 不为 null 就比较 phone
            if (!userDB.getPhone().equals(user.getPhone())) {
                throw new UsernameNotFoundException("用户名未找到");
            }

            if (!userDB.getStatus())
                throw new UserLoginException("该账户已经被禁用");


            authReq = userDB;
        }

        // 认证
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authReq.getPhone(), redisVCode);

        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        if (authenticate == null)
            throw new AuthenticationServiceException("登入失败 电话号码输入错误");

        authReq = (User) authenticate.getPrincipal();

        token = tokenUtils.getJwtToken(authReq.getId() + "", authReq.getPhone());

        // 保存信息到 redis
        try {
            String obj = new ObjectMapper().writeValueAsString(authReq);
            redisUtils.set("login:" + (authReq.getId()), obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 正确则删除对应的 验证码
        redisUtils.delete(verifyCodeKey);

        Cookie tokenCookie = new Cookie("token", token);
        tokenCookie.setMaxAge(Integer.parseInt(60 * 60 * 24 * tokenUtils.getExpireTime() + ""));
        tokenCookie.setPath("/app/");
        response.addCookie(tokenCookie);

        return JsonResponseResult.successToken(token, user.getPhone());
    }

    /**
     * 生成验证码
     */
    @Override
    public String generateVCode(String phone) throws IOException {
        String verifyCodeKey = "captcha:" + phone;
        String verifyCode = redisUtils.get(verifyCodeKey);
        if (verifyCode != null) {
            return JsonResponseResult.error("验证码未过期, 无法重新获取: " + verifyCode);
        }

        // 1, 生产验证码字符串
        verifyCode = producer.createText();
        // 2, 存入 redis
        redisUtils.set(verifyCodeKey, verifyCode);
        // 五分钟
        redisUtils.expire(verifyCodeKey, 5, TimeUnit.MINUTES);
        // 3, 生成图片
        BufferedImage bi = producer.createImage(verifyCode);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bi, "jpg", bos);
        // 4, 转为 Base64 发送给前端
        byte[] base64 = bos.toByteArray();
        Base64.Encoder encoder = Base64.getEncoder();

        return JsonResponseResult.success(base64);
    }

    /**
     * 查询用户详情
     * @param token 令牌
     */
    @Override
    public String queryDetail(String token) {

        Long uid = Long.valueOf(tokenUtils.getMemberIdByJwtToken(token));

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<User>()
                .eq("id", uid)
                .eq("status", 1);

        User user = userMapper.selectOne(userQueryWrapper);

        if (user == null)
            throw new RuntimeException("查询不到对应的用户");


        return JsonResponseResult.success(user);
    }

    /**
     * 修改用户信息
     * @param token 令牌
     * @param user 用户
     */
    @Override
    public String updateUser(String token, User user) {
        Long uid = Long.valueOf(tokenUtils.getMemberIdByJwtToken(token));

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<User>()
                .eq("id", uid)
                .eq("status", 1);

        User userDB = userMapper.selectOne(userQueryWrapper);

        if (userDB == null)
            throw new RuntimeException("未查询到对应的用户");

        UpdateWrapper<User> updateWrapper = new UpdateWrapper<User>()
                .eq("id", userDB.getId());

        int update = userMapper.update(user, updateWrapper);
        if (update <= 0)
            throw new RuntimeException("未知异常");

        return JsonResponseResult.defaultSuccess("修改成功");
    }

    @Resource
    HttpServletResponse response;
    /**
     * 用户注销登入
     * @param token token
     */
    @Override
    public String logout(String token) {
        Long uid = Long.valueOf(tokenUtils.getMemberIdByJwtToken(token));

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<User>()
                .eq("id", uid)
                .eq("status", 1);

        User user = userMapper.selectOne(userQueryWrapper);

        // 删除 redis 中的数据
        redisUtils.delete("login:" + user.getId());
        // 向 黑名单中添加数据
        redisUtils.sAdd("token:black", token);

        Cookie cookie = new Cookie("token", token);
        cookie.setPath("/app/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return JsonResponseResult.defaultSuccess("注销成功");
    }

    /**
     * 删除用户
     * @param token 令牌
     */
    @Override
    public String del(String token) {
        Long uid = Long.valueOf(tokenUtils.getMemberIdByJwtToken(token));

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<User>()
                .eq("id", uid)
                .eq("status", 1);

        User user = userMapper.selectOne(userQueryWrapper);

        if (user == null)
            throw new UsernameNotFoundException("未找到对应的账户");

        UpdateWrapper<User> updateWrapper = new UpdateWrapper<User>()
                .eq("id", uid)
                .eq("status", 1)
                .set("status", 0);

        int update = userMapper.update(null, updateWrapper);
        if (update <= 0)
            throw new RuntimeException("服务异常");


        // 删除 redis 中的数据
        redisUtils.delete("login:" + user.getId());
        // 向 黑名单中添加数据
        redisUtils.sAdd("token:black", token);

        Cookie cookie = new Cookie("token", token);
        cookie.setPath("/app/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);


        return JsonResponseResult.defaultSuccess("删除成功");
    }
}
