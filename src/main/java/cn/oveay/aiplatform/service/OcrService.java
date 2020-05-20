package cn.oveay.aiplatform.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.aliyun.ocr20191230.Client;
import com.aliyun.ocr20191230.models.*;
import com.aliyun.tearpc.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * @author OVEA(Qiangwei Luo)
 * created on : 2020/5/18 13:04
 * 文件说明：文字识别服务类
 */
@Service
public class OcrService {

    private Client orcClient;
    private RuntimeOptions runtimeOptions;

    @Value("${aliapi.accessKeyId}")
    private String accessKeyId;
    @Value("${aliapi.accessKeySecret}")
    private String accessKeySecret;

    /**
     * PostConstruct注解的方法，在注入的时候，会自动执行一次这个方法
     * @throws Exception 异常
     */
    @PostConstruct
    private void init() throws Exception {
        Config config = new Config();
        config.endpointType = "access_key";
        config.regionId = "cn-shanghai";
        config.accessKeyId = accessKeyId;
        config.accessKeySecret = accessKeySecret;
        config.endpoint = "ocr.cn-shanghai.aliyuncs.com";

        orcClient = new Client(config);
        runtimeOptions = new RuntimeOptions();
    }

    /**
     * 调用阿里云的API
     * @param fielpath 图片文件路径
     * @param side 身份证正面还是反面
     * @return 返回解析的数据集
     * @throws Exception 解析异常
     */
    public Map<String, String> recognizeIdCard(String fielpath, String side) throws Exception {
        RecognizeIdentityCardAdvanceRequest request = new RecognizeIdentityCardAdvanceRequest();
        request.imageURLObject = Files.newInputStream(Paths.get(fielpath));
        request.side = side;
        RecognizeIdentityCardResponse response = orcClient.recognizeIdentityCardAdvance(request, runtimeOptions);
        return "face".equals(side) ?
                JSON.parseObject(JSON.toJSONString(response.data.frontResult), new TypeReference<Map<String, String>>(){})
                : JSON.parseObject(JSON.toJSONString(response.data.backResult), new TypeReference<Map<String, String>>(){});
    }

    public Map<String, String> recognizeDriveCard(String fielpath) throws Exception {
        RecognizeDriverLicenseAdvanceRequest request = new RecognizeDriverLicenseAdvanceRequest();
        request.imageURLObject = Files.newInputStream(Paths.get(fielpath));
        request.side = "face";
        RecognizeDriverLicenseResponse response = orcClient.recognizeDriverLicenseAdvance(request, runtimeOptions);
        return JSON.parseObject(JSON.toJSONString(response.data.faceResult), new TypeReference<Map<String, String>>(){});
    }

    public Map<String, String> recognizeDrivingCard(String fielpath) throws Exception {
        RecognizeDrivingLicenseAdvanceRequest request = new RecognizeDrivingLicenseAdvanceRequest();
        request.imageURLObject = Files.newInputStream(Paths.get(fielpath));
        request.side = "face";
        RecognizeDrivingLicenseResponse response = orcClient.recognizeDrivingLicenseAdvance(request, runtimeOptions);
        return JSON.parseObject(JSON.toJSONString(response.data.faceResult), new TypeReference<Map<String, String>>(){});
    }

}
