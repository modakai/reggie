package com.sakura.reggieApi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sakura.reggieApi.common.pojo.Role;
import com.sakura.reggieApi.common.utils.RedisUtils;

import com.sakura.reggieApi.module.sysuser.pojo.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class ReggieAppApplicationTests {

    @Autowired
    RedisUtils redisUtils;

    @Test
    void contextLoads() throws JsonProcessingException {

        /*String obj = redisUtils.get("login:1");
        Employee employee = new ObjectMapper().readValue(obj, Employee.class);
        System.out.println(employee.getPassword());

        System.out.println("-----------------------------------------");
        String password = employee.getPassword().substring(employee.getPassword().indexOf("}") + 1);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode("123abc");
        boolean matches = passwordEncoder.matches("123abc", password);
        System.out.println(matches);*/

        String reqPassword = "$2a$10$jlB/x3GS.aFbXgyqagxR1OhEOql4M1EAE9h0ENd1slMITmIDDJCrC";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean abc123 = encoder.matches("abc123", reqPassword);
        System.out.println(abc123);
    }

}

