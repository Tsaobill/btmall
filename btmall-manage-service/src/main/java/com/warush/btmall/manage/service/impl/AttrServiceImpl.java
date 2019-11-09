package com.warush.btmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.warush.btmall.beans.*;
import com.warush.btmall.manage.mapper.*;
import com.warush.btmall.service.AttrService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

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

    @Autowired
    PmsBaseSaleAttrMapper baseSaleAttrMapper;

    @Autowired
    PmsProductSaleAttrMapper productSaleAttrMapper;

    @Autowired
    PmsBaseAttrInfoMapper pmsBaseAttrInfoMapper;

    @Autowired
    PmsBaseAttrValueMapper pmsBaseAttrValueMapper;

    @Autowired
    PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;

    @Override
    public List<PmsBaseAttrValue> getAttrValueList(String attrId) {
        PmsBaseAttrValue value = new PmsBaseAttrValue ();
        value.setAttrId (attrId);
        List<PmsBaseAttrValue> valueList = valueMapper.select (value);

        return valueList;
    }

    @Override
    public List<PmsBaseSaleAttr> baseSaleAttrList() {

        List<PmsBaseSaleAttr> baseSaleAttrs = baseSaleAttrMapper.selectAll ();
        return baseSaleAttrs;
    }

    @Override
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId) {
        PmsProductSaleAttr pmsProductSaleAttr = new PmsProductSaleAttr ();
        pmsProductSaleAttr.setProductId (spuId);
        List<PmsProductSaleAttr> attrList = productSaleAttrMapper.select (pmsProductSaleAttr);
        for (PmsProductSaleAttr saleAttr : attrList) {
            PmsProductSaleAttrValue saleAttrValue = new PmsProductSaleAttrValue ();
            saleAttrValue.setProductId (spuId);
            saleAttrValue.setSaleAttrId (saleAttr.getSaleAttrId ());

            List<PmsProductSaleAttrValue> attrValues = pmsProductSaleAttrValueMapper.select (saleAttrValue);
            saleAttr.setSpuSaleAttrValueList (attrValues);
        }
        return attrList;
    }

    @Override
    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id) {
        PmsBaseAttrInfo info = new PmsBaseAttrInfo ();
        info.setCatalog3Id (catalog3Id);
        List<PmsBaseAttrInfo> infoList = infoMapper.select (info);
        for (PmsBaseAttrInfo baseAttrInfo : infoList) {
            PmsBaseAttrValue attrValue = new PmsBaseAttrValue ();
            attrValue.setAttrId (baseAttrInfo.getId ());
            baseAttrInfo.setAttrValueList (pmsBaseAttrValueMapper.select (attrValue));
        }
        return infoList;
    }

    // 保存和修改属性
    @Override
    public String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {

        if (StringUtils.isBlank (pmsBaseAttrInfo.getId ())) {

            int insert = infoMapper.insertSelective (pmsBaseAttrInfo);
            List<PmsBaseAttrValue> values = pmsBaseAttrInfo.getAttrValueList ();
            for (PmsBaseAttrValue e : values) {
                e.setAttrId (pmsBaseAttrInfo.getId ());
                valueMapper.insertSelective (e);
            }
        } else {
            // id 非空，修改

            // 属性
            Example e = new Example (PmsBaseAttrInfo.class);
            e.createCriteria ().andEqualTo ("id", pmsBaseAttrInfo.getId ());
            infoMapper.updateByExampleSelective (pmsBaseAttrInfo, e);

            // 属性值
            List<PmsBaseAttrValue> values = pmsBaseAttrInfo.getAttrValueList ();

            // 根据属性id，删除所有该属性id的属性值
            PmsBaseAttrValue value = new PmsBaseAttrValue ();
            value.setAttrId (pmsBaseAttrInfo.getId ());
            valueMapper.delete (value);

            // 再将新的属性值插入
            for (PmsBaseAttrValue ele : values) {
                ele.setAttrId (pmsBaseAttrInfo.getId ());
                valueMapper.insertSelective (ele);
            }
        }
        return "success";
    }


}
