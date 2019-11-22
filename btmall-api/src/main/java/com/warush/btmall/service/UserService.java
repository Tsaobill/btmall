package com.warush.btmall.service;

import com.warush.btmall.beans.UmsMember;
import com.warush.btmall.beans.UmsMemberReceiveAddress;

import java.util.List;

public interface UserService {

    List<UmsMember> getAllUser();

    List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId);
    UmsMember getMemberById(String id);

    UmsMember login(UmsMember umsMember);

    void addUserToken(String token, String memberId);
}
