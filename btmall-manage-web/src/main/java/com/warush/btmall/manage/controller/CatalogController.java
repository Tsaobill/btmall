package com.warush.btmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.warush.btmall.beans.PmsBaseCatalog1;
import com.warush.btmall.beans.PmsBaseCatalog2;
import com.warush.btmall.beans.PmsBaseCatalog3;
import com.warush.btmall.service.CatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @version 1.0
 * @created by bill
 * @on 2019-11-06 22:11
 **/
@Controller
@CrossOrigin
public class CatalogController {
    @Reference
    CatalogService catalogService;

    @RequestMapping("getCatalog1")
    @ResponseBody
    public List<PmsBaseCatalog1> getCatalog1() {
        List<PmsBaseCatalog1> list = catalogService.getCatalog1 ();
        return list;
    }

    @RequestMapping("getCatalog2")
    @ResponseBody
    public List<PmsBaseCatalog2> getCatalog2(String catalog1Id) {
        List<PmsBaseCatalog2> list = catalogService.getCatalog2 (catalog1Id);
        return list;
    }

    @RequestMapping("getCatalog3")
    @ResponseBody
    public List<PmsBaseCatalog3> getCatalog3(String catalog2Id) {
        List<PmsBaseCatalog3> list = catalogService.getCatalog3 (catalog2Id);
        return list;
    }
}
