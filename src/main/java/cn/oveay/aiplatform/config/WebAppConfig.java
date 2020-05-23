package cn.oveay.aiplatform.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * @author OVEA(Qiangwei Luo)
 * created on : 2020/5/18 19:14
 * 文件说明：
 */
@Slf4j
@Configuration
public class WebAppConfig implements WebMvcConfigurer {
    @Value("${file.upload.imgPath}")
    public String imgPath;
    @Value("${file.uplaod.carpath}")
    public String carImagesPath;
    @Value("${file.uplaod.path}")
    public String idCardImagesPath;

    @Bean
    public WebAppConfig newWebAppConfig() {
        return new WebAppConfig();
    }

    /**
     * 用于开放资源
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.warn("addResourceHandlers...");
//        new WebAppConfig();
        registry.addResourceHandler("/images/idcard/**").addResourceLocations("file:" + idCardImagesPath);
        registry.addResourceHandler("/images/car/**").addResourceLocations("file:" + carImagesPath);
        registry.addResourceHandler("/img/**").addResourceLocations("file:" + imgPath);
//        log.warn(new File("file:base/static").getAbsolutePath());
//        registry.addResourceHandler("/**").addResourceLocations("base/static");
//        registry.addResourceHandler("/static/**").addResourceLocations("file:base/static");
//        registry.addResourceHandler("/templates/**").addResourceLocations("file:base/templates/");
//        registry.addResourceHandler("/img/**").addResourceLocations("file:" + imgPath);
    }
}
