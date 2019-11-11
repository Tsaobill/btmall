package com.warush.btmall.redissontest.controller;

import com.warush.btmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;

/**
 * @version 1.0
 * @created by bill
 * @on 2019-11-11 00:18
 **/

@Controller
public class RedissonTestController {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    RedissonClient redissonClient;

    @RequestMapping("redissonTest")
    @ResponseBody
    public String testRedisson() {
        // <== redis的并发安全问题
//        Jedis jedis = redisUtil.getJedis ();
//        String v = jedis.get ("k");
//        if (StringUtils.isBlank (v)) {
//            v = "1";
//        }
//        System.out.println (v);
//        jedis.set ("k", Integer.parseInt (v) + 1 + "");
//
//        jedis.close ();
        // ==>


        // <== 使用jedisson解决
        RLock lock = redissonClient.getLock ("lock");
        Jedis jedis = redisUtil.getJedis ();
        lock.lock ();
        try {
            String v = jedis.get ("k");
            if (StringUtils.isBlank (v)) {
                v = "1";
            }
            System.out.println (v);
            jedis.set ("k", Integer.parseInt (v) + 1 + "");
        } finally {
            lock.unlock ();
            jedis.close ();
        }

        // ==>

        return "redissonTest";

    }

}
