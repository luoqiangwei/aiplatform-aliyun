package cn.oveay.aiplatform.controller;

import cn.oveay.aiplatform.service.OcrService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author OVEA(Qiangwei Luo)
 * created on : 2020/5/18 16:10
 * 文件说明：class1的实验内容
 */
@Controller
@RequestMapping("/idcard")
public class IDCardController {
    // Value是将配置文件中，file.uplaod.path对应的值注入到uploadDir中
    @Value("${file.uplaod.path}")
    private String uploadDir;
    // Autowired会自动创建一个OcrService实例并注入到ocrService
    @Autowired
    private OcrService ocrService;

//    private List<String> faceImages = new ArrayList<>();

    /**
     * 用于跳转到首先前的处理，RequestMapping表示接受任意这个请求的路径
     * @param model 这个是Spring中，用于向视图传递数据的一种数据结构
     * @return index表示跳转到index.html
     */
    @RequestMapping("/index")
    public String index(Model model, HttpServletRequest request) {
//        index是使用功能时一定会访问的，所以在这里初始化用户的容器
        List<String> faceImages = (List<String>) request.getSession().getAttribute("faceImages");
        if (faceImages == null) {
            faceImages = new ArrayList<>();
            request.getSession().setAttribute("faceImages", faceImages);
        }
        List<String> backImages = (List<String>) request.getSession().getAttribute("backImages");
        if (backImages == null) {
            backImages = new ArrayList<>();
            request.getSession().setAttribute("backImages", backImages);
        }
        List<Map<String, String>> faceResults = (List<Map<String, String>>) request.getSession().getAttribute("faceResults");
        if (faceResults == null) {
            faceResults = new ArrayList<>();
            request.getSession().setAttribute("faceResults", faceResults);
        }
        List<Map<String, String>> backResults = (List<Map<String, String>>) request.getSession().getAttribute("backResults");
        if (backResults == null) {
            backResults = new ArrayList<>();
            request.getSession().setAttribute("backResults", backResults);
        }

        // 说明只上传了身份证的一面
        if (faceImages.size() != backImages.size()) {
            faceImages.clear();
            backImages.clear();
            faceResults.clear();
            backResults.clear();
        }
        // 保持识别结果，用于在视图显示
        if (!CollectionUtils.isEmpty(faceImages) && faceImages.size() == backImages.size()) {
            model.addAttribute("faceImage", faceImages.get(faceImages.size() - 1));
            model.addAttribute("faceResult", faceResults.get(faceResults.size() - 1));
            model.addAttribute("backImage", backImages.get(backImages.size() - 1));
            model.addAttribute("backResult", backResults.get(backResults.size() - 1));
        }
        return "idcard";
    }

    /**
     * 这个方法仅能由POST请求访问，让用户上传身份证信息
     * @param face 人像面
     * @param back 背面
     * @param redirectAttributes 用于重定向的一个数据结构，可以保留一些信息到视图
     * @return 重定向回index.html
     */
    @PostMapping("/upload")
    public String upload(@RequestParam("face") MultipartFile face, @RequestParam("back") MultipartFile back,
                         RedirectAttributes redirectAttributes, HttpServletRequest request) {
        if (face.isEmpty() || back.isEmpty()) {
            redirectAttributes.addFlashAttribute("messages", "请选择一个文件进行上传。");
            return "redirect:/idcard/index";
        }
        List<String> faceImages = (List<String>) request.getSession().getAttribute("faceImages");
        List<String> backImages = (List<String>) request.getSession().getAttribute("backImages");
        List<Map<String, String>> faceResults = (List<Map<String, String>>) request.getSession().getAttribute("faceResults");
        List<Map<String, String>> backResults = (List<Map<String, String>>) request.getSession().getAttribute("backResults");
        String errorMessages = null;
        Path dir = Paths.get(uploadDir);
        if (!Files.exists(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                e.printStackTrace();
                errorMessages += e.getMessage() + "\n";
            }
        }
        try {
            if (!face.isEmpty()) {
                String filename = saveFile(face);
                Map<String, String> faceResult = ocrService.recognizeIdCard(uploadDir + filename, "face");
                faceImages.add("/images/idcard/" + filename);
                faceResults.add(faceResult);
            }
            if (!back.isEmpty()) {
                String filename = saveFile(back);
                Map<String, String> backResult = ocrService.recognizeIdCard(uploadDir + filename, "back");
                backImages.add("/images/idcard/" + filename);
                backResults.add(backResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessages += e.getMessage() + "\n";
        }
        if (StringUtils.isNoneBlank(errorMessages)) {
            redirectAttributes.addFlashAttribute("messages", errorMessages);
        }
        return "redirect:/idcard/index";
    }

    /**
     * 将上传的文件重命名
     * @param file 上传的文件名
     * @return 返回重命名后的文件
     */
    public String saveFile(MultipartFile file) {
        String filename = UUID.randomUUID().toString() + "."
                + StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
        Path path = Paths.get(uploadDir + filename);
        try {
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            return null;
        }
        return filename;
    }
}
