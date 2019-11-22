package com.warush.btmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.warush.btmall.annotations.LoginRequired;
import com.warush.btmall.beans.OmsCartItem;
import com.warush.btmall.beans.PmsSkuInfo;
import com.warush.btmall.service.CartService;
import com.warush.btmall.service.SkuService;
import com.warush.btmall.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @version 1.0
 * @created by bill
 * @on 2019-11-19 17:56
 **/
@Controller
public class CartController {

    @Reference
    SkuService skuService;

    @Reference
    CartService cartService;



    @RequestMapping("toTrade")
    @LoginRequired(loginSuccess = false)
    public String cartList(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String memberId =(String) request.getAttribute ("memberId");
        String nickname =(String) request.getAttribute ("nickname");

        return "trade";
    }



    @RequestMapping("checkCartaaaaa")
    public String checkCart(String isChecked, String skuId,ModelMap modelMap,HttpServletRequest request){
        String memberId =(String) request.getAttribute ("memberId");
        String nickname =(String) request.getAttribute ("nickname");
        //调用服务，修改状态
        OmsCartItem omsCartItem = new OmsCartItem ();
        omsCartItem.setMemberId (memberId);
        omsCartItem.setIsChecked (isChecked);
        omsCartItem.setProductSkuId (skuId);
        cartService.checkCart(omsCartItem);


        // 将最新数据从缓存中取出，渲染给内嵌页面
        List<OmsCartItem> omsCartItems = cartService.cartList (memberId);
        modelMap.put ("cartList", omsCartItems);

        return "cartListInner";
    }

    @RequestMapping("cartList")
    @LoginRequired(loginSuccess = false)
    public String cartList(ModelMap modelMap, HttpServletRequest request) {
        List<OmsCartItem> omsCartItems = new ArrayList<> ();
        String memberId = (String) request.getAttribute ("memberId");
        String nickname = (String) request.getAttribute ("nickname");
        if (StringUtils.isNotBlank (memberId)) {
            // 已经登录查询db
            omsCartItems = cartService.cartList (memberId);
        } else {
            // 未登录查询cookie
            String cartListCookie = CookieUtil.getCookieValue (request, "cartListCookie", true);
            if (StringUtils.isNotBlank (cartListCookie)) {
                omsCartItems = JSON.parseArray (cartListCookie, OmsCartItem.class);
            }
        }

        for (OmsCartItem omsCartItem : omsCartItems) {
            omsCartItem.setTotalPrice (omsCartItem.getPrice ().multiply (omsCartItem.getQuantity ()));
        }


        modelMap.put ("cartList", omsCartItems);
        BigDecimal checkedTotalPrice = getCheckedTotalPrice (omsCartItems);
        modelMap.put ("checkedTotalPrice", checkedTotalPrice);
        return "cartList";
    }

    private BigDecimal getCheckedTotalPrice(List<OmsCartItem> omsCartItems) {
        BigDecimal res = new BigDecimal ("0");
        for (OmsCartItem cartItem : omsCartItems) {
            if(cartItem.getIsChecked ().equals ("1")){
                res.add (cartItem.getTotalPrice ());
            }
        }
        return res;
    }

    @RequestMapping("addToCart")
    @LoginRequired(loginSuccess = false)
    public String addToCart(String skuId, int quantity, HttpServletRequest request, HttpServletResponse response) {

        // 1.根据skuid查询商品信息
        PmsSkuInfo skuInfo = skuService.getSkuById (skuId);


        // 2.将商品信息封装成购物车信息
        OmsCartItem omsCartItem = new OmsCartItem ();
        omsCartItem.setCreateDate (new Date ());
        omsCartItem.setDeleteStatus (0);
        omsCartItem.setProductSkuId (skuId);
        omsCartItem.setModifyDate (new Date ());
        omsCartItem.setPrice (skuInfo.getPrice ());
        omsCartItem.setProductAttr ("");
        omsCartItem.setProductBrand ("");
        omsCartItem.setProductId (skuInfo.getProductId ());
        omsCartItem.setProductName (skuInfo.getSkuName ());
        omsCartItem.setQuantity (new BigDecimal (quantity));
        omsCartItem.setProductPic (skuInfo.getSkuDefaultImg ());


        List<OmsCartItem> omsCartItems = new ArrayList<> ();

        // 3.判断用户登录状态
        String memberId = (String) request.getAttribute ("memberId");
        String nickname = (String) request.getAttribute ("nickname");
        if (StringUtils.isBlank (memberId)) {
            // 未登录
            // 4.1 cookie 同步购物车信息
            // cookie跨域问题
            // 从cookie里获取原本的购物车列表
            String cartListCookie = CookieUtil.getCookieValue (request, "cartListCookie", true);
            if (StringUtils.isBlank (cartListCookie)) {
                // cookie为空
                omsCartItems.add (omsCartItem);
            } else {
                omsCartItems = JSON.parseArray (cartListCookie, OmsCartItem.class);
                boolean exist = ifCartExist (omsCartItems, omsCartItem);
                if (!exist) {
                    // 不存在该商品
                    omsCartItems.add (omsCartItem);
                }
            }
            //5.1 覆盖cookie
            CookieUtil.setCookie (request, response, "cartListCookie", JSON.toJSONString (omsCartItems), 60 * 60 * 24 * 3, true);
        } else {
            // 已登录
            // 4.2 db 同步购物车信息
            // db + cache

            // 从db中查询购物车数据
            OmsCartItem omsCartItemInDB = cartService.ifCartExistByUser (memberId, skuId);
            if (omsCartItemInDB == null) {
                // 购物车中没有该商品
                omsCartItem.setMemberId (memberId);
                omsCartItem.setQuantity (new BigDecimal (quantity));
                omsCartItem.setProductSkuId (skuId);
                omsCartItem.setProductId (skuInfo.getProductId ());
                omsCartItem.setProductName (omsCartItem.getProductName ());
                omsCartItem.setProductSubTitle (omsCartItem.getProductSubTitle ());
                cartService.addToCart (omsCartItem);

            } else {
                // db中购物车存在该商品
                omsCartItemInDB.setQuantity (omsCartItemInDB.getQuantity ().add (omsCartItem.getQuantity ()));
                cartService.updateCart (omsCartItemInDB);
            }

            // 5.2更新db和缓存
            cartService.synCartCache (memberId);
        }


        return "redirect:/success.html";
    }

    private boolean ifCartExist(List<OmsCartItem> omsCartItems, OmsCartItem omsCartItem) {
        boolean exist = false;
        for (OmsCartItem cartItem : omsCartItems) {
            if (cartItem.getProductSkuId ().equals (omsCartItem.getProductSkuId ())) {
                cartItem.setQuantity (cartItem.getQuantity ().add (omsCartItem.getQuantity ()));
                cartItem.setPrice (cartItem.getPrice ().add (omsCartItem.getPrice ()));
                exist = true;
            }
        }
        return exist;
    }

    @RequestMapping("addToCarta")
    @LoginRequired(loginSuccess = false)
    public ModelAndView a() {

        return new ModelAndView ("success");
//        return "redirect:/success.html";
    }
}
