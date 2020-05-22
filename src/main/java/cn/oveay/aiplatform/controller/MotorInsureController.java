package cn.oveay.aiplatform.controller;

import cn.oveay.aiplatform.service.OcrService;
import cn.oveay.aiplatform.service.VisionService;
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

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * @author OVEA(Qiangwei Luo)
 * created on : 2020/5/19 11:58
 * 文件说明：
 */
@Slf4j
@Controller
@RequestMapping("/issue")
public class MotorInsureController {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");

    @Value("${file.uplaod.carpath}")
    private String uploadDir;

    private Random random = new Random();

    @Autowired
    private OcrService ocrService;
    @Autowired
    private VisionService visionService;

    static int issueNo = 1;

    @RequestMapping("/index")
    public String index(Model model, HttpServletRequest request) {
        List<String> carImages = (List<String>) request.getSession().getAttribute("carImages");
        if (carImages == null) {
            carImages = new ArrayList<>();
            request.getSession().setAttribute("carImages", carImages);
        }
        List<Map<String, String>> carResults = (List<Map<String, String>>) request.getSession().getAttribute("carResults");
        if (carResults == null) {
            carResults = new ArrayList<>();
            request.getSession().setAttribute("carResults", carResults);
        }
        List<String> driveImages = (List<String>) request.getSession().getAttribute("driveImages");
        if (driveImages == null) {
            driveImages = new ArrayList<>();
            request.getSession().setAttribute("driveImages", driveImages);
        }
        List<Map<String, String>> driveResults = (List<Map<String, String>>) request.getSession().getAttribute("driveResults");
        if (driveResults == null) {
            driveResults = new ArrayList<>();
            request.getSession().setAttribute("driveResults", driveResults);
        }
        List<String>  drivingImages = (List<String>) request.getSession().getAttribute("drivingImages");
        if (drivingImages == null) {
            drivingImages = new ArrayList<>();
            request.getSession().setAttribute("drivingImages", drivingImages);
        }
        List<Map<String, String>> drivingResults = (List<Map<String, String>>) request.getSession().getAttribute("drivingResults");
        if (drivingResults == null) {
            drivingResults = new ArrayList<>();
            request.getSession().setAttribute("drivingResults", drivingResults);
        }

        if (carImages.size() != driveImages.size() && drivingImages.size() != driveImages.size()) {
            carImages.clear();
            carResults.clear();
            driveImages.clear();
            driveResults.clear();
            drivingImages.clear();
            drivingResults.clear();
        }
        if (carImages.size() > 0) {
            model.addAttribute("carImage", carImages.get(carImages.size() - 1));
            model.addAttribute("carResult", carResults.get(carResults.size() - 1));
            model.addAttribute("driveImage", driveImages.get(driveImages.size() - 1));
            model.addAttribute("driveResult", driveResults.get(driveResults.size() - 1));
            model.addAttribute("drivingImage", drivingImages.get(drivingImages.size() - 1));
            model.addAttribute("drivingResult", drivingResults.get(drivingResults.size() - 1));
            // 此处开始生产保单
            String fileName = "" + issueNo++ + ".jpg";

            Map<String, String> lastDriveResult = driveResults.get(driveResults.size() - 1);
            Map<String, String> lastCarResult = carResults.get(carResults.size() - 1);
            Map<String, String> lastDrivingResult = drivingResults.get(drivingResults.size() - 1);

            // 营运车辆出问题概率太高，随机加保费
            if (!lastDrivingResult.get("useCharacter").equals("非营运")) {
                BigDecimal reset = new BigDecimal(lastCarResult.get("insurers"));
                reset = reset.multiply(BigDecimal.valueOf(random.nextDouble() * 10 + 1));
                lastCarResult.put("insurers", reset.toString());
            }

            BufferedImage image = new BufferedImage(1200,1540, BufferedImage.TYPE_INT_RGB);
            Graphics2D brush = (Graphics2D) image.getGraphics();
            brush.setColor(Color.WHITE);
            brush.fillRect(0, 0, 1200, 1540);
            brush.rotate(Math.toRadians(-46), image.getWidth(), image.getHeight());
            brush.setColor(new Color(0xECECEC));
            brush.setFont(new Font("黑体", Font.BOLD | Font.ITALIC,200));
            for (int x = -1000, y = -500; y < 1540; x += 300, y += 300) {
                brush.drawString("二营保险有限公司 二营保险有限公司", x, y);
            }
            brush.setColor(new Color(0xE0F3FF));
            brush.setFont(new Font("幼圆", Font.BOLD,50));
            String id = lastCarResult.get("id");
            StringBuilder stringBuilder = new StringBuilder(id);
            for (int i = 0; i < 20; i ++) {
                stringBuilder.append(" " + id);
            }
            for (int x = -1000, y = -375; y < 1540; x += 300, y += 300) {
                brush.drawString(stringBuilder.toString(), x, y);
            }
            brush.rotate(Math.toRadians(46), image.getWidth(), image.getHeight());

            brush.setColor(Color.RED);
            brush.setStroke(new BasicStroke(10));
            brush.drawRect(4,4,1192,1532);
            brush.setStroke(new BasicStroke(20));
            brush.drawRect(40,40,1120,1460);

            brush.setColor(new Color(0x00808D));
            brush.setFont(new Font("黑体", Font.BOLD,60));
            brush.drawString("二营保险有限公司", 350, 140);
            brush.setColor(new Color(0x08005F));
            brush.setFont(new Font("宋体", Font.BOLD,30));
            brush.drawString("保险单", 500, 200);

            brush.setColor(new Color(0x160039));
            brush.setFont(new Font("楷体", Font.BOLD,20));
            brush.drawString("印刷单号: " + visionService.randomId(90, 6), 800, 200);

            brush.setColor(Color.BLACK);
            brush.setFont(new Font("宋体", Font.BOLD,25));

            brush.drawString("姓名： " + lastDriveResult.get("name"), 200, 300);
            brush.drawString("驾照序列号： " + lastDriveResult.get("licenseNumber"), 200, 400);
            brush.drawString("驾照生效时间： " + lastDriveResult.get("issueDate"), 200, 500);
            brush.drawString("驾照到期时间： " + lastDriveResult.get("endDate"), 200, 600);
            brush.drawString("住址： " + lastDriveResult.get("address"), 200, 700);
            brush.drawString("性别： " + lastDriveResult.get("gender"), 200, 800);
            brush.drawString("准驾车型： " + lastDriveResult.get("vehicleType"), 200, 900);

            brush.drawString("车牌： " + lastDrivingResult.get("plateNumber"), 200, 1000);
            brush.drawString("车辆所有者： " + lastDrivingResult.get("owner"), 200, 1100);
            brush.drawString("发动机号码： " + lastDrivingResult.get("engineNumber"), 200, 1200);
            brush.drawString("实际车型： " + lastDrivingResult.get("vehicleType"), 200, 1300);
            brush.drawString("具体车型： " + lastDrivingResult.get("model"), 200, 1400);

            brush.drawString("保险单号： " + id, 600, 300);
            brush.drawString("车型： " + lastCarResult.get("carType"), 600, 400);
            brush.drawString("费用： " + lastCarResult.get("insurers") + "元", 600, 500);
            brush.drawString("理赔金额： " + lastCarResult.get("aoc") + "元", 600, 600);
            brush.drawString("签发： " + "二营保险有限公司", 600, 700);
            brush.drawString("保单有效期： " + lastCarResult.get("startTime") + "-" + lastCarResult.get("endTime"), 600, 800);

            brush.drawString("注册时间： " + lastDrivingResult.get("registerDate"), 600, 900);
            brush.drawString("生效时间： " + lastDrivingResult.get("issueDate"), 600, 1000);
            brush.drawString("车辆识别代号： " + lastDrivingResult.get("vin"), 600, 1100);
            brush.drawString("使用性质： " + lastDrivingResult.get("useCharacter"), 600, 1200);

//            产生公章
            int i = random.nextInt(30);
            brush.rotate(Math.toRadians(i), image.getWidth(), image.getHeight());
            produceStamp(brush, i);
            brush.rotate(Math.toRadians(-i), image.getWidth(), image.getHeight());

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
                         @RequestParam("driving") MultipartFile driving, RedirectAttributes redirectAttributes,
                         HttpServletRequest request) {
        if (car.isEmpty() || drive.isEmpty() || driving.isEmpty()) {
            redirectAttributes.addFlashAttribute("messages", "请上传所有文件。");
            return "redirect:/issue/index";
        }
        List<String> carImages = (List<String>) request.getSession().getAttribute("carImages");
        List<Map<String, String>> carResults = (List<Map<String, String>>) request.getSession().getAttribute("carResults");
        List<String> driveImages = (List<String>) request.getSession().getAttribute("driveImages");
        List<Map<String, String>> driveResults = (List<Map<String, String>>) request.getSession().getAttribute("driveResults");
        List<String> drivingImages = (List<String>) request.getSession().getAttribute("drivingImages");
        List<Map<String, String>> drivingResults = (List<Map<String, String>>) request.getSession().getAttribute("drivingResults");

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
                Map<String, String> driveResult = ocrService.recognizeDriveCard(uploadDir + filename);
                driveImages.add("/images/car/" + filename);
                driveResults.add(driveResult);
            }
            if (!driving.isEmpty()) {
                String filename = saveFile(driving);
                Map<String, String> drivingResult = ocrService.recognizeDrivingCard(uploadDir + filename);
                drivingImages.add("/images/car/" + filename);
                drivingResults.add(drivingResult);
            }
        } catch (Exception e) {
            carImages.clear();
            carResults.clear();
            driveImages.clear();
            driveResults.clear();
            drivingImages.clear();
            drivingResults.clear();
            e.printStackTrace();
            errorMessages += e.getMessage() + "\n";
            errorMessages += "您输入的图像检测失败\n";
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

    private void produceStamp(Graphics2D brush, int degree) {
        String compStr = "二营保险有限公司";

        int radius = 150;
//        逆时针的，但是不对
//        int x = (int) (800 * Math.cos(degree) + 1300 * Math.sin(degree));
//        int x = (int) (800 * Math.cos(degree));
//        int y = (int) (1300 * Math.cos(degree) - 800 * Math.sin(degree));

        int x = (int) (800 * Math.cos(Math.toRadians(degree)) - 1300 * Math.sin(Math.toRadians(degree)));
        int y = (int) (800 * Math.sin(Math.toRadians(degree)) + 1300 * Math.cos(Math.toRadians(degree)));

        //设置文字透明度
        brush.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5F));
        brush.setColor(new Color(0xF82700));
        brush.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        brush.setStroke(new BasicStroke(20));
        Ellipse2D circle = new Ellipse2D.Double();
        circle.setFrameFromCenter(x, y, x + radius + 10, y + radius + 10);
        brush.draw(circle);

        Font starFont = new Font("宋体", Font.BOLD, 120);
        brush.setFont(starFont);
        brush.drawString("★", x - (120 / 2), y + (120 / 3));

        // 签名
        brush.setFont(new Font("宋体", Font.LAYOUT_LEFT_TO_RIGHT, 30));
        brush.drawString("OVEA", x - (25), y + (30 + 50));
        // 年份
        brush.setFont(new Font("宋体", Font.LAYOUT_LEFT_TO_RIGHT, 20));// 写入签名
        brush.drawString(dateFormat.format(new Date()), x - (60), y + (30 + 80));

        brush.setStroke(new BasicStroke(10));

        //根据输入字符串得到字符数组
        String[] messages2 = compStr.split("", 0);
        String[] messages = new String[messages2.length-1];
        System.arraycopy(messages2,0,messages,0,messages2.length-1);

        //输入的字数
        int length = messages.length;

        //设置字体属性
        int fontsize = 40;
        Font f = new Font("Serif", Font.BOLD, fontsize);

        FontRenderContext context = brush.getFontRenderContext();
        Rectangle2D bounds = f.getStringBounds(compStr, context);

        //字符宽度＝字符串长度/字符数
        double char_interval = (bounds.getWidth() / length);
        //上坡度
        double ascent = -bounds.getY();

        int first = 0,second = 0;
        boolean odd = false;

        if (length % 2 == 1) {
            first = (length - 1) / 2;
            odd = true;
        } else {
            first = length / 2 - 1;
            second = length / 2;
            odd = false;
        }

        double radius2 = radius - ascent;
        double x0 = x;
        double y0 = y - radius + ascent;
        //旋转角度
        double a = 2*Math.asin(char_interval/(2*radius2));

        if (odd) {
            brush.setFont(f);
            brush.drawString(messages[first], (float)(x0 - char_interval/2), (float)y0);

            //中心点的右边
            for (int i = first + 1; i < length; i++) {
                double aa = (i - first) * a;
                double ax = radius2 * Math.sin(aa);
                double ay = radius2 - radius2 * Math.cos(aa);
                AffineTransform transform = AffineTransform.getRotateInstance(aa);//,x0 + ax, y0 + ay);
                Font f2 = f.deriveFont(transform);
                brush.setFont(f2);
                brush.drawString(messages[i], (float)(x0 + ax - char_interval/2* Math.cos(aa)), (float)(y0 + ay - char_interval/2* Math.sin(aa)));
            }
            //中心点的左边
            for (int i = first - 1; i > -1; i--) {
                double aa = (first - i) * a;
                double ax = radius2 * Math.sin(aa);
                double ay = radius2 - radius2 * Math.cos(aa);
                AffineTransform transform = AffineTransform.getRotateInstance(-aa);//,x0 + ax, y0 + ay);
                Font f2 = f.deriveFont(transform);
                brush.setFont(f2);
                brush.drawString(messages[i], (float)(x0 - ax - char_interval/2* Math.cos(aa)), (float)(y0 + ay + char_interval/2* Math.sin(aa)));
            }
        } else {
            //中心点的右边
            for (int i = second; i < length; i++) {
                double aa = (i - second + 0.5) * a;
                double ax = radius2 * Math.sin(aa);
                double ay = radius2 - radius2 * Math.cos(aa);
                AffineTransform transform = AffineTransform.getRotateInstance(aa);//,x0 + ax, y0 + ay);
                Font f2 = f.deriveFont(transform);
                brush.setFont(f2);
                brush.drawString(messages[i], (float)(x0 + ax - char_interval/2* Math.cos(aa)), (float)(y0 + ay - char_interval/2* Math.sin(aa)));
            }

            //中心点的左边
            for (int i = first; i> - 1; i--) {
                double aa = (first - i + 0.5) * a;
                double ax = radius2 * Math.sin(aa);
                double ay = radius2 - radius2 * Math.cos(aa);
                AffineTransform transform = AffineTransform.getRotateInstance(-aa);
                Font f2 = f.deriveFont(transform);
                brush.setFont(f2);
                brush.drawString(messages[i], (float)(x0 - ax - char_interval / 2 * Math.cos(aa)), (float)(y0 + ay + char_interval / 2 * Math.sin(aa)));
            }
        }
    }

}
