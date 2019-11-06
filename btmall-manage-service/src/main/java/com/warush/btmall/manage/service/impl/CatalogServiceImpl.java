package com.warush.btmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.warush.btmall.beans.PmsBaseCatalog1;
import com.warush.btmall.beans.PmsBaseCatalog2;
import com.warush.btmall.beans.PmsBaseCatalog3;
import com.warush.btmall.manage.mapper.PmsBaseCatalog1Mapper;
import com.warush.btmall.manage.mapper.PmsBaseCatalog2Mapper;
import com.warush.btmall.manage.mapper.PmsBaseCatalog3Mapper;
import com.warush.btmall.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @version 1.0
 * @created by bill
 * @on 2019-11-06 22:31
 **/

@Service
public class CatalogServiceImpl implements CatalogService {

    @Autowired
    PmsBaseCatalog1Mapper pmsBaseCatalog1Mapper;

    @Autowired
    PmsBaseCatalog2Mapper pmsBaseCatalog2Mapper;

    @Autowired
    PmsBaseCatalog3Mapper pmsBaseCatalog3Mapper;


    @Override
    public List<PmsBaseCatalog1> getCatalog1() {
        return pmsBaseCatalog1Mapper.selectAll ();
    }

    @Override
    public List<PmsBaseCatalog2> getCatalog2(String catalog1Id) {
        PmsBaseCatalog2 pmsBaseCatalog2 = new PmsBaseCatalog2 ();
        pmsBaseCatalog2.setCatalog1Id (catalog1Id);
        List<PmsBaseCatalog2> catalog2List = pmsBaseCatalog2Mapper.select (pmsBaseCatalog2);

        return catalog2List;
    }

    @Override
    public List<PmsBaseCatalog3> getCatalog3(String catalog2Id) {
        PmsBaseCatalog3 pmsBaseCatalog3 = new PmsBaseCatalog3 ();
        pmsBaseCatalog3.setCatalog2Id (catalog2Id);
        List<PmsBaseCatalog3> catalog3List = pmsBaseCatalog3Mapper.select (pmsBaseCatalog3);

        return catalog3List;
    }
}
