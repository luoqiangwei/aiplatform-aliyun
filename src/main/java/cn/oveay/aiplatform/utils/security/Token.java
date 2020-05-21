package cn.oveay.aiplatform.utils.security;

import cn.oveay.aiplatform.utils.redis.JedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
@Service
public class Token {
    private Integer timeout;
    {
        InputStream inStream = Token.class.getClassLoader().getResourceAsStream("application.properties");
        Properties properties = new Properties();
        try {
            properties.load(inStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        timeout = Integer.parseInt(properties.get("token-timeout").toString());
    }

    /**
     * 检查form的token是否正确，用 + "⚪"来标识时fOrm表单的验证信息
     * @param userId
     * @param key
     * @return
     */
    public boolean checkToken(String userId, String key){
        log.warn("check Token User: " + userId + "O");
        return String.valueOf(JedisUtils.get(userId + "O")).equals(key);
    }
    public boolean addToken(String userId, String key){
        log.warn("create Token User: " + userId + "O");
        log.warn("create Token key: " + key);
        return JedisUtils.set(userId + "O", key, timeout);
    }
}
