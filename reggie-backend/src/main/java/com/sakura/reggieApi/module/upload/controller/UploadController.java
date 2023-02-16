package com.sakura.reggieApi.module.upload.controller;

import com.sakura.reggieApi.common.anno.LogAnnotation;
import com.sakura.reggieApi.common.utils.TokenUtils;
import com.sakura.reggieApi.common.utils.UploadFileUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @author sakura
 * @className UploadController
 * @createTime 2023/2/12
 */
@RestController
@RequestMapping("/backend/")
public class UploadController {

    @Resource
    UploadFileUtil uploadFileUtil;
    @Resource
    TokenUtils tokenUtils;

    @LogAnnotation("上传菜品封面")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPADMIN')")
    @PostMapping("/upload")
    public String doDishUploadImage(@RequestHeader(TokenUtils.REQUEST_HEADER_TOKEN_KEY) String token,
                                    MultipartFile dishFile) {

        tokenUtils.checkToken(token);

        return uploadFileUtil.uploadFile(dishFile);
    }
}
