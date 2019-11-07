package com.warush.btmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.warush.btmall.beans.PmsProductImage;
import com.warush.btmall.beans.PmsProductInfo;
import com.warush.btmall.manage.mapper.PmsProductImageMapper;
import com.warush.btmall.manage.mapper.PmsProductInfoMapper;
import com.warush.btmall.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @version 1.0
 * @created by bill
 * @on 2019-11-07 17:47
 **/
@Service
public class SpuServiceImpl implements SpuService {
    @Autowired
    PmsProductInfoMapper pmsProductInfoMapper;

    @Autowired
    PmsProductImageMapper pmsProductImageMapper;


    @Override
    public List<PmsProductInfo> spuList(String catalog3Id) {
        PmsProductInfo pmsProductInfo = new PmsProductInfo ();
        pmsProductInfo.setCatalog3Id (catalog3Id);
        List<PmsProductInfo> productInfoList = pmsProductInfoMapper.select (pmsProductInfo);
        return productInfoList;
    }

    @Override
    public String saveSpuInfo(PmsProductInfo pmsProductInfo) {
        Example e = new Example (PmsProductInfo.class);
        e.createCriteria ().andEqualTo ("id", pmsProductInfo.getId ());
        int i = pmsProductInfoMapper.updateByExampleSelective (pmsProductInfo, e);
        return i + "";
    }

    @Override
    public List<PmsProductImage> spuImageList(String spuId) {

        PmsProductImage pmsProductImage = new PmsProductImage ();
        pmsProductImage.setId (spuId);
        List<PmsProductImage> images = pmsProductImageMapper.select (pmsProductImage);
        return images;
    }
}
