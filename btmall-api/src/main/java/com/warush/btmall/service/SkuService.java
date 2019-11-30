package com.warush.btmall.service;


import com.warush.btmall.beans.PmsSkuInfo;

import java.math.BigDecimal;
import java.util.List;

public interface SkuService {
    void saveSkuInfo(PmsSkuInfo pmsSkuInfo);

    PmsSkuInfo getSkuById(String skuId);

    List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId);
    List<PmsSkuInfo> getAllSku(String catalog3Id);


    boolean checkPrice(String productSkuId, BigDecimal price);
}
