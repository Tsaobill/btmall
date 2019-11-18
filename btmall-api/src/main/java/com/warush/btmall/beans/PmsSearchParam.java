package com.warush.btmall.beans;

import java.io.Serializable;

/**
 * @version 1.0
 * @created by bill
 * @on 2019-11-17 18:23
 **/
public class PmsSearchParam implements Serializable {
    private String catalog3Id;

    private String keyword;

    //private List<PmsSkuAttrValue> skuAttrValueList;

    private String[] valueIds;

    public String getCatalog3Id() {
        return catalog3Id;
    }

    public void setCatalog3Id(String catalog3Id) {
        this.catalog3Id = catalog3Id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

//    public List<PmsSkuAttrValue> getSkuAttrValueList() {
//        return skuAttrValueList;
//    }
//
//    public void setSkuAttrValueList(List<PmsSkuAttrValue> skuAttrValueList) {
//        this.skuAttrValueList = skuAttrValueList;
//    }

    public String[] getValueIds() {
        return valueIds;
    }

    public void setValueIds(String[] valueIds) {
        this.valueIds = valueIds;
    }
}
