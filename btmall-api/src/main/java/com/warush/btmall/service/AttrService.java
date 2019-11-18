package com.warush.btmall.service;


import com.warush.btmall.beans.PmsBaseAttrInfo;
import com.warush.btmall.beans.PmsBaseAttrValue;
import com.warush.btmall.beans.PmsBaseSaleAttr;
import com.warush.btmall.beans.PmsProductSaleAttr;

import java.util.List;
import java.util.Set;


public interface AttrService {

    List<PmsBaseAttrInfo> attrInfoList(String catalog3Id);

    String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo);

    List<PmsBaseAttrValue> getAttrValueList(String attrId);
    List<PmsBaseAttrInfo> getAttrValueListBuValueId(Set<String> valueIdSet);

    List<PmsBaseSaleAttr> baseSaleAttrList();
    List<PmsProductSaleAttr> spuSaleAttrList(String spuId);

}
