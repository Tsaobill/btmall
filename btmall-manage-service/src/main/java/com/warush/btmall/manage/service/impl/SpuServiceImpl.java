package com.warush.btmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.warush.btmall.beans.PmsProductImage;
import com.warush.btmall.beans.PmsProductInfo;
import com.warush.btmall.beans.PmsProductSaleAttr;
import com.warush.btmall.beans.PmsProductSaleAttrValue;
import com.warush.btmall.manage.mapper.PmsProductImageMapper;
import com.warush.btmall.manage.mapper.PmsProductInfoMapper;
import com.warush.btmall.manage.mapper.PmsProductSaleAttrMapper;
import com.warush.btmall.manage.mapper.PmsProductSaleAttrValueMapper;
import com.warush.btmall.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Autowired
    PmsProductSaleAttrMapper pmsProductSaleAttrMapper;

    @Autowired
    PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;


    @Override
    public List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String productId, String skuId) {
//        PmsProductSaleAttr pmsProductSaleAttr = new PmsProductSaleAttr ();
//        pmsProductSaleAttr.setProductId (productId);
//        List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductSaleAttrMapper.select (pmsProductSaleAttr);
//        for (PmsProductSaleAttr saleAttr : pmsProductSaleAttrs) {
//            String saleAttrId = saleAttr.getSaleAttrId ();
//            PmsProductSaleAttrValue saleAttrValue = new PmsProductSaleAttrValue ();
//            saleAttrValue.setSaleAttrId (saleAttrId);
//            saleAttrValue.setProductId (productId);
//            List<PmsProductSaleAttrValue> saleAttrValues = pmsProductSaleAttrValueMapper.select (saleAttrValue);
//            saleAttr.setSpuSaleAttrValueList (saleAttrValues);
//        }

        List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductSaleAttrMapper.selectSpuSaleAttrListCheckBySku (productId, skuId);

        return pmsProductSaleAttrs;
    }

    @Override
    public List<PmsProductInfo> spuList(String catalog3Id) {
        PmsProductInfo pmsProductInfo = new PmsProductInfo ();
        pmsProductInfo.setCatalog3Id (catalog3Id);
        List<PmsProductInfo> productInfoList = pmsProductInfoMapper.select (pmsProductInfo);
        return productInfoList;
    }

    @Override
    public String saveSpuInfo(PmsProductInfo pmsProductInfo) {
        // 保存商品信息
        pmsProductInfoMapper.insertSelective (pmsProductInfo);

        // 获取商品主键信息
        String productId = pmsProductInfo.getId ();

        // 保存图片信息
        List<PmsProductImage> spuImageList = pmsProductInfo.getSpuImageList ();
        for (PmsProductImage productImage : spuImageList) {
            productImage.setProductId (productId);
            pmsProductImageMapper.insertSelective (productImage);
        }

        // 保存销售属性信息
        List<PmsProductSaleAttr> spuSaleAttrList = pmsProductInfo.getSpuSaleAttrList ();
        for (PmsProductSaleAttr saleAttr : spuSaleAttrList) {
            saleAttr.setProductId (productId);
            pmsProductSaleAttrMapper.insertSelective (saleAttr);


            // 保存销售属性值
            List<PmsProductSaleAttrValue> attrValueList = saleAttr.getSpuSaleAttrValueList ();
            for (PmsProductSaleAttrValue saleAttrValue : attrValueList) {
                saleAttrValue.setProductId (productId);
                pmsProductSaleAttrValueMapper.insertSelective (saleAttrValue);
            }
        }

        //return i + "";
        return "success";
    }

    @Override
    public List<PmsProductImage> spuImageList(String spuId) {

        PmsProductImage pmsProductImage = new PmsProductImage ();
        pmsProductImage.setProductId (spuId);
        List<PmsProductImage> images = pmsProductImageMapper.select (pmsProductImage);
        return images;
    }
}
