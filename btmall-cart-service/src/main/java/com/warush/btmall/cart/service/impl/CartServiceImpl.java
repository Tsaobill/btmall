package com.warush.btmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.warush.btmall.beans.OmsCartItem;
import com.warush.btmall.cart.mapper.OmsCartItemMapper;
import com.warush.btmall.service.CartService;
import com.warush.btmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version 1.0
 * @created by bill
 * @on 2019-11-19 22:52
 **/
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    OmsCartItemMapper omsCartItemMapper;

    @Autowired
    RedisUtil redisUtil;


    @Override
    public void checkCart(OmsCartItem omsCartItem) {
        Example e = new Example (OmsCartItem.class);
        e.createCriteria ().andEqualTo ("memberId", omsCartItem.getMemberId ()).andEqualTo ("productSkuId", omsCartItem.getProductSkuId ());
        omsCartItemMapper.updateByExampleSelective (omsCartItem, e);
        synCartCache (omsCartItem.getMemberId ());
    }

    @Override
    public List<OmsCartItem> cartList(String userId) {
        Jedis jedis = null;
        List<OmsCartItem> omsCartItems = new ArrayList<> ();
        try {

            jedis = redisUtil.getJedis ();
            String key = "user:" + userId + ":cart";
            List<String> hvals = jedis.hvals (key);
            // 如果hvals != null，缓存中有数据
            for (String hval : hvals) {
                omsCartItems.add (JSON.parseObject (hval, OmsCartItem.class));
            }
            // 否则去db中查询

        } catch (Exception e) {
            e.printStackTrace ();
        } finally {
            jedis.close ();
        }
        return omsCartItems;
    }

    @Override
    public OmsCartItem ifCartExistByUser(String memberId, String skuId) {
        OmsCartItem omsCartItem = new OmsCartItem ();
        omsCartItem.setMemberId (memberId);
        omsCartItem.setProductSkuId (skuId);

        OmsCartItem selectOne = omsCartItemMapper.selectOne (omsCartItem);
        return selectOne;
    }

    @Override
    public void addToCart(OmsCartItem omsCartItem) {
        if (StringUtils.isNotBlank (omsCartItem.getMemberId ())) {
            omsCartItemMapper.insertSelective (omsCartItem);
        }
    }

    @Override
    public void updateCart(OmsCartItem omsCartItemInDB) {
        Example e = new Example (OmsCartItem.class);
        e.createCriteria ().andEqualTo ("id", omsCartItemInDB.getId ());
        omsCartItemMapper.updateByExampleSelective (omsCartItemInDB, e);
    }

    @Override
    public void synCartCache(String memberId) {
        // 从db查询
        OmsCartItem omsCartItem = new OmsCartItem ();
        omsCartItem.setMemberId (memberId);
        List<OmsCartItem> omsCartItems = omsCartItemMapper.select (omsCartItem);

        // 同步到缓存
        Jedis jedis = redisUtil.getJedis ();
        try {
            String key = "user:" + memberId + ":cart";
            Map<String, String> map = new HashMap<> ();
            for (OmsCartItem cartItem : omsCartItems) {
                map.put (cartItem.getProductSkuId (), JSON.toJSONString (cartItem));
            }
            jedis.del (key);
            jedis.hmset (key, map);
        } finally {
            jedis.close ();
        }
    }
}
