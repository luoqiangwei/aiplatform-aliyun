package cn.oveay.aiplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan("cn.oveay.aiplatform.filter")
public class AiplatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiplatformApplication.class, args);
    }

}
