package com.cloudblog.common.utils;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
public class UploadUtil {

    public static String UPLOAD_PATH;

    public static String PREFIX;

    @Value("${file.resource.path}")
    private String prePath;

    @Value("${file.resource.prefix}")
    private String prefix;

    @PostConstruct
    public void init() {
        UPLOAD_PATH = this.prePath;
        PREFIX = this.prefix;
        log.info("上传文件路径：{}", UPLOAD_PATH);
    }

    /**
     * 上传单个文件
     */
    public static String uploadFile(MultipartFile file, String path) {
        // 检查文件是否为空
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("上传的文件不能为空");
        }

        try {
            // 确保路径格式正确
            if (!path.startsWith("/")) {
                path = "/" + path;
            }

            // 构建完整的上传目录路径，确保使用正确的文件分隔符
            String uploadDir = UPLOAD_PATH + path;

            // 标准化路径，处理路径分隔符问题
            File directory = new File(uploadDir);

            // 调试信息：打印路径信息
//            System.out.println("目录是否存在: " + directory.exists());
//            System.out.println("目录是否可写: " + directory.canWrite());

            // 如果目录不存在，则创建目录（包括父目录）
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                System.out.println("尝试创建目录: " + directory.getAbsolutePath());
//              System.out.println("目录创建结果: " + created);
                if (!created) {
                    throw new RuntimeException("创建目录失败: " + directory.getAbsolutePath());
                }
            }

            // 再次确认目录存在且可写
            if (!directory.exists() || !directory.canWrite()) {
                throw new RuntimeException("目录不可用: " + directory.getAbsolutePath());
            }

            // 获取原始文件名
            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null || originalFileName.trim().isEmpty()) {
                throw new RuntimeException("文件名无效");
            }

            // 生成唯一的文件名
            String fileName = generateUniqueFileName(originalFileName);

            // 构建目标文件
            File destFile = new File(directory, fileName);

            // 调试信息
            System.out.println("目标文件路径: " + destFile.getAbsolutePath());

            // 保存文件
            file.transferTo(destFile);

            // 返回路径（确保路径格式正确）
            String resultPath = PREFIX + path + "/" + fileName;
            return resultPath.replace("\\", "/"); // 统一使用正斜杠

        } catch (IOException e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }

    /**
     * 上传多个文件
     */
    public static List<String> uploadFiles(MultipartFile[] files, String path) {
        List<String> paths = new ArrayList<>();
        for (MultipartFile file : files) {
            String resultPath = uploadFile(file, path);
            paths.add(resultPath);
        }
        log.info("上传文件成功");
        return paths;
    }

    /**
     * 生成唯一的文件名
     */
    private static String generateUniqueFileName(String originalFileName) {
        // 获取文件扩展名
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        // 使用UUID + 时间戳确保唯一性
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);

        return uuid + "_" + timeStamp + fileExtension;
    }
}
