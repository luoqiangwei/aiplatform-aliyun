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
import org.springframework.web.bind.annotation.ResponseBody;
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
 * created on : 2020/5/18 13:04
 * 文件说明：主控制流
 */
@Controller
public class MainController {
    @RequestMapping("/index")
    public String index(Model model) {
        return "index";
    }

    @RequestMapping("/album")
    public String album(Model model) {
        return "album";
    }

    /**
     * 只是用来测试Spring boot的
     * @return ResponseBody表示返回的字符在网页上直接显示
     */
    @RequestMapping("/test")
    @ResponseBody
    public String test() {
        return "test";
    }
}
