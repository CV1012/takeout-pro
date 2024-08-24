package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("admin/common")
@Slf4j
@Api(tags = "通用接口")
public class CommonController {

    @Autowired
    AliOssUtil aliOssUtil;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file){

        try {
            // 获取原始文件名
            String originalFilename = file.getOriginalFilename();

            // 获取文件拓展名
            String extendion = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 通过UUID生成文件名，主要是防止文件重名，导致文件覆盖
            String objectName = UUID.randomUUID().toString() + extendion;

            // filePath：上传文件的访问路径
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);

            return Result.success(filePath);
        } catch (IOException ex) {
            log.info("文件上传失败：{}", ex);
        }

        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
