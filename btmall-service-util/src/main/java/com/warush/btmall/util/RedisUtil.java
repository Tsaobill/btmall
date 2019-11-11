package com.warush.btmall.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @version 1.0
 * @created by bill
 * @on 2019-11-10 17:21
 **/

public class RedisUtil {
    private JedisPool jedisPool;

    public void initPool(String host, int port, int database) {
        JedisPoolConfig poolConfig = new JedisPoolConfig ();
        poolConfig.setMaxTotal (200);
        poolConfig.setMaxIdle (30);
        poolConfig.setBlockWhenExhausted (true);
        poolConfig.setMaxWaitMillis (200);
        poolConfig.setTestOnBorrow (true);
        jedisPool = new JedisPool (poolConfig, host, port, 20 * 1000);
    }

    public Jedis getJedis() {
        Jedis jedis = jedisPool.getResource ();
        return jedis;
    }
}
