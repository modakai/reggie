package com.sakura.reggieApi.common.utils;

import com.sakura.reggieApi.common.exception.UploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

/**
 *  文件上传 工具
 * @author sakura
 * @className UploadFileUtil
 * @createTime 2023/2/12
 */
@Component
public class UploadFileUtil {

    @Value("${server.port}")
    private String port;


    /**
     * 上传文件
     * @param uploadFile 文件对象
     * @return 上传成功后的 图片地址
     */
    public String uploadFile(MultipartFile uploadFile) {

        try {
            if (uploadFile.isEmpty())
                throw new UploadException("请选择上传的文件");

            // 获取上传的文件名
            String originalFilename = uploadFile.getOriginalFilename();
            // 获取上传文件的后缀
            String fileSuffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            // UUID 生成文件名
            String uuid = String.valueOf(UUID.randomUUID());

            // 保存文件的路径 /resource/static/image/
            String basePath = ResourceUtils.getURL("classpath:").getPath() + "static/image/";

            // 生成新的文件名
            String fileName = uuid + fileSuffix;

            // 创建新的文件
            File fileExist = new File(basePath);
            // 文件夹不存在，则新建
            if (!fileExist.exists()) {
                fileExist.mkdirs();
            }
            // 获取文件对象
            File file = new File(basePath, fileName);
            // 完成文件的上传
            uploadFile.transferTo(file);

            return JsonResponseResult.success("上传成功",
                    "http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port + "/image/" + fileName);
        } catch (Exception e) {
            throw new UploadException("上传失败");
        }
    }
}
