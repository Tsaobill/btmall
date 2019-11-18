package com.warush.btmall.search.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.warush.btmall.beans.PmsSearchParam;
import com.warush.btmall.beans.PmsSearchSkuInfo;
import com.warush.btmall.beans.PmsSkuInfo;
import com.warush.btmall.service.SearchService;
import com.warush.btmall.service.SkuService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @version 1.0
 * @created by bill
 * @on 2019-11-17 19:00
 **/
@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    JestClient jestClient;
    @Reference
    SkuService skuService;

    @Override
    public List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam) {
        String strDsl = getSearchDsl (pmsSearchParam);
        List<PmsSearchSkuInfo> searchSkuInfos = new ArrayList<> ();
        try {
            searchSkuInfos = search (strDsl);
        } catch (IOException e) {
            e.printStackTrace ();
        }
        return searchSkuInfos;
    }


    /**
     * 使用 api执行复杂查询
     *
     * @return
     * @throws IOException
     */
    private List<PmsSearchSkuInfo> search(String dsl) throws IOException {
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<> ();
        Search search = new Search.Builder (dsl).addIndex ("btmall").addType ("pmsSkuInfo").build ();
        SearchResult result = jestClient.execute (search);
        List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = result.getHits (PmsSearchSkuInfo.class);
        for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
            // 解析source
            PmsSearchSkuInfo source = hit.source;
            // 解析highlight
            Map<String, List<String>> highlightMap = hit.highlight;
            if (highlightMap != null) {
                String skuName = highlightMap.get ("skuName").get (0);
                source.setSkuName (skuName);
            }
            pmsSearchSkuInfos.add (source);
        }
        return pmsSearchSkuInfos;
    }

    /**
     * 获取dsl查询语句
     *
     * @param pmsSearchParam
     * @return
     */
    private String getSearchDsl(PmsSearchParam pmsSearchParam) {
        String[] skuAttrValueList = pmsSearchParam.getValueIds ();
        String keyword = pmsSearchParam.getKeyword ();
        String catalog3Id = pmsSearchParam.getCatalog3Id ();
        // jest的dsl工具

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder ();
        // bool
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder ();
        //  filter
        if (StringUtils.isNoneBlank (catalog3Id)) {
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder ("catalog3Id", catalog3Id);
            boolQueryBuilder.filter (termQueryBuilder);
        }
        if (skuAttrValueList != null) {
            for (String skuAttrValue : skuAttrValueList) {
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder ("skuAttrValueList.valueId", skuAttrValue);
                boolQueryBuilder.filter (termQueryBuilder);
            }
        }
        // must
        if (StringUtils.isNoneBlank (keyword)) {
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder ("skuName", keyword);
            boolQueryBuilder.must (matchQueryBuilder);
        }

        //query
        searchSourceBuilder.query (boolQueryBuilder);
        //from
        searchSourceBuilder.from (0);
        //size
        searchSourceBuilder.size (20);
        //highlight
        HighlightBuilder highlightBuilder = new HighlightBuilder ();
        highlightBuilder.preTags ("<span style='color:red;'>");
        highlightBuilder.field ("skuName");
        highlightBuilder.postTags ("</span>");
        searchSourceBuilder.highlight (highlightBuilder);

        // aggs
        TermsBuilder groupby_attr = AggregationBuilders.terms ("groupby_attr").field ("skuAttrValueList.valueId");
        searchSourceBuilder.aggregation (groupby_attr);

        //sort
        searchSourceBuilder.sort ("id", SortOrder.DESC);

        String strDsl = searchSourceBuilder.toString ();
        System.err.println (strDsl);
        return strDsl;
    }


    @Override
    public void putDataToSearchDB() {
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
            try {
                jestClient.execute (put);
            } catch (IOException e) {
                e.printStackTrace ();
            }
        }
    }

}
