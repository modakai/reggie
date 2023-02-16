package com.sakura.reggieApi;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sakura.reggieApi.common.utils.RedisUtils;

import com.sakura.reggieApi.module.category.mapper.CategoryMapper;
import com.sakura.reggieApi.module.dishmanagement.pojo.Dish;
import com.sakura.reggieApi.module.order.mapper.OrdersMapper;
import com.sakura.reggieApi.module.order.pojo.Orders;
import com.sakura.reggieApi.module.setmealmanagement.pojo.SetmealDish;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.format.datetime.standard.DateTimeFormatterFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@SpringBootTest
class ReggieAppApplicationTests {

    @Autowired
    RedisUtils redisUtils;

    @Resource
    CategoryMapper categoryMapper;
    @Resource
    OrdersMapper ordersMapper;
    @Test
    void contextLoads() throws JsonProcessingException, ParseException {


    }

}

