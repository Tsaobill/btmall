package com.warush.btmall.manage;

import tk.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.warush.btmall.manage.mapper")
public class BtmallManageServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run (BtmallManageServiceApplication.class, args);
    }

}
