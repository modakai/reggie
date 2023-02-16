package com.sakura.reggieApi.common.config;

import com.sakura.reggieApi.common.filter.JsonAuthenticationFilter;
import com.sakura.reggieApi.common.utils.JsonResponseResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.annotation.Resource;
import java.util.List;

/**
 *  security 配置类
 * @author sakura
 * @className SecurityConfig
 * @createTime 2023/2/8
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Resource
    JsonAuthenticationFilter jsonAuthenticationFilter;

    @Resource
    @Lazy
    AuthenticationManager authenticationManager;

    /**
     * 静态资源放行
     */
    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().mvcMatchers("/image/**");
    }

    /**
     * 解决 跨域
     */
    @Bean
    CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(false);
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource url = new UrlBasedCorsConfigurationSource();
        url.registerCorsConfiguration("/**", configuration);
        return url;
    }


    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /*
        控制 管理端安全的 securityFilterChain
     */
    @Bean
    SecurityFilterChain backendSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests((requests) -> requests
                        .mvcMatchers("/backend/user/login", "/backend/user/logout").permitAll()
                        .antMatchers("/backend/**").authenticated()
                )
                .exceptionHandling((ex) -> ex
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setContentType("application/json;charset=UTF-8");

                            response.getWriter().println(JsonResponseResult.error("用户权限不足"));
                        })
                )
                .csrf().disable();

        // 解决跨域
        http.cors().configurationSource(configurationSource());

        // 设置 过滤器在 username 过滤器之前
        http.addFilterAt(jsonAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

//    @Bean
//    @Order(0)
//    SecurityFilterChain appSecurityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .antMatcher("/app/**")
//                .authorizeRequests((reqs) -> reqs
//                        .mvcMatchers("/app/user/login/**", "/app/user/logout", "/app/user/vc.jpg").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .exceptionHandling((ex) -> ex
//                        .accessDeniedHandler((request, response, accessDeniedException) -> {
//                            response.setContentType("application/json;charset=UTF-8");
//
//                            response.getWriter().println(JsonResponseResult.error("用户权限不足"));
//                        })
//                )
//                .csrf().disable();
//
//        http.cors().configurationSource(configurationSource());
//        http.addFilterBefore(jsonAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }


}
