package com.agri.common.upload.controller;

import com.agri.common.upload.model.ImageInfo;
import com.agri.common.upload.service.IImageInfoService;
import com.agri.model.CommonResult;
import com.agri.utils.RedisUtil;
import com.agri.utils.annotation.SaveAuth;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
public class UploadController {

    private static final String POSTFIX = "IMAGE_FIX";

    @Resource
    private RedisUtil redisUtil;
    @Resource
    private IImageInfoService service;

    @PostMapping("/image")
    @SaveAuth(roles = {"admin", "farmer", "coder", "user"})
    public CommonResult<?> uploadImage(@RequestParam("file")MultipartFile file) {
        String fileName = UUID.randomUUID() + "." + Objects.requireNonNull(file.getContentType())
                .substring(file.getContentType().lastIndexOf("/") + 1);
        String path = getSavePath();
        File destFileName = new File(path, fileName);
        File parentFile = destFileName.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdir();
        }

        try {
            file.transferTo(destFileName);
        }catch (Exception e) {
            e.printStackTrace();
            return CommonResult.error("上传失败" + e.getMessage());
        }
        String uri = "/static/img/" + fileName;
        service.save(ImageInfo.builder().imgAddress(uri).build());
        return CommonResult.OK(uri);
    }

    public String getSavePath() {
        // 这里需要注意的是ApplicationHome是属于SpringBoot的类
        // 获取项目下resources/static/img路径
        ApplicationHome applicationHome = new ApplicationHome(this.getClass());

        // 保存目录位置根据项目需求可随意更改
        return applicationHome.getDir().getParentFile()
                .getParentFile().getAbsolutePath() + "\\src\\main\\resources\\static\\img";
    }
}
