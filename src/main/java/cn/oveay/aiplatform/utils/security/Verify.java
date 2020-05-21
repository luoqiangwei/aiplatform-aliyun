package cn.oveay.aiplatform.utils.security;

import cn.oveay.aiplatform.utils.redis.JedisUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


@Service
public class Verify {
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
     * 检查电话验证码
     * @param userId
     * @param key
     * @return
     */
    public synchronized boolean checkVerify(String userId, String key){
        return String.valueOf(JedisUtils.get(userId + "P")).equals(key);
    }
    public synchronized void addVerify(String userId, String key){
        JedisUtils.set(userId + "P", key, timeout);
    }
}
