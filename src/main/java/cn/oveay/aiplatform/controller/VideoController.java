package cn.oveay.aiplatform.controller;

import cn.oveay.aiplatform.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @RequestMapping("/index")
    public String index(Model model, HttpServletRequest request) {
        List<String> lthResultUrls = (List<String>) request.getSession().getAttribute("lthResultUrls");
        if (lthResultUrls == null) {
            lthResultUrls = new ArrayList<>();
            request.getSession().setAttribute("lthResultUrls", lthResultUrls);
        }
        // 说明只上传了身份证的一面
        if (lthResultUrls.size() > 0) {
            model.addAttribute("video", lthResultUrls.get(lthResultUrls.size() - 1));
        }
        return "lthvideo";
    }

    @RequestMapping("/clear")
    public String clear(Model model, HttpServletRequest request) {
        List<String> clearResultUrls = (List<String>) request.getSession().getAttribute("clearResultUrls");
        if (clearResultUrls == null) {
            clearResultUrls = new ArrayList<>();
            request.getSession().setAttribute("clearResultUrls", clearResultUrls);
        }
        // 说明只上传了身份证的一面
        if (clearResultUrls.size() > 0) {
            model.addAttribute("video", clearResultUrls.get(clearResultUrls.size() - 1));
        }
        return "videoclear";
    }

    @PostMapping("/uploadclear")
    public String uploadclear(@RequestParam("userVideo") MultipartFile userVideo, RedirectAttributes redirectAttributes,
                              HttpServletRequest request) {
        if (userVideo.isEmpty()) {
            redirectAttributes.addFlashAttribute("messages", "请选择一个文件进行上传。");
            return "redirect:/video/index";
        }
        List<String> clearResultUrls = (List<String>) request.getSession().getAttribute("clearResultUrls");
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
                clearResultUrls.add(videoService.clearVideoFlag(uploadDir + filename));
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessages += e.getMessage() + "\n";
        }
        if (StringUtils.isNoneBlank(errorMessages)) {
            redirectAttributes.addFlashAttribute("messages", errorMessages);
        }
        return "redirect:/video/clear";
    }

    @PostMapping("/uploadlth")
    public String uploadlth(@RequestParam("userVideo") MultipartFile userVideo, RedirectAttributes redirectAttributes,
                            HttpServletRequest request) {
        if (userVideo.isEmpty()) {
            redirectAttributes.addFlashAttribute("messages", "请选择一个文件进行上传。");
            return "redirect:/video/index";
        }
        List<String> lthResultUrls = (List<String>) request.getSession().getAttribute("lthResultUrls");
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
                lthResultUrls.add(videoService.superResolveFlag(uploadDir + filename));
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
