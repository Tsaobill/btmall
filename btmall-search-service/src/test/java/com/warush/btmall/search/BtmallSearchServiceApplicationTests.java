package com.warush.btmall.search;

import com.alibaba.dubbo.config.annotation.Reference;
import com.warush.btmall.beans.PmsSearchSkuInfo;
import com.warush.btmall.beans.PmsSkuInfo;
import com.warush.btmall.service.SkuService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class BtmallSearchServiceApplicationTests {

    @Reference
    SkuService skuService;

    @Autowired
    JestClient jestClient;

    @Test
    public void getDataToSearch() throws IOException {
        // 从mysql获取sku
        List<PmsSkuInfo> pmsSkuInfos = skuService.getAllSku ("");
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<> ();
        // 转化成search data
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {
            PmsSearchSkuInfo pmsSearchSkuInfo = new PmsSearchSkuInfo ();
            BeanUtils.copyProperties (pmsSkuInfo, pmsSearchSkuInfo);
            pmsSearchSkuInfos.add (pmsSearchSkuInfo);
        }

        // 存入search data base
        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
            Index put = new Index.Builder (pmsSearchSkuInfo).index ("btmall").type ("pmsSkuInfo").id (pmsSearchSkuInfo.getId ()).build ();
            jestClient.execute (put);
        }

    }

    @Test
    void searchES() throws IOException {
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<> ();
        Search search = new Search.Builder (null).addIndex (null).addType (null).build ();
        SearchResult result = jestClient.execute (search);
        List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = result.getHits (PmsSearchSkuInfo.class);
        for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
            PmsSearchSkuInfo source = hit.source;
            pmsSearchSkuInfos.add (source);
        }
    }

    @Test
    void contextLoads() {

    }

}
