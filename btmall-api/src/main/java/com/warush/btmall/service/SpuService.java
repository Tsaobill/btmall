package com.warush.btmall.service;


import com.warush.btmall.beans.PmsProductImage;
import com.warush.btmall.beans.PmsProductInfo;

import java.util.List;

public interface SpuService {
    List<PmsProductInfo> spuList(String catalog3Id);

    List<PmsProductImage> spuImageList(String spuId);

    String saveSpuInfo(PmsProductInfo pmsProductInfo);
}