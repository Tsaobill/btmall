#btmall电商系统

###用户系统
- btmall-user-service的服务端口为8070
- btmall-user-web的服务端口为8080


###后台管理系统
- btmall-manage-service的服务端口为8071
- btmall-manage-web的服务端口为8081


###商品详情系统
- btmall-item-service 前台系统的商品详情服务 8072
    这一部分实际上是调用manage-service的服务
    这里就体现出SOA的特性，
- btmall-item-web 前台系统的商品详情展示 8082


###搜索服务
- btmall-manage-service的端口为8074
- btmall-manage-web的端口为8083

###购物车服务
- btmall-cart-service的端口为8074
- btmall-cart-web的端口为8084

###用户认证服务
- btmall-user-service的服务端口为8070
- btmall-passport-web的端口为8085

###订单服务
- btmall-order-web的端口为8086
- btmall-order-service的端口为8076


###支付服务
- btmall-payment的端口为8087