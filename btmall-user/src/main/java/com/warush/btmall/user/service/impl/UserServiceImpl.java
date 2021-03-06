package com.warush.btmall.user.service.impl;


import com.warush.btmall.beans.UmsMember;
import com.warush.btmall.beans.UmsMemberReceiveAddress;
import com.warush.btmall.service.UserService;
import com.warush.btmall.user.mapper.UmsMemberReceiveAddressMapper;
import com.warush.btmall.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @version 1.0
 * @created by bill
 * @on 2019-11-03 19:10
 **/
@Service
public class UserServiceImpl {
    @Autowired
    UserMapper userMapper;

    @Autowired
    UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;

    public List<UmsMember> getAllUser() {
        List<UmsMember> umsMembers = userMapper.selectAll ();
        return umsMembers;
    }


    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String umsMemberId) {
        Example e = new Example (UmsMemberReceiveAddress.class);
        e.createCriteria ().andEqualTo ("memberId", umsMemberId);
        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberReceiveAddressMapper.selectByExample (e);
        return umsMemberReceiveAddresses;
    }

    public UmsMember getMemberById(String id) {
        Example e = new Example (UmsMember.class);
        e.createCriteria ().andEqualTo ("id", id);
        return userMapper.selectOneByExample (e);
    }
}
