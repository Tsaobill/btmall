package com.warush.btmall.manage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = "com.warush.btmall.manage.mapper")
public class BtmallManageServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run (BtmallManageServiceApplication.class, args);
    }

}
