package com.sakura.reggieApi.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author sakura
 * @className CaptchaCofig
 * @createTime 2023/2/14
 */
@Configuration
public class CaptchaConfig {

    @Bean
    public Producer producer() {
        Properties properties = new Properties();
        // 1, 验证码的宽度
        properties.setProperty("kaptcha.image.width", "200");
        // 2, 验证码的高度
        properties.setProperty("kaptcha.image.height", "50");
        // 3, 验证码字符串
        properties.setProperty("kaptcha.textproducer.char.string", "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345678923456789");
        // 4, 验证码的长度
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        Config config = new Config(properties);
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}
