package cn.oveay.aiplatform.service;

import com.alibaba.fastjson.JSON;
import com.aliyun.tearpc.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.aliyun.videoenhan20200320.Client;
import com.aliyun.videoenhan20200320.models.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author OVEA(Qiangwei Luo)
 * created on : 2020/5/19 16:52
 * 人脸人体：pip install aliyun-python-sdk-facebody
 * 文字识别：pip install aliyun-python-sdk-ocr
 * 商品理解：pip install aliyun-python-sdk-goodstech
 * 内容安全：pip install aliyun-python-sdk-imageaudit
 * 图像识别：pip install aliyun-python-sdk-imagerecog
 * 图像生产：pip install aliyun-python-sdk-imageenhan
 * 分割抠图：pip install aliyun-python-sdk-imageseg
 * 目标检测：pip install aliyun-python-sdk-objectdet
 * 图像分析处理：pip install aliyun-python-sdk-imageprocess
 * 视觉搜索：pip install aliyun-python-sdk-imgsearch
 * 视频理解：pip install aliyun-python-sdk-videorecog
 * 视频生产：pip install aliyun-python-sdk-videoenhan
 * 文件说明：超清视频
 */
@Slf4j
@Service
public class VideoService {
    private Client videoClient;
    private RuntimeOptions runtimeOptions;

    @Value("${aliapi.accessKeyId}")
    private String accessKeyId;
    @Value("${aliapi.accessKeySecret}")
    private String accessKeySecret;

    @PostConstruct
    private void init() throws Exception {
        Config config = new Config();
        config.endpointType = "access_key";
        config.regionId = "cn-shanghai";
        config.accessKeyId = accessKeyId;
        config.accessKeySecret = accessKeySecret;
        config.endpoint = "videoenhan.cn-shanghai.aliyuncs.com";

        videoClient = new Client(config);
        runtimeOptions = new RuntimeOptions();
    }

    /**
     *
     * @param filepath 识破所在位置
     * @param high 0~1
     * @param width 0~1
     * @param x 0~1
     * @param y 0~1
     * @return
     * @throws Exception
     */
    public String clearVideoFlag(String filepath, double high, double width, double x, double y) throws Exception {
        EraseVideoLogoAdvanceRequest request = new EraseVideoLogoAdvanceRequest();
        request.videoUrlObject = Files.newInputStream(Paths.get(filepath));
        EraseVideoLogoAdvanceRequest.EraseVideoLogoAdvanceRequestBoxes boxes = new EraseVideoLogoAdvanceRequest.EraseVideoLogoAdvanceRequestBoxes();
        boxes.h = high;
        boxes.w = width;
        boxes.x = x;
        boxes.y = y;
        List<EraseVideoLogoAdvanceRequest.EraseVideoLogoAdvanceRequestBoxes> boxesList = new ArrayList<>();
        boxesList.add(boxes);
        request.boxes = boxesList;
        EraseVideoLogoResponse response = videoClient.eraseVideoLogoAdvance(request, runtimeOptions);
        log.info("clearVideoFlag  " + response.requestId);
        return getFinalResult(response.requestId);
    }

    /**
     * 不限制范围地进行视频标识擦除
     * @param filepath
     * @return
     * @throws Exception
     */
    public String clearVideoFlag(String filepath) throws Exception {
        EraseVideoLogoAdvanceRequest request = new EraseVideoLogoAdvanceRequest();
        request.videoUrlObject = Files.newInputStream(Paths.get(filepath));
        EraseVideoLogoResponse response = videoClient.eraseVideoLogoAdvance(request, runtimeOptions);
        log.info("clearVideoFlag C  " + response.requestId);
        return getFinalResult(response.requestId);
    }

    /**
     * bitRate是1-100，意思是编码率放大多少倍，那当然是最大啦~ U•ェ•*U
     * @param filepath 视频所在位置
     * @return
     * @throws Exception
     */
    public String superResolveFlag(String filepath) throws Exception {
        SuperResolveVideoAdvanceRequest request = new SuperResolveVideoAdvanceRequest();
        request.videoUrlObject = Files.newInputStream(Paths.get(filepath));
        request.bitRate = 4;
        SuperResolveVideoResponse response = videoClient.superResolveVideoAdvance(request, runtimeOptions);
        return getFinalResult(response.requestId);
    }



    public String getFinalResult(String jobId) throws Exception {
        GetAsyncJobResultRequest request = new GetAsyncJobResultRequest();
        request.jobId = jobId;
        GetAsyncJobResultResponse response = videoClient.getAsyncJobResult(request, runtimeOptions);
        while (response.data.result == null && (response.data.status.equals("QUEUING") || (response.data.status.equals("PROCESSING")))) {
            response = videoClient.getAsyncJobResult(request, runtimeOptions);
            Thread.sleep(10000);
        }
        log.info("getFinalResult " + response.data.result);
        if (response.data.result != null) {
            return JSON.parseObject(response.data.result, Result.class).getVideoUrl();
        } else {
            return null;
        }
    }

    @Data
    static class Result {
        private String VideoUrl;
    }

}
