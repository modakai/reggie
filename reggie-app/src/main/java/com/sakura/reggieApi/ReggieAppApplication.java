package com.sakura.reggieApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableAspectJAutoProxy
@EnableCaching // 开启缓存
public class ReggieAppApplication {


    public static void main(String[] args) {
        SpringApplication.run(ReggieAppApplication.class, args);
    }

}
