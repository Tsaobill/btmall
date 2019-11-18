package com.warush.btmall.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.warush.btmall.beans.PmsSkuInfo;
import com.warush.btmall.service.SkuService;

import java.util.List;

/**
 * @version 1.0
 * @created by bill
 * @on 2019-11-16 17:48
 **/
@Service
public class asdf implements SkuService {
    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {

    }

    @Override
    public PmsSkuInfo getSkuById(String skuId) {
        return null;
    }

    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId) {
        return null;
    }

    @Override
    public List<PmsSkuInfo> getAllSku(String catalog3Id) {
        return null;
    }
}
