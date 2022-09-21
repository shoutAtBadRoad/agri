package com.agri.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * springboot的跨域配置
 */
//@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 設置允許跨域請求的路徑
        registry.addMapping("/**")
                //設置允許跨域請求的域名
                .allowedOrigins("*")
                //是否允許cookie
                .allowCredentials(true)
                //設置允許的請求方式
                .allowedMethods("GET", "POST", "DELETE", "PUT")
                //設置允許的header屬性
                .allowedHeaders("*")
                //允許跨域時間
                .maxAge(3600);

    }
}
