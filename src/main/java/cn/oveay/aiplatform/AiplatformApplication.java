package cn.oveay.aiplatform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan("cn.oveay.aiplatform.filter")
@MapperScan("cn.oveay.aiplatform.dao")
public class AiplatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiplatformApplication.class, args);
    }

}
