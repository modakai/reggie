package com.sakura.reggieApi.common.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sakura.reggieApi.common.pojo.User;
import com.sakura.reggieApi.common.utils.JsonResponseResult;
import com.sakura.reggieApi.common.utils.RedisUtils;
import com.sakura.reggieApi.common.utils.TokenUtils;
import com.sakura.reggieApi.common.pojo.Employee;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author sakura
 * @className JsonAuthenticationFilter
 * @createTime 2023/2/8
 */
@Component
//extends OncePerRequestFilter
public class JsonAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    TokenUtils tokenUtils;
    @Resource
    RedisUtils redisUtils;


    private void returnResponse(HttpServletResponse response, String msg) {
        response.setContentType("application/json;charset=UTF-8");

        try {
            response.getWriter().println(JsonResponseResult.error(msg));
            response.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //1, 获取token
        String token = request.getHeader("token");
        if (ObjectUtils.isEmpty(token)) {
           filterChain.doFilter(request, response);
           return;
        }



        // 2, 校验 token
        try {
            tokenUtils.checkToken(token);
        } catch (Exception e) {
            returnResponse(response, e.getMessage());
            return;
        }

        // 3, 解析token
        String userId = tokenUtils.getMemberIdByJwtToken(token);

        // 4, 从redis 中读取 用户的信息
        String userObj = redisUtils.get("login:" + userId);
        if (userObj == null) {
            returnResponse(response, "用户未登入");
            return;
        }

        Employee employee = null;
        User user = null;
        try {
            employee = new ObjectMapper().readValue(userObj, Employee.class);
        } catch (JsonProcessingException e) {
            user = new ObjectMapper().readValue(userObj, User.class);
        }

        if (ObjectUtils.isEmpty(employee) && ObjectUtils.isEmpty(user)) {
            returnResponse(response, "用户未登入");
            return;
        }

        if (employee != null){
            // 5, 报错到 SecurityContextHolder
            // TODO 从 redis 中获取用户信息并且封装成 认证对象
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(employee, null, employee.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authToken);
        } else {
            // 5, 报错到 SecurityContextHolder
            // TODO 从 redis 中获取用户信息并且封装成 认证对象
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }


        filterChain.doFilter(request, response);
    }
}
