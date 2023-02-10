package com.sakura.reggieApi.common.config;

import com.sakura.reggieApi.common.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *  令牌的控制类
 * @author sakura
 * @className TokenConfig
 * @createTime 2023/2/8
 */
@Configuration
public class TokenConfig {

    @Value("${token.expireTime:#{null}}")
    private Long expireTime;
    @Value("${token.secret:#{null}}")
    private String secret;

    @Bean
    TokenUtils tokenUtils() {
        TokenUtils tokenUtils = new TokenUtils();

        if (expireTime != null)
            tokenUtils.setExpireTime(expireTime);

        if (secret != null)
            tokenUtils.setSecret(secret);

        return tokenUtils;
    }
}
