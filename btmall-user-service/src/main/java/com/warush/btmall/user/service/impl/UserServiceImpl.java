package com.warush.btmall.user.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.warush.btmall.beans.UmsMember;
import com.warush.btmall.beans.UmsMemberReceiveAddress;
import com.warush.btmall.service.UserService;
import com.warush.btmall.user.mapper.UmsMemberReceiveAddressMapper;
import com.warush.btmall.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public List<UmsMember> getAllUser() {
        List<UmsMember> umsMembers = userMapper.selectAll ();
        return umsMembers;
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
}
