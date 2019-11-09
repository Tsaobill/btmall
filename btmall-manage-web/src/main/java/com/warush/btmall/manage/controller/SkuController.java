package com.warush.btmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.warush.btmall.beans.PmsSkuInfo;
import com.warush.btmall.service.SkuService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @version 1.0
 * @created by bill
 * @on 2019-11-08 20:11
 **/
@Controller
@CrossOrigin
public class SkuController {

    @Reference
    SkuService skuService;

    @RequestMapping("saveSkuInfo")
    @ResponseBody
    public String saveSkuInfo(@RequestBody PmsSkuInfo pmsSkuInfo) {
        // 将spuId，封装给productId
        pmsSkuInfo.setProductId (pmsSkuInfo.getSpuId ());

        // 处理默认图片
        String defaulImage = pmsSkuInfo.getSkuDefaultImg ();
        if (StringUtils.isBlank (defaulImage)) {
            pmsSkuInfo.setSkuDefaultImg (pmsSkuInfo.getSkuImageList ().get (0).getImgUrl ());
        }


        skuService.saveSkuInfo (pmsSkuInfo);
        return null;
    }
}
