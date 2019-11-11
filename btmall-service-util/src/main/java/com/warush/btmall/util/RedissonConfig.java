package com.warush.btmall.util;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @version 1.0
 * @created by bill
 * @on 2019-11-10 23:56
 **/
@Configuration
public class RedissonConfig {
    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private String port;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config ();
        config.useSingleServer ().setAddress ("redis://" + host + ":" + port);
        RedissonClient redisson = Redisson.create (config);
        return redisson;
    }
}
