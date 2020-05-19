package cn.oveay.aiplatform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author OVEA(Qiangwei Luo)
 * created on : 2020/5/18 19:14
 * 文件说明：
 */
public class WebAppConfig implements WebMvcConfigurer {
    @Value("${file.upload.imgPath}")
    public String imgPath;

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
        new WebAppConfig();
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/templates/**").addResourceLocations("classpath:/templates/");
        registry.addResourceHandler("/img/**").addResourceLocations("file:" + imgPath);
    }
}
