package com.warush.btmall.service;

import com.warush.btmall.beans.OmsCartItem;

import java.util.List;

public interface CartService {
    OmsCartItem ifCartExistByUser(String memberId, String skuId);

    void addToCart(OmsCartItem omsCartItem);

    void updateCart(OmsCartItem omsCartItemInDB);

    void synCartCache(String memberId);

    List<OmsCartItem> cartList(String userId);

    void checkCart(OmsCartItem omsCartItem);
}
