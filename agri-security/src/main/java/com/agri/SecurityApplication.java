package com.agri;

import com.agri.utils.annotation.EnableSaveAuth;
import com.github.jeffreyning.mybatisplus.conf.EnableMPP;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan(value = "com.agri.mapper")
@EnableAspectJAutoProxy
@EnableMPP
@EnableSaveAuth
public class SecurityApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecurityApplication.class);
    }
}
