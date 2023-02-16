package com.sakura.reggieApi.module.appuser.controller;

import com.sakura.reggieApi.common.anno.LogAnnotation;
import com.sakura.reggieApi.common.utils.TokenUtils;
import com.sakura.reggieApi.common.pojo.User;
import com.sakura.reggieApi.module.appuser.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * @author sakura
 * @className UserController
 * @createTime 2023/2/14
 */
@RestController
@RequestMapping("/app/user")
public class UserController {

    private static final String HEADER_TOKEN_KEy = TokenUtils.REQUEST_HEADER_TOKEN_KEY;

    @Resource
    UserService userService;

    @LogAnnotation("删除账户")
    @DeleteMapping("")
    public String doDelUser(@RequestHeader(HEADER_TOKEN_KEy) String token) {
        return userService.del(token);
    }

    @LogAnnotation("用户注销登入")
    @PostMapping("/logout")
    public String doDelete(@RequestHeader(HEADER_TOKEN_KEy) String token) {
        return userService.logout(token);
    }

    @LogAnnotation("修改用户信息")
    @PutMapping
    public String doUpdate(@RequestHeader(HEADER_TOKEN_KEy) String token,
                           @RequestBody User user) {
        return userService.updateUser(token, user);
    }

    @LogAnnotation("查询用户信息")
    @GetMapping
    public String doUserDetail(@RequestHeader(HEADER_TOKEN_KEy) String token) {
       return userService.queryDetail(token);
    }

    @LogAnnotation("外卖用户进行登入 / 注册")
    @PostMapping("/login/{code}")
    public String doLogin(@RequestBody User user,
                          @PathVariable("code") String code,
                          HttpServletResponse response) {
        return userService.login(user, code, response);
    }

    @LogAnnotation("获取验证码")
    @GetMapping("/vc.jpg")
    public String getVerifyCode(@RequestBody String phone) throws Exception {
        return userService.generateVCode(phone);
    }

}
