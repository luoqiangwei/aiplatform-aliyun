package cn.oveay.aiplatform.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author OVEA(Qiangwei Luo)
 * created on : 2020/5/18 20:39
 * 文件说明：资源服务类
 */
@Slf4j
@Service
public class ResourceService {
    private String scene;

    @Value("${storagePath}")
    private String storagePath;

    @Value("${imagePath}")
    private String imagePath;

    static final String fileName = "data.json";
    private LabelModel labelModel;

    @Autowired
    private VisionService visionService;

    @PostConstruct
    public void loadMetaData() {
        log.info("laod");
        try (InputStream inputStream = new FileInputStream(storagePath + fileName)) {
            labelModel = JSONObject.parseObject(inputStream, LabelModel.class);
        } catch (Exception e) {
            log.error(e.toString());
        }
        if (labelModel == null) {
            labelModel = new LabelModel();
        }
    }

    @PreDestroy
    public void saveMetaData() {
        log.info("Save");
        try (OutputStream outputStream = new FileOutputStream(storagePath + fileName)) {
            outputStream.write(JSON.toJSONBytes(labelModel));
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.toString());
        }
    }

    public List<String> getPhotosByCate(String cate) {
        return getAccessPath(labelModel.getAllImg());
    }

    public Object getAllCates() {
        return labelModel.cateMap;
    }

    public List<String> getAllPhotos() {
        return getAccessPath(labelModel.getAllImg());
    }

    private List<String> getAccessPath(List<String> imgs) {
        List<String> result = new ArrayList<>();
        imgs.stream().forEach(img -> {
            result.add(String.format("/img/%s", img));
        });
        return result;
    }

    public void saveAndRecognizeImage(String fileName, ByteArrayInputStream inputStream) {
        log.info("saveImage");
        try {
            // 识别场景
            inputStream.reset();
            inputStream.mark(0);
            List<String> scenes = visionService.recognizeScene(inputStream);
            labelModel.addImg(LabelModel.SCENE, fileName, scenes);

            inputStream.reset();
            inputStream.mark(0);
            List<String> expressions = visionService.recognizeExpression(inputStream);
            labelModel.addImg(LabelModel.EXPRESSION, fileName, expressions);

            inputStream.reset();
            inputStream.mark(0);

            OutputStream outputStream = new FileOutputStream(imagePath + fileName);
            IOUtils.copy(inputStream, outputStream);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    public List<String> getPhotosByCateAndLabel(String cate, String tag) {
        return getAccessPath(labelModel.getImgByCateAndLabel(cate, tag));
    }

    @Data
    static class LabelModel {
        static final String SCENE = "scene";
        static final String EXPRESSION = "expression";
        static final String STYLE = "style";
        // 不同场景
        private Map<String, Set<String>> sceneMap = new HashMap<>();
        // 不同表情
        private Map<String, Set<String>> expressionMap = new HashMap<>();

//        不同风格
        private Map<String, Set<String>> styleMap = new HashMap<>();
        private Map<String, Set<String>> cateMap = new HashMap<>();

        // key - 图片地址
        // value - {[scene|expression|style]}_{label}
        private Map<String, Set<String>> imgLabels = new HashMap<>();

        public List<String> getAllImg() {
            List<String> result = new ArrayList<>();
            imgLabels.forEach((k, v) -> {
                result.add(k);
            });
            return result.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        }

        public List<String> getImgByCate(String cate) {
            Map<String, Set<String>> data = new HashMap<>();
            switch (cate) {
                case SCENE:
                    data = sceneMap;
                    break;
                case EXPRESSION:
                    data = expressionMap;
                    break;
                case STYLE:
                    data = styleMap;
                    break;
            }
            Set<String> result = new HashSet<>();
            data.forEach((k, d) -> {
                result.addAll(d);
            });
            return result.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        }

        public List<String> getImgByCateAndLabel(String cate, String label) {
            String key = String.format("%s%_s", cate, label);

            Set<String> result = new HashSet<>();
            switch (cate) {
                case SCENE:
                    result = sceneMap.get(key);
                    break;
                case EXPRESSION:
                    result = expressionMap.get(key);
                    break;
                case STYLE:
                    result = styleMap.get(key);
                    break;
            }
            return result.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        }

        public void addImg(String cate, String img, List<String> labels) {
            for (String label : labels) {
                String item = String.format("%s_%s", cate, label);
                Set<String> imgSet = imgLabels.getOrDefault(img, new HashSet<>());
                imgSet.add(item);
                imgLabels.put(img, imgSet);
                switch (cate) {
                    case SCENE: {
                        Set<String> sceneSet = sceneMap.getOrDefault(item, new HashSet<>());
                        sceneSet.add(img);
                        sceneMap.put(item, sceneSet);
                        Set<String> cateSet = cateMap.getOrDefault(cate, new HashSet<>());
                        cateSet.add(label);
                        cateMap.put(cate, cateSet);
                        break;
                    }
                    case EXPRESSION: {
                        Set<String> expressionSet = expressionMap.getOrDefault(item, new HashSet<>());
                        expressionSet.add(img);
                        expressionMap.put(item, expressionSet);
                        Set<String> cateSet = cateMap.getOrDefault(cate, new HashSet<>());
                        cateSet.add(label);
                        cateMap.put(cate, cateSet);
                        break;
                    }
                    case STYLE: {
                        Set<String> styleSet = styleMap.getOrDefault(item, new HashSet<>());
                        styleSet.add(img);
                        styleMap.put(item, styleSet);
                        Set<String> cateSet = cateMap.getOrDefault(cate, new HashSet<>());
                        cateSet.add(label);
                        cateMap.put(cate, cateSet);
                        break;
                    }
                }
            }
        }

        public void remove(String img) {
            Set<String> labels = imgLabels.remove(img);
            labels.stream().forEach(label -> {
                String[] str = label.split("_");
                String cate = str[0];
                String labelKey = str[1];
                switch (cate) {
                    case SCENE: {
                        sceneMap.get(labelKey).remove(img);
                        break;
                    }
                    case EXPRESSION: {
                        expressionMap.get(labelKey).remove(img);
                        break;
                    }
                    case STYLE: {
                        styleMap.get(labelKey).remove(img);
                        break;
                    }
                }
            });
        }
    }
}
