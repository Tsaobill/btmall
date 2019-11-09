package com.warush.btmall.service;


import com.warush.btmall.beans.PmsProductImage;
import com.warush.btmall.beans.PmsProductInfo;
import com.warush.btmall.beans.PmsProductSaleAttr;

import java.util.List;

public interface SpuService {
    List<PmsProductInfo> spuList(String catalog3Id);

    List<PmsProductImage> spuImageList(String spuId);

    String saveSpuInfo(PmsProductInfo pmsProductInfo);

    List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String productId, String skuId);
}