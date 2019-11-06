package com.warush.btmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.warush.btmall.beans.PmsBaseAttrInfo;
import com.warush.btmall.beans.PmsBaseAttrValue;
import com.warush.btmall.service.AttrService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @version 1.0
 * @created by bill
 * @on 2019-11-07 00:08
 **/
@Controller
@CrossOrigin
public class AttrController {
    @Reference
    AttrService attrService;


    @RequestMapping("attrInfoList")
    @ResponseBody
    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id) {
        List<PmsBaseAttrInfo> attrInfos = attrService.attrInfoList (catalog3Id);
        return attrInfos;
    }

    @RequestMapping("getAttrValueList")
    @ResponseBody
    public List<PmsBaseAttrValue> getAttrValueList(String attrId) {
        List<PmsBaseAttrValue> attrInfos = attrService.getAttrValueList (attrId);
        return attrInfos;
    }
    @RequestMapping("saveAttrInfo")
    @ResponseBody
    public String saveAttrInfo(@RequestBody PmsBaseAttrInfo pmsBaseAttrInfo) {
        //PmsBaseAttrInfo
        String res = attrService.saveAttrInfo (pmsBaseAttrInfo);
        return res;
    }

}
