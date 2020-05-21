package cn.oveay.aiplatform;

import cn.oveay.aiplatform.utils.redis.JedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class AiplatformApplicationTests {

    @Test
    void contextLoads() {
        JedisUtils.set("E6B0C241DBF8CEBFB3195E6B3396DDBDO", "1111", 600);
        log.warn(JedisUtils.get("E6B0C241DBF8CEBFB3195E6B3396DDBDO"));
    }

}
