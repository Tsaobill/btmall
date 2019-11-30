package com.warush.btmall.user.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.warush.btmall.beans.UmsMember;
import com.warush.btmall.beans.UmsMemberReceiveAddress;
import com.warush.btmall.service.UserService;
import com.warush.btmall.user.mapper.UmsMemberReceiveAddressMapper;
import com.warush.btmall.user.mapper.UserMapper;
import com.warush.btmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @version 1.0
 * @created by bill
 * @on 2019-11-03 19:10
 **/
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;

    @Autowired
    UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public List<UmsMember> getAllUser() {
        List<UmsMember> umsMembers = userMapper.selectAll ();
        return umsMembers;
    }

    @Override
    public UmsMemberReceiveAddress getReceiveAddressById(String receiveAddressId) {
        UmsMemberReceiveAddress ad = new UmsMemberReceiveAddress ();
        ad.setId (receiveAddressId);
        UmsMemberReceiveAddress receiveAddress = umsMemberReceiveAddressMapper.selectOne (ad);
        return receiveAddress;
    }

    @Override
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String umsMemberId) {
        Example e = new Example (UmsMemberReceiveAddress.class);
        e.createCriteria ().andEqualTo ("memberId", umsMemberId);
        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberReceiveAddressMapper.selectByExample (e);
        return umsMemberReceiveAddresses;
    }

    @Override
    public UmsMember getMemberById(String id) {
        Example e = new Example (UmsMember.class);
        e.createCriteria ().andEqualTo ("id", id);
        return userMapper.selectOneByExample (e);
    }

    @Override
    public UmsMember login(UmsMember umsMember) {
        Jedis jedis = null;
        UmsMember memberLogin = null;
        try {
            jedis = redisUtil.getJedis ();

            String key = "user:" + umsMember.getPassword () + ":info";

            if (jedis != null) {
                String umsMemberStr = jedis.get (key);

                if (StringUtils.isNotBlank (umsMemberStr)) {
                    // 密码正确
                    memberLogin = JSON.parseObject (umsMemberStr, UmsMember.class);
                    return memberLogin;
                }
            }
            // 密码错误或者缓存中不存在
            // 去db查询
            UmsMember memberFromDb = loginFromDb (umsMember);
            if (memberFromDb != null) {
                jedis.setex (key, 60 * 60 * 24, JSON.toJSONString (memberFromDb));
            }
            return memberFromDb;

        } finally {
            jedis.close ();
        }
    }

    @Override
    public void addUserToken(String token, String memberId) {
        Jedis jedis = redisUtil.getJedis ();
        jedis.setex ("user:" + memberId + ":token", 60 * 60 * 2, token);
        jedis.close ();
    }

    private UmsMember loginFromDb(UmsMember umsMember) {

        List<UmsMember> umsMembers = userMapper.select (umsMember);
        if (umsMembers != null) {
            return umsMembers.get (0);
        }
        return null;
    }

}
