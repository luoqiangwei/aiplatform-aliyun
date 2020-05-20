package cn.oveay.aiplatform.service;

import cn.oveay.aiplatform.basebean.CarEnum;
import cn.oveay.aiplatform.basebean.CarIssueEnum;
import cn.oveay.aiplatform.basebean.ExpressionEnum;
import com.aliyun.facebody20191230.models.RecognizeExpressionAdvanceRequest;
import com.aliyun.facebody20191230.models.RecognizeExpressionResponse;
import com.aliyun.imagerecog20190930.models.RecognizeSceneAdvanceRequest;
import com.aliyun.imagerecog20190930.models.RecognizeSceneResponse;
import com.aliyun.imagerecog20190930.models.RecognizeVehicleTypeAdvanceRequest;
import com.aliyun.imagerecog20190930.models.RecognizeVehicleTypeResponse;
import com.aliyun.tearpc.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author OVEA(Qiangwei Luo)
 * created on : 2020/5/18 20:47
 * 文件说明：
 */
@Slf4j
@Service
public class VisionService {

    @Value("${aliapi.accessKeyId}")
    private String accessKeyId;
    @Value("${aliapi.accessKeySecret}")
    private String accessKeySecret;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    static Random random = new Random();

    private com.aliyun.imagerecog20190930.Client getImageRecogClient() throws Exception {
        Config config = new Config();
        config.endpointType = "access_key";
        config.regionId = "cn-shanghai";
        config.accessKeyId = accessKeyId;
        config.accessKeySecret = accessKeySecret;
        config.endpoint = "imagerecog.cn-shanghai.aliyuncs.com";
        config.protocol = "http";
        return new com.aliyun.imagerecog20190930.Client(config);
    }

    private com.aliyun.facebody20191230.Client getFaceBodyClient() throws Exception {
        Config config = new Config();
        config.endpointType = "access_key";
        config.regionId = "cn-shanghai";
        config.accessKeyId = accessKeyId;
        config.accessKeySecret = accessKeySecret;
        config.endpoint = "facebody.cn-shanghai.aliyuncs.com";
        config.protocol = "http";
        return new com.aliyun.facebody20191230.Client(config);
    }

    public static InputStream getImageStream(String url) throws IOException {
        URLConnection connection = new URL(url).openConnection();
        connection.setConnectTimeout(10 * 1000);
        connection.setReadTimeout(10 * 1000);
        return connection.getInputStream();
    }

    public List<String> recognizeScene(InputStream inputStream) {
        RecognizeSceneAdvanceRequest request = new RecognizeSceneAdvanceRequest();
        request.imageURLObject = inputStream;

        List<String> labels = new ArrayList<>();

        try {
            com.aliyun.imagerecog20190930.Client client = getImageRecogClient();
            RecognizeSceneResponse response = response = client.recognizeSceneAdvance(request, new RuntimeOptions());
            for (RecognizeSceneResponse.RecognizeSceneResponseDataTags tag : response.data.tags) {
                labels.add(tag.value);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return labels;
    }

    public List<String> recognizeExpression(InputStream inputStream) {
        RecognizeExpressionAdvanceRequest request = new RecognizeExpressionAdvanceRequest();
        request.imageURLObject = inputStream;

        List<String> labels = new ArrayList<>();
        try {
            com.aliyun.facebody20191230.Client client = getFaceBodyClient();
            RecognizeExpressionResponse response = client.recognizeExpressionAdvance(request, new RuntimeOptions());
            for (RecognizeExpressionResponse.RecognizeExpressionResponseDataElements element : response.data.elements) {
                labels.add(ExpressionEnum.getNameByEnName(element.expression));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return labels;
    }

    public Map<String, String> recognizeCar(InputStream inputStream) {
        RecognizeVehicleTypeAdvanceRequest request = new RecognizeVehicleTypeAdvanceRequest();
        request.imageURLObject = inputStream;
        
        String carType = "";

        List<String> labels = new ArrayList<>();
        try {
            com.aliyun.imagerecog20190930.Client client = getImageRecogClient();
            RecognizeVehicleTypeResponse response = response = client.recognizeVehicleTypeAdvance(request, new RuntimeOptions());
            double maxScore = 0;
            for (RecognizeVehicleTypeResponse.RecognizeVehicleTypeResponseDataElements element : response.data.elements) {
                if (element.score < 0.3) {
                    continue;
                }
                if (element.score > maxScore) {
                    carType = element.name;
                    maxScore = element.score;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        Map<String, String> result = new HashMap<>();
        BigDecimal insurers = CarIssueEnum.getInsurersByName(carType);
        insurers = insurers.add(BigDecimal.valueOf(random.nextInt(Integer.parseInt(insurers.toString()))));
        BigDecimal aoc = CarIssueEnum.getAOCByName(carType);
        aoc = aoc.add(BigDecimal.valueOf(random.nextInt(Integer.parseInt(aoc.toString()))));

        result.put("carType", CarEnum.getNameByEnName(carType));
        result.put("insurers", insurers.toString());
        result.put("aoc", aoc.toString());
        result.put("id", randomId());
        result.put("startTime", dateFormat.format(new Date()));
        result.put("endTime", dateFormat.format(new Date(new Date().getTime() + 60 * 60 * 24 * 365)));
        return result;
    }

    public String randomId() {
        StringBuilder buffer = new StringBuilder("68000");
        for (int i = 0; i < 7; i++) {
            buffer.append(random.nextInt(10));
        }
        return buffer.toString();
    }

    public String randomId(int prefix, int n) {
        StringBuilder buffer = new StringBuilder(prefix);
        for (int i = 0; i < n; i++) {
            buffer.append(random.nextInt(10));
        }
        return buffer.toString();
    }
}
