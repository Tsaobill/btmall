package com.warush.btmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.warush.btmall.beans.PmsSkuAttrValue;
import com.warush.btmall.beans.PmsSkuImage;
import com.warush.btmall.beans.PmsSkuInfo;
import com.warush.btmall.beans.PmsSkuSaleAttrValue;
import com.warush.btmall.manage.mapper.PmsSkuAttrValueMapper;
import com.warush.btmall.manage.mapper.PmsSkuImageMapper;
import com.warush.btmall.manage.mapper.PmsSkuInfoMapper;
import com.warush.btmall.manage.mapper.PmsSkuSaleAttrValueMpper;
import com.warush.btmall.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @version 1.0
 * @created by bill
 * @on 2019-11-08 20:32
 **/

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    PmsSkuInfoMapper skuInfoMapper;

    @Autowired
    PmsSkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    PmsSkuSaleAttrValueMpper skuSaleAttrValueMpper;

    @Autowired
    PmsSkuImageMapper skuImageMapper;


    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {
        skuInfoMapper.insertSelective (pmsSkuInfo);
        String skuId = pmsSkuInfo.getId ();
        // 插入平台属性关联
        List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList ();
        for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
            pmsSkuAttrValue.setSkuId (skuId);
            skuAttrValueMapper.insertSelective (pmsSkuAttrValue);
        }

        // 插入销售属性关联
        List<PmsSkuSaleAttrValue> saleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList ();
        for (PmsSkuSaleAttrValue saleAttrValue : saleAttrValueList) {
            saleAttrValue.setSkuId (skuId);
            skuSaleAttrValueMpper.insertSelective (saleAttrValue);
        }
        // 插入图片信息
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList ();
        for (PmsSkuImage skuImage : skuImageList) {
            skuImage.setSkuId (skuId);
            skuImageMapper.insertSelective (skuImage);
        }

    }

    @Override
    public PmsSkuInfo getSkuById(String skuId) {

        // 商品对象
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo ();
        pmsSkuInfo.setId (skuId);
        PmsSkuInfo skuInfo = skuInfoMapper.selectOne (pmsSkuInfo);

        // 获取商品图片列表
        PmsSkuImage pmsSkuImage = new PmsSkuImage ();
        pmsSkuImage.setSkuId (skuId);
        List<PmsSkuImage> skuImages = skuImageMapper.select (pmsSkuImage);
        skuInfo.setSkuImageList (skuImages);


        return skuInfo;
    }

    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId) {
        List<PmsSkuInfo> skuInfos = skuInfoMapper.selectSkuSaleAttrValueListBySpu (productId);
        return skuInfos;
    }
}
