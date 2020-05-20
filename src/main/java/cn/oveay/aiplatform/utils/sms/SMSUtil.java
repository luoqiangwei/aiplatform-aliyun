package cn.oveay.aiplatform.utils.sms;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.Random;

/**
 * @author OVEA(Qiangwei Luo)
 * created on : 2020/5/20 17:23
 * 文件说明：短信服务
 */
@Slf4j
public class SMSUtil {

    // 短信应用 SDK AppID
    @Value("${appid}")
    private static int appid; // SDK AppID 以1400开头
    // 短信应用 SDK AppKey
    @Value("${appkey}")
    private static String appkey;
    // 需要发送短信的手机号码
//    private static String[] phoneNumbers = {"21212313123", "12345678902", "12345678903"};
    // 短信模板 ID，需要在短信应用中申请
    @Value("${templateId}")
    private static int templateId;
    // 签名
    @Value("${smsSign}")
    private static String smsSign;

    private static Random random = new Random();

    public static String sendMsg(String phone){
        SmsSingleSender smsSingleSender = new SmsSingleSender(appid, appkey);
        // 分别是模板中 {1} 和 {2}
        String[] params = new String[]{String.valueOf(random.nextInt(9999)), "10"};
        try {
            SmsSingleSenderResult result = smsSingleSender.sendWithParam("86", phone, templateId, params, smsSign, "", "");
        } catch (HTTPException e) {
            // HTTP 响应码错误
            log.error("SMSUtil.sendMsg: " + e.getMessage());
        } catch (IOException e) {
            // 网络 IO 错误
            log.error("SMSUtil.sendMsg: " + e.getMessage());
        }
        return params[0];
    }
}
