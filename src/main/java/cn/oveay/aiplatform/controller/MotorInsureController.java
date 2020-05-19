package cn.oveay.aiplatform.controller;

import cn.oveay.aiplatform.service.OcrService;
import cn.oveay.aiplatform.service.VisionService;
import cn.oveay.aiplatform.utils.MD5;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
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
 * created on : 2020/5/19 11:58
 * 文件说明：
 */
@Slf4j
@Controller
@RequestMapping("/issue")
public class MotorInsureController {

    @Value("${file.uplaod.carpath}")
    private String uploadDir;

    @Autowired
    private OcrService ocrService;
    @Autowired
    private VisionService visionService;

    private List<String> carImages = new ArrayList<>();
    private List<Map<String, String>> carResults = new ArrayList<>();
    private List<String> driveImages = new ArrayList<>();
    private List<Map<String, String>> driveResults = new ArrayList<>();

    static int issueNo = 1;

    @RequestMapping("/index")
    public String index(Model model) {
        if (carImages.size() != driveImages.size()) {
            carImages.clear();
            carResults.clear();
            driveImages.clear();
            driveResults.clear();
        }
        if (carImages.size() > 0) {
            model.addAttribute("carImage", carImages.get(carImages.size() - 1));
            model.addAttribute("carResult", carResults.get(carResults.size() - 1));
            model.addAttribute("driveImage", driveImages.get(driveImages.size() - 1));
            model.addAttribute("driveResult", driveResults.get(driveResults.size() - 1));
            // 此处开始生产Issue 半张A4纸
            String fileName = "" + issueNo++ + ".jpg";

            BufferedImage image = new BufferedImage(1200,1040, BufferedImage.TYPE_INT_RGB);
            Graphics2D brush = (Graphics2D) image.getGraphics();
            brush.setColor(Color.WHITE);
            brush.fillRect(0, 0, 1200, 1040);

            brush.setColor(new Color(0xECECEC));
            brush.setFont(new Font("黑体", Font.BOLD | Font.ITALIC,200));
            brush.drawString("二营保险有限公司", 0, 100);
            brush.drawString("二营保险有限公司", -100, 400);
            brush.drawString("二营保险有限公司", -200, 700);
            brush.drawString("二营保险有限公司", -300, 1000);

            brush.setColor(Color.RED);
            brush.setStroke(new BasicStroke(10));
            brush.drawRect(4,4,1192,1032);
            brush.setStroke(new BasicStroke(20));
            brush.drawRect(40,40,1120,960);

            brush.setColor(new Color(0x00808D));
            brush.setFont(new Font("黑体", Font.BOLD,60));
            brush.drawString("二营保险有限公司", 350, 140);
            brush.setColor(new Color(0x08005F));
            brush.setFont(new Font("宋体", Font.BOLD,30));
            brush.drawString("保险单", 500, 200);

            brush.setColor(Color.BLACK);
            brush.setFont(new Font("宋体", Font.BOLD,25));

            Map<String, String> lastDriveResult = driveResults.get(driveResults.size() - 1);
            Map<String, String> lastCarResult = carResults.get(carResults.size() - 1);

            brush.drawString("姓名： " + lastDriveResult.get("name"), 200, 300);
            brush.drawString("序列号： " + lastDriveResult.get("licenseNumber"), 200, 400);
            brush.drawString("驾照生效时间： " + lastDriveResult.get("issueDate"), 200, 500);
            brush.drawString("驾照到期时间： " + lastDriveResult.get("endDate"), 200, 600);
            brush.drawString("住址： " + lastDriveResult.get("address"), 200, 700);
            brush.drawString("性别： " + lastDriveResult.get("gender"), 200, 800);
            brush.drawString("准驾车型： " + lastDriveResult.get("vehicleType"), 200, 900);

            brush.drawString("车型： " + lastCarResult.get("carType"), 600, 300);
            brush.drawString("费用： " + lastCarResult.get("insurers") + "元", 600, 400);
            brush.drawString("理赔金额： " + lastCarResult.get("aoc") + "元", 600, 500);
            brush.drawString("签发： " + "二营保险有限公司", 600, 600);
            brush.drawString("保单有效期： " + "2020-05-11 ~ 2025-05-10", 600, 700);
            brush.drawString("保险单号： " + lastCarResult.get("id"), 600, 800);


            try (FileOutputStream fileOutputStream = new FileOutputStream(uploadDir + fileName)) {
                ImageIO.write(image, "jpg", fileOutputStream);
            } catch (IOException e) {
                log.error(e.getMessage());
            }

            model.addAttribute("issue", "/images/car/" + fileName);
        }
        return "motorinsure";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("car") MultipartFile car, @RequestParam("drive") MultipartFile drive,
                         RedirectAttributes redirectAttributes) {
        if (car.isEmpty() || drive.isEmpty()) {
            redirectAttributes.addFlashAttribute("messages", "请选择一个文件进行上传。");
            return "redirect:/issue/index";
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
            if (!car.isEmpty()) {
                String filename = saveFile(car);
                Map<String, String> carResult = visionService.recognizeCar(Files.newInputStream(Paths.get(uploadDir + filename)));
                carImages.add("/images/car/" + filename);
                carResults.add(carResult);
            }
            if (!drive.isEmpty()) {
                String filename = saveFile(drive);
                Map<String, String> carResult = ocrService.recognizeDriveCard(uploadDir + filename);
                driveImages.add("/images/car/" + filename);
                driveResults.add(carResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessages += e.getMessage() + "\n";
        }
        if (StringUtils.isNoneBlank(errorMessages)) {
            redirectAttributes.addFlashAttribute("messages", errorMessages);
        }
        return "redirect:/issue/index";
    }

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
