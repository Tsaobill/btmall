package com.warush.btmall.conf;

import com.warush.btmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @version 1.0
 * @created by bill
 * @on 2019-11-10 17:27
 **/
@Configuration
public class RedisConfig {
    // 读取配置文件中的配置
    @Value("${spring.redis.host:disabled}")
    private String host;

    @Value("${spring.redis.port:0}")
    private int port;

    @Value("${spring.redis.database:0}")
    private int database;

    @Bean
    public RedisUtil getRedisUtil() {
        if (host.equals ("disabled")) {
            return null;
        }
        RedisUtil redisUtil = new RedisUtil ();
        redisUtil.initPool (host, port, database);
        return redisUtil;
    }
}
