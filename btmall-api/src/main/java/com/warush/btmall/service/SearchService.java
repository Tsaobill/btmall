package com.warush.btmall.service;

import com.warush.btmall.beans.PmsSearchParam;
import com.warush.btmall.beans.PmsSearchSkuInfo;

import java.util.List;

public interface SearchService {

    List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam);

    void putDataToSearchDB();
}
