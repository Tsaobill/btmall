package com.warush.btmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.warush.btmall.beans.UmsMember;
import com.warush.btmall.service.UserService;
import com.warush.btmall.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.0
 * @created by bill
 * @on 2019-11-21 21:28
 **/
@Controller
public class PassportController {

    @Reference
    UserService userService;

    @RequestMapping("verify")
    @ResponseBody
    public String verify(String token, String currentIp) {

        // 通过jwt校验
        Map<String, String> map = new HashMap<> ();

        Map<String, Object> decode = JwtUtil.decode (token, "2019btmall1004", currentIp);
        if (decode == null) {
            map.put ("status", "fail");
        } else {
            map.put ("status", "success");
            map.put ("memberId", (String) decode.get ("memberId"));
            map.put ("nickname", (String) decode.get ("nickname"));
        }
        return JSON.toJSONString (map);
    }

    @RequestMapping("login")
    @ResponseBody
    public String login(UmsMember umsMember, HttpServletRequest request) {
        String token = "";

        // 调用用户服务验证用户名和密码
        UmsMember umsMemberLogin = userService.login (umsMember);

        if (umsMemberLogin != null) {
            // 登录成功
            // 用jwt生成token
            String memberId = umsMemberLogin.getId ();
            String nickname = umsMemberLogin.getNickname ();
            Map<String, Object> userMap = new HashMap<> ();
            userMap.put ("memberId", memberId);
            userMap.put ("nickname", nickname);

            String ip = request.getHeader ("x-forwarded-for");
            if (StringUtils.isNotBlank (ip)) {
                ip = request.getRemoteAddr ();
                if (StringUtils.isNotBlank (ip)) {
                    ip = "127.0.0.1";
                }
            }
            token = JwtUtil.encode ("2019btmall1004", userMap, ip);

            // 将token存入redis
            userService.addUserToken (token, memberId);

        } else {
            // 登录失败
            token = "fail";
        }

        return token;
    }

    @RequestMapping("index")
    public String index(String ReturnUrl, ModelMap modelMap) {

        modelMap.put ("ReturnUrl", ReturnUrl);
        return "index";
    }
}
