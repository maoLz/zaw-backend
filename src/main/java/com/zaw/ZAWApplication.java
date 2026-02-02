package com.zaw;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author lizhong.zhang@yituishui.com
 * @since 2026/1/9
 */
@SpringBootApplication
@EnableAsync
public class ZAWApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZAWApplication.class, args);
    }


}
