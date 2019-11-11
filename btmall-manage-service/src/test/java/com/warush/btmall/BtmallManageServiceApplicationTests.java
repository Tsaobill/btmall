package com.warush.btmall;

import com.warush.btmall.util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

@SpringBootTest
@RunWith (SpringRunner.class)
class BtmallManageServiceApplicationTests {

    @Autowired
    RedisUtil redisUtil;

    @Test
    public void testRedis(){
        Jedis jedis = redisUtil.getJedis ();
        System.out.println (jedis);
    }

    @Test
    void contextLoads() {
    }

}
