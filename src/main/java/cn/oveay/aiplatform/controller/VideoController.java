package cn.oveay.aiplatform.controller;

import cn.oveay.aiplatform.service.VideoService;
import lombok.extern.slf4j.Slf4j;
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
 * created on : 2020/5/19 20:01
 * 文件说明：
 */
@Controller
@Slf4j
@RequestMapping("/video")
public class VideoController {
    @Value("${vedio.upload.path}")
    private String uploadDir;

    @Autowired
    private VideoService videoService;

    private List<String> resultUrls = new ArrayList<>();

    @RequestMapping("/index")
    public String index(Model model) {
        // 说明只上传了身份证的一面
        if (resultUrls.size() > 0) {
            model.addAttribute("video", resultUrls.get(resultUrls.size() - 1));
        }
        return "lthvideo";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("userVideo") MultipartFile userVideo, RedirectAttributes redirectAttributes) {
        if (userVideo.isEmpty()) {
            redirectAttributes.addFlashAttribute("messages", "请选择一个文件进行上传。");
            return "redirect:/video/index";
        }
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
            if (!userVideo.isEmpty()) {
                String filename = saveFile(userVideo);
//                企图处理Bilibili台标失败，不知道为什么，加了限定范围后就会处理失败
//                resultUrls.add(videoService.clearVideoFlag(uploadDir + filename, 0.24, 0.24, 0, 0.74));
//                效果不太好……
//                resultUrls.add(videoService.clearVideoFlag(uploadDir + filename));
                resultUrls.add(videoService.superResolveFlag(uploadDir + filename));
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessages += e.getMessage() + "\n";
        }
        if (StringUtils.isNoneBlank(errorMessages)) {
            redirectAttributes.addFlashAttribute("messages", errorMessages);
        }
        return "redirect:/video/index";
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
