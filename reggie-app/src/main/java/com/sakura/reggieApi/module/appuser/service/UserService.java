package com.sakura.reggieApi.module.appuser.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sakura.reggieApi.common.pojo.User;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author sakura
 * @className UserService
 * @createTime 2023/2/14
 */
public interface UserService extends IService<User> {

    String login(User user, String code, HttpServletResponse response);

    String generateVCode(String phone) throws IOException;

    String queryDetail(String token);

    String updateUser(String token, User user);

    String logout(String token);

    String del(String token);
}
