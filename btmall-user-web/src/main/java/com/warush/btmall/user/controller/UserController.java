package com.warush.btmall.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.warush.btmall.beans.UmsMember;
import com.warush.btmall.beans.UmsMemberReceiveAddress;
import com.warush.btmall.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @version 1.0
 * @created by bill
 * @on 2019-11-03 19:11
 **/
@RestController
@ResponseBody
public class UserController {
    @Reference
    UserService userService;

    @RequestMapping("getMemberById")
    @ResponseBody
    public UmsMember getMemberById(String id) {
        UmsMember member = userService.getMemberById (id);
        return member;
    }

    @RequestMapping("getReceiveAddressByMemberId")
    @ResponseBody
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId) {
        List<UmsMemberReceiveAddress> addresses = userService.getReceiveAddressByMemberId (memberId);
        return addresses;
    }


    @GetMapping("getAllUser")
    public List<UmsMember> getAllUser() {
        List<UmsMember> umsMembers = userService.getAllUser ();
        return umsMembers;
    }

    @GetMapping("index")
    public String index() {
        return " hello user !";
    }
}
