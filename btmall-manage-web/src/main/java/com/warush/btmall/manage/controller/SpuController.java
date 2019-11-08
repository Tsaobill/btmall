package com.warush.btmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.warush.btmall.beans.PmsBaseSaleAttr;
import com.warush.btmall.beans.PmsProductImage;
import com.warush.btmall.beans.PmsProductInfo;
import com.warush.btmall.beans.PmsProductSaleAttr;
import com.warush.btmall.manage.util.PmsUploadUtil;
import com.warush.btmall.service.AttrService;
import com.warush.btmall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @version 1.0
 * @created by bill
 * @on 2019-11-07 17:44
 **/
@Controller
@CrossOrigin
public class SpuController {

    @Reference
    SpuService spuService;

    @Reference
    AttrService attrService;

    @RequestMapping("fileUpload")
    @ResponseBody
    public String fileUpload(@RequestParam("file") MultipartFile multipartFile) {
        // 将图片上传到分布式文件存储系统
        // 链接FastDFS
        //  将存储的地址返回给前端
        String imgUrl = "";
        try {
            imgUrl = PmsUploadUtil.uploadImage (multipartFile);
        } catch (Exception e) {
        }
        return imgUrl;
    }

    @RequestMapping("saveSpuInfo")
    @ResponseBody
    public String saveSpuInfo(@RequestBody PmsProductInfo productInfo) {
        String res = spuService.saveSpuInfo (productInfo);
        return "";
    }


    @RequestMapping("spuList")
    @ResponseBody
    public List<PmsProductInfo> getSpuList(String catalog3Id) {
        List<PmsProductInfo> productInfoList = spuService.spuList (catalog3Id);
        return productInfoList;
    }


    @RequestMapping("spuSaleAttrList")
    @ResponseBody
    public List<PmsProductSaleAttr> getSpuSaleAttrList(String spuId) {
        List<PmsProductSaleAttr> saleAttrList = attrService.spuSaleAttrList (spuId);
        return saleAttrList;
    }

    @RequestMapping("spuImageList")
    @ResponseBody
    public List<PmsProductImage> getSpuImageList(String spuId) {
        List<PmsProductImage> imageList = spuService.spuImageList (spuId);
        return imageList;
    }

    @RequestMapping("baseSaleAttrList")
    @ResponseBody
    public List<PmsBaseSaleAttr> getBaseSaleAttrList() {
        List<PmsBaseSaleAttr> baseSaleAttrs = attrService.baseSaleAttrList ();
        return baseSaleAttrs;
    }
}
