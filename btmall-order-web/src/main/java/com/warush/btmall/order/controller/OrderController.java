package com.warush.btmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.warush.btmall.annotations.LoginRequired;
import com.warush.btmall.beans.OmsCartItem;
import com.warush.btmall.beans.OmsOrder;
import com.warush.btmall.beans.OmsOrderItem;
import com.warush.btmall.beans.UmsMemberReceiveAddress;
import com.warush.btmall.service.CartService;
import com.warush.btmall.service.OrderService;
import com.warush.btmall.service.SkuService;
import com.warush.btmall.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @version 1.0
 * @created by bill
 * @on 2019-11-22 23:45
 **/
@Controller
public class OrderController {

    @Reference(timeout = 600000,retries = 0)
    CartService cartService;

    @Reference(timeout = 600000,retries = 0)
    UserService userService;

    @Reference(timeout = 600000,retries = 0)
    OrderService orderService;

    @Reference(timeout = 600000,retries = 0)
    SkuService skuService;


    @RequestMapping("submitOrder")
    @LoginRequired(loginSuccess = false)
    public ModelAndView submitOrder(String receiveAddressId, String nickname, BigDecimal totalAmount, String tradeCode, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String memberId = "1";
        // pre：检查交易码
        String success = orderService.checkTradeCode (memberId, tradeCode);
        if (success.equals ("success")) {
            // 1. 根据用户id获得要购买的商品列表（购物车中的勾选商品），而不是结算页面的数据

            List<OmsCartItem> omsCartItems = cartService.cartList (memberId);

            // 订单对象
            OmsOrder omsOrder = new OmsOrder ();
            omsOrder.setAutoConfirmDay (7);
            omsOrder.setCreateTime (new Date ());
            omsOrder.setMemberId (memberId);
            omsOrder.setMemberUsername (nickname);
            omsOrder.setNote ("尽快发货");
            // 外部订单号
            String outTradeNo = "btmall";
            outTradeNo = outTradeNo + System.currentTimeMillis ();
            SimpleDateFormat sdf = new SimpleDateFormat ("YYYYMMDDHHmmss");
            outTradeNo = outTradeNo + sdf.format (new Date ());
            omsOrder.setOrderSn (outTradeNo);

            omsOrder.setPayAmount (totalAmount);
            omsOrder.setOrderType (0);

            UmsMemberReceiveAddress addr = userService.getReceiveAddressById (receiveAddressId);
            omsOrder.setReceiverCity (addr.getCity ());
            omsOrder.setReceiverDetailAddress (addr.getDetailAddress ());
            omsOrder.setReceiverName (addr.getName ());
            omsOrder.setReceiverPhone (addr.getPhoneNumber ());
            omsOrder.setReceiverPostCode (addr.getPostCode ());
            omsOrder.setReceiverProvince (addr.getProvince ());
            omsOrder.setReceiverRegion (addr.getRegion ());

            omsOrder.setStatus ("0");

            // 订单对象的商品列表
            List<OmsOrderItem> omsOrderItems = new ArrayList<> ();

            // 2 校验价格和库存
            // 如果列表中有一个商品校验失败
            for (OmsCartItem omsCartItem : omsCartItems) {
                if (omsCartItem.getIsChecked ().equals ("1")) {
                    OmsOrderItem omsOrderItem = new OmsOrderItem ();
                    // 2.1 验价
                    boolean checkPrice = skuService.checkPrice (omsCartItem.getProductSkuId (), omsCartItem.getPrice ());
                    if (!checkPrice) {
                        return new ModelAndView ("tradeFail");
                    }

                    // 2.2 验库存，远程调用库存系统
                    //TODO

                    omsOrderItem.setProductSkuId (omsCartItem.getProductSkuId ());
                    omsOrderItem.setProductPic (omsCartItem.getProductPic ());
                    omsOrderItem.setProductName (omsCartItem.getProductName ());
                    omsOrderItem.setProductCategoryId (omsCartItem.getProductCategoryId ());
                    omsOrderItem.setProductPrice (omsCartItem.getPrice ());
                    omsOrderItem.setProductQuantity (omsCartItem.getQuantity ());
                    omsOrderItem.setRealAmount (omsCartItem.getTotalPrice ());
                    omsOrderItem.setProductId (omsCartItem.getProductId ());
                    omsOrderItem.setProductSn ("仓库对于的商品编号");

                    // 外部订单号

                    omsOrderItem.setOrderSn (outTradeNo);

                    omsOrderItems.add (omsOrderItem);
                }
            }
            omsOrder.setOmsOrderItems (omsOrderItems);


            // 3 将订单和订单详情写入数据库并删除购物车中的商品
            orderService.saveOrder (omsOrder);


            // 4. 重定向到支付系统
            ModelAndView mv = new ModelAndView ("redirect:http://127.0.0.1:8087/index");
            mv.addObject ("totalAmount", totalAmount);
            mv.addObject ("outTradeNo", outTradeNo);
            return mv;
        } else {
            return new ModelAndView ("tradeFail");
        }

    }

    @RequestMapping("toTrade")
    @LoginRequired(loginSuccess = false)
    public String cartList(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, HttpSession
            session) {
        String memberId = "1";

        // 收件人地址列表
        List<UmsMemberReceiveAddress> address = userService.getReceiveAddressByMemberId (memberId);
        modelMap.put ("userAddressList", address);


        List<OmsCartItem> omsCartItems = cartService.cartList (memberId);

        // 将购物车集合转化为结算页面的清单集合

        List<OmsOrderItem> omsOrderItems = new ArrayList<> ();
        BigDecimal price = getOrderTotalPrice (omsCartItems);
        modelMap.put ("totalAmount", price);

        for (OmsCartItem omsCartItem : omsCartItems) {
            if (omsCartItem.getIsChecked ().equals ("1")) {
                OmsOrderItem omsOrderItem = new OmsOrderItem ();

                omsOrderItem.setProductSkuId (omsCartItem.getProductSkuId ());
                omsOrderItem.setProductName (omsCartItem.getProductName ());
                omsOrderItem.setProductPic (omsCartItem.getProductPic ());
                omsOrderItem.setProductQuantity (omsCartItem.getQuantity ());
                omsOrderItems.add (omsOrderItem);
            }
        }
        modelMap.put ("omsOrderItems", omsOrderItems);

        // 生成交易码，以供提交订单时校验
        String tradeCode = orderService.genTradeCode (memberId);
        modelMap.put ("tradeCode", tradeCode);
        return "trade";
    }


    private BigDecimal getOrderTotalPrice(List<OmsCartItem> omsCartItems) {
        BigDecimal res = new BigDecimal ("0");
        for (OmsCartItem cartItem : omsCartItems) {
            if (cartItem.getIsChecked ().equals ("1")) {
                BigDecimal sum = cartItem.getPrice ().multiply (cartItem.getQuantity ());
                res = res.add (sum);
            }
        }
        return res;
    }
}
