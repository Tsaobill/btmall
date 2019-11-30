package com.warush.btmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.warush.btmall.beans.PmsSkuAttrValue;
import com.warush.btmall.beans.PmsSkuImage;
import com.warush.btmall.beans.PmsSkuInfo;
import com.warush.btmall.beans.PmsSkuSaleAttrValue;
import com.warush.btmall.manage.mapper.PmsSkuAttrValueMapper;
import com.warush.btmall.manage.mapper.PmsSkuImageMapper;
import com.warush.btmall.manage.mapper.PmsSkuInfoMapper;
import com.warush.btmall.manage.mapper.PmsSkuSaleAttrValueMpper;
import com.warush.btmall.service.SkuService;
import com.warush.btmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * @version 1.0
 * @created by bill
 * @on 2019-11-08 20:32
 **/

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    PmsSkuInfoMapper skuInfoMapper;

    @Autowired
    PmsSkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    PmsSkuSaleAttrValueMpper skuSaleAttrValueMpper;

    @Autowired
    PmsSkuImageMapper skuImageMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public boolean checkPrice(String productSkuId, BigDecimal price) {
        boolean res = false;
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo ();
        pmsSkuInfo.setId (productSkuId);
        PmsSkuInfo skuInfoInDb = skuInfoMapper.selectOne (pmsSkuInfo);
        res = skuInfoInDb.getPrice ().compareTo (price) == 0;

        return res;
    }

    @Override
    public List<PmsSkuInfo> getAllSku(String catalog3Id) {
        List<PmsSkuInfo> pmsSkuInfos = skuInfoMapper.selectAll ();
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {
            String skuId = pmsSkuInfo.getId ();

            PmsSkuAttrValue skuAttrValue = new PmsSkuAttrValue ();
            skuAttrValue.setSkuId (skuId);
            List<PmsSkuAttrValue> attrValues = skuAttrValueMapper.select (skuAttrValue);
            pmsSkuInfo.setSkuAttrValueList (attrValues);
        }
        return pmsSkuInfos;
    }


    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {
        skuInfoMapper.insertSelective (pmsSkuInfo);
        String skuId = pmsSkuInfo.getId ();
        // 插入平台属性关联
        List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList ();
        for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
            pmsSkuAttrValue.setSkuId (skuId);
            skuAttrValueMapper.insertSelective (pmsSkuAttrValue);
        }

        // 插入销售属性关联
        List<PmsSkuSaleAttrValue> saleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList ();
        for (PmsSkuSaleAttrValue saleAttrValue : saleAttrValueList) {
            saleAttrValue.setSkuId (skuId);
            skuSaleAttrValueMpper.insertSelective (saleAttrValue);
        }
        // 插入图片信息
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList ();
        for (PmsSkuImage skuImage : skuImageList) {
            skuImage.setSkuId (skuId);
            skuImageMapper.insertSelective (skuImage);
        }

    }

    public PmsSkuInfo getSkuByIdFromDB(String skuId) {
        // 商品对象
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo ();
        pmsSkuInfo.setId (skuId);
        PmsSkuInfo skuInfo = skuInfoMapper.selectOne (pmsSkuInfo);

        // 获取商品图片列表
        PmsSkuImage pmsSkuImage = new PmsSkuImage ();
        pmsSkuImage.setSkuId (skuId);
        List<PmsSkuImage> skuImages = skuImageMapper.select (pmsSkuImage);
        skuInfo.setSkuImageList (skuImages);

        return skuInfo;
    }

    @Override
    public PmsSkuInfo getSkuById(String skuId) {
        PmsSkuInfo pmsSkuInfo;
        // 连接缓存
        Jedis jedis = redisUtil.getJedis ();

        try {
            // 查询缓存
            String skuKey = "sku:" + skuId + ":info";// 应该放在常量类中
            String skuJson = jedis.get (skuKey);
            if (StringUtils.isNoneBlank (skuJson)) { // 相当于非空且不为"";
                pmsSkuInfo = JSON.parseObject (skuJson, PmsSkuInfo.class);
            } else {
                // 缓存中没有, 查询mysql

                // 查询mysql之前，利用redis设置一个分布式锁
                String lock = "SkuInfo:" + skuId + ":lock";
                String token = UUID.randomUUID ().toString ();
                String setRes = jedis.set (lock, token, "nx", "ex", 10);
                if (StringUtils.isNoneBlank (setRes) && setRes.equals ("OK")) {
                    // 加锁成功，操作数据库
                    pmsSkuInfo = getSkuByIdFromDB (skuId);
                    if (pmsSkuInfo != null) {
                        // 将结果存入redis
                        jedis.set (skuKey, JSON.toJSONString (pmsSkuInfo));
                    } else {
                        // 这里可以将不存在的key设置一个特殊值和一个较小的过期时间，防止缓存穿透
                        jedis.setex (skuKey, 3 * 60, null);
                    }

                    // 删除锁
                    // 防止误删锁，删除之前需要验证是否是自己之前设置的
                    String lockToken = jedis.get (skuKey);
                    if (StringUtils.isNoneBlank (lockToken) && lockToken.equals (token)) {
                        // 判断成功才删除锁
                        jedis.del (lock);
                    }
                } else {
                    // 设置失败，自旋
                    try {
                        Thread.sleep (1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace ();
                    }
                    return getSkuById (skuId);// 注意这里通过return 方法调用，而不是直接调用
                }
            }
        } finally {
            // 关闭jedis.close()， 这一步应该放在finally中
            jedis.close ();
        }


        return pmsSkuInfo;
    }

    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId) {
        List<PmsSkuInfo> skuInfos = skuInfoMapper.selectSkuSaleAttrValueListBySpu (productId);
        return skuInfos;
    }
}
