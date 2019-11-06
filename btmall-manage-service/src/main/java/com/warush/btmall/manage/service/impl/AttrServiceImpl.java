package com.warush.btmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.warush.btmall.beans.PmsBaseAttrInfo;
import com.warush.btmall.beans.PmsBaseAttrValue;
import com.warush.btmall.beans.PmsBaseSaleAttr;
import com.warush.btmall.manage.mapper.PmsBaseAttrInfoMapper;
import com.warush.btmall.manage.mapper.PmsBaseAttrValueMapper;
import com.warush.btmall.service.AttrService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @version 1.0
 * @created by bill
 * @on 2019-11-07 00:11
 **/
@Service
public class AttrServiceImpl implements AttrService {
    @Autowired
    PmsBaseAttrInfoMapper infoMapper;

    @Autowired
    PmsBaseAttrValueMapper valueMapper;

    @Override
    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id) {
        PmsBaseAttrInfo info = new PmsBaseAttrInfo ();
        info.setCatalog3Id (catalog3Id);
        List<PmsBaseAttrInfo> infoList = infoMapper.select (info);
        return infoList;
    }

    @Override
    public String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {
        int insert = infoMapper.insert (pmsBaseAttrInfo);
        List<PmsBaseAttrValue> values = pmsBaseAttrInfo.getAttrValueList ();
        for (PmsBaseAttrValue e : values) {
            valueMapper.insert (e);
        }
        return "true";
    }

    @Override
    public List<PmsBaseAttrValue> getAttrValueList(String attrId) {
        PmsBaseAttrValue value = new PmsBaseAttrValue ();
        value.setAttrId (attrId);
        List<PmsBaseAttrValue> valueList = valueMapper.select (value);

        return valueList;
    }

    @Override
    public List<PmsBaseSaleAttr> baseSaleAttrList() {
        return null;
    }
}
