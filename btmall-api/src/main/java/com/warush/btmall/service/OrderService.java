package com.warush.btmall.service;

import com.warush.btmall.beans.OmsOrder;

public interface OrderService {

    String checkTradeCode(String memberId, String tradeCode);

    String genTradeCode(String memberId);


    void saveOrder(OmsOrder omsOrder);

    OmsOrder getOrderByOutTradeNo(String outTradeNo);

    void updateOrder(OmsOrder omsOrder);
}
