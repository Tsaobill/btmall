package com.warush.btmall.service;


import com.warush.btmall.beans.PmsBaseAttrInfo;
import com.warush.btmall.beans.PmsBaseAttrValue;
import com.warush.btmall.beans.PmsBaseSaleAttr;

import java.util.List;


public interface AttrService {

    List<PmsBaseAttrInfo> attrInfoList(String catalog3Id);

    String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo);

    List<PmsBaseAttrValue> getAttrValueList(String attrId);

    List<PmsBaseSaleAttr> baseSaleAttrList();
}
