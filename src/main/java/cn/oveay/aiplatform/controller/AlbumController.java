package cn.oveay.aiplatform.controller;

import cn.oveay.aiplatform.service.ResourceService;
import cn.oveay.aiplatform.service.VisionService;
import cn.oveay.aiplatform.utils.MD5;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author OVEA(Qiangwei Luo)
 * created on : 2020/5/18 20:36
 * 文件说明：
 */
@RestController
@RequestMapping("/album/v1")
@Slf4j
public class AlbumController {

    @Autowired
    private VisionService visionService;

    @Autowired
    private ResourceService resourceService;

    @GetMapping("/list")
    public List<String> getList() {
        return resourceService.getAllPhotos();
    }

    @GetMapping("/allCates")
    public Object getCates() {
        return resourceService.getAllCates();
    }

    @GetMapping("/getPhotoByCateAndLabel")
    public List<String> getPhotoByCateAndLabel(@RequestParam("cate") String cate, @RequestParam("tag") String tag) {
        return resourceService.getPhotosByCateAndLabel(cate, tag);
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(file.getBytes())) {
            String md5Str = MD5.byteToMd5(inputStream.readAllBytes());
            inputStream.reset();
            inputStream.mark(0);

            String fileName = file.getOriginalFilename();
            String fileType = fileName.substring(fileName.lastIndexOf("."));
            fileName = String.format("%s%s", md5Str, fileType);
            resourceService.saveAndRecognizeImage(fileName, inputStream);
            return fileName;
        }
    }

}
