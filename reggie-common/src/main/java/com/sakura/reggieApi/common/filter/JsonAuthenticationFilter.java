package com.sakura.reggieApi.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sakura.reggieApi.common.utils.JsonResponseResult;
import com.sakura.reggieApi.common.utils.RedisUtils;
import com.sakura.reggieApi.common.utils.TokenUtils;
import com.sakura.reggieApi.common.pojo.Employee;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author sakura
 * @className JsonAuthenticationFilter
 * @createTime 2023/2/8
 */
@Component
public class JsonAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    TokenUtils tokenUtils;
    @Resource
    RedisUtils redisUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1, 获取token
        String token = request.getHeader("token");
        if (ObjectUtils.isEmpty(token)){
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
        if (userObj == null){
            returnResponse(response, "用户未登入");
            return;
        }

        Employee employee = new ObjectMapper().readValue(userObj, Employee.class);

        if (ObjectUtils.isEmpty(employee)) {
            returnResponse(response, "用户未登入");
            return;
        }

        // 5, 报错到 SecurityContextHolder
        // TODO 从 redis 中获取用户信息并且封装成 认证对象
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(employee, null, employee.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }


    private void returnResponse(HttpServletResponse response, String msg) {
        response.setContentType("application/json;charset=UTF-8");

        try {
            response.getWriter().println(JsonResponseResult.error(msg));
            response.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
