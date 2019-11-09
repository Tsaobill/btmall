package com.warush.btmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.warush.btmall.beans.PmsProductSaleAttr;
import com.warush.btmall.beans.PmsSkuInfo;
import com.warush.btmall.beans.PmsSkuSaleAttrValue;
import com.warush.btmall.service.SkuService;
import com.warush.btmall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;

/**
 * @version 1.0
 * @created by bill
 * @on 2019-11-08 23:34
 **/
@Controller
//@CrossOrigin
public class ItemController {

    @Reference
    SkuService skuService;

    @Reference
    SpuService spuService;

    @RequestMapping("{skuId}.html")
    public String item(@PathVariable String skuId, ModelMap map) {

        //1. 获得sku对象
        PmsSkuInfo skuInfo = skuService.getSkuById (skuId);
        map.put ("skuInfo", skuInfo);

        //2. 获取sku对应的spu的销售属性列表
        List<PmsProductSaleAttr> pmsProductSaleAttrs = spuService.spuSaleAttrListCheckBySku (skuInfo.getProductId (), skuInfo.getId ());
        map.put ("spuSaleAttrListCheckBySku", pmsProductSaleAttrs);

        //3. 查询该sku所属的spu下面其他sku集合，并生成哈希表
        List<PmsSkuInfo> pmsSkuInfos = skuService.getSkuSaleAttrValueListBySpu (skuInfo.getProductId ());
        HashMap<String, String> skuValuesHashMap = new HashMap<> ();
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {
            // 获取skuInfo的skuId作为哈希表的值
            String v = pmsSkuInfo.getId ();
            String k = "";
            List<PmsSkuSaleAttrValue> attrValueList = pmsSkuInfo.getSkuSaleAttrValueList ();
            for (PmsSkuSaleAttrValue attrValue : attrValueList) {
                k += attrValue.getSaleAttrValueId () + "|";
            }
            k = k.substring (0, k.length () - 1);
            skuValuesHashMap.put (k, v);
        }
        //4. 将sku的销售属性哈希表，返回给前台
        String skuSaleAttrHashJsonStr = JSON.toJSONString (skuValuesHashMap);
        map.put ("skuSaleAttrHashJsonStr", skuSaleAttrHashJsonStr);
        return "item";
    }


    @RequestMapping("test")
    public String test(ModelMap modelMap) {
        modelMap.put ("hello", "thymeleaf 取值测试");
        return "index";
    }

}
