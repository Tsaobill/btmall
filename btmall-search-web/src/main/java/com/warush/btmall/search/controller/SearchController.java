package com.warush.btmall.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.warush.btmall.beans.*;
import com.warush.btmall.service.AttrService;
import com.warush.btmall.service.SearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @version 1.0
 * @created by bill
 * @on 2019-11-16 21:47
 **/
@Controller
public class SearchController {
    @Reference
    SearchService searchService;

    @Reference
    AttrService attrService;


    @RequestMapping("list.html")
    public String list(PmsSearchParam pmsSearchParam, ModelMap modelMap) { // 三级分类id，关键字、

        List<PmsSearchSkuInfo> pmsSearchSkuInfos = searchService.list (pmsSearchParam);
        modelMap.put ("skuLsInfoList", pmsSearchSkuInfos);
        // 调用搜索服务，返回搜索结果

        Set<String> valueIdSet = new HashSet<> ();
        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
            List<PmsSkuAttrValue> skuAttrValueList = pmsSearchSkuInfo.getSkuAttrValueList ();
            for (PmsSkuAttrValue skuAttrValue : skuAttrValueList) {
                String valueId = skuAttrValue.getValueId ();
                valueIdSet.add (valueId);
            }
        }

        // 根据valueID将属性列表查询出来
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = attrService.getAttrValueListBuValueId (valueIdSet);
        modelMap.put ("attrList", pmsBaseAttrInfos);

        // 删除平台属性列表中已选中作为面包屑的属性
        String[] delValueIds = pmsSearchParam.getValueIds ();
        if (delValueIds != null) {
            // 面包屑
            List<PmsSearchCrumb> pmsSearchCrumbs = new ArrayList<> ();

            for (String delValueId : delValueIds) {
                Iterator<PmsBaseAttrInfo> iterator = pmsBaseAttrInfos.iterator ();
                PmsSearchCrumb crumb = new PmsSearchCrumb ();
                // 设置面包屑参数
                crumb.setValueId (delValueId);
                crumb.setUrlParam (getUrlParam (pmsSearchParam, delValueId));
                while (iterator.hasNext ()) {
                    PmsBaseAttrInfo next = iterator.next ();
                    List<PmsBaseAttrValue> attrValueList = next.getAttrValueList ();
                    for (PmsBaseAttrValue attrValue : attrValueList) {

                        String valueId = attrValue.getId ();
                        if (delValueId.equals (valueId)) {
                            // 设置面包屑的名称
                            crumb.setValueName (attrValue.getValueName ());

                            iterator.remove ();
                        }
                    }
                }
                pmsSearchCrumbs.add (crumb);
            }
            modelMap.put ("attrValueSelectedList", pmsSearchCrumbs);
        }


        String urlParam = getUrlParam (pmsSearchParam);
        modelMap.put ("urlParam", urlParam);
        String keyword = pmsSearchParam.getKeyword ();
        if (StringUtils.isNotBlank (keyword)) {
            modelMap.put ("keyword", keyword);
        }

        return "list";
    }

    private String getUrlParam(PmsSearchParam pmsSearchParam, String... delValueId) {

        String keyword = pmsSearchParam.getKeyword ();
        String catalog3Id = pmsSearchParam.getCatalog3Id ();
        String[] skuAttrValueIds = pmsSearchParam.getValueIds ();
        String urlParam = "";

        if (StringUtils.isNotBlank (keyword)) {
            if (StringUtils.isNotBlank (urlParam)) {
                urlParam += "&";
            }
            urlParam += "keyword=" + keyword;
        }
        if (StringUtils.isNotBlank (catalog3Id)) {
            if (StringUtils.isNotBlank (urlParam)) {
                urlParam += "&";
            }
            urlParam += "catalog3Id=" + catalog3Id;

        }
        if (skuAttrValueIds != null) {
            for (String skuAttrValueId : skuAttrValueIds) {
                if (!skuAttrValueId.equals (delValueId)) {
                    urlParam += "&valueId=" + skuAttrValueId;
                }
            }
        }

        return null;
    }

    @RequestMapping("put")
    @ResponseBody
    public String put() {
        searchService.putDataToSearchDB ();
        return "put success";
    }

    @RequestMapping("index")
    public String index() {

        return "index";
    }

}
