package com.warush.btmall;

import tk.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.warush.btmall.cart.mapper")
public class BtmallCartServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run (BtmallCartServiceApplication.class, args);
    }

}
