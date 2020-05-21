package cn.oveay.aiplatform;

import cn.oveay.aiplatform.utils.redis.JedisUtils;
import cn.oveay.aiplatform.utils.token.Token;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class AiplatformApplicationTests {

    @Test
    void contextLoads() {
        Token.set("E6B0C241DBF8CEBFB3195E6B3396DDBDO", "1111", 600);
        assert (Token.check("E6B0C241DBF8CEBFB3195E6B3396DDBDO", "1111"));
    }

}
