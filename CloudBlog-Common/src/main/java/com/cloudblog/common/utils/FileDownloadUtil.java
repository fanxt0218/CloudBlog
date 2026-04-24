package com.cloudblog.common.utils;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
public class FileDownloadUtil {

    private static String RESOURCE_PATH;
    private static String PATH_PREFIX;

    @Value("${file.resource.path}")
    private String resourcePath;

    @Value("${file.resource.prefix}")
    private String pathPrefix;

    @PostConstruct
    public void init() {
        RESOURCE_PATH = this.resourcePath;
        PATH_PREFIX = this.pathPrefix;
        log.info("文件下载工具初始化完成，资源根路径: {}", RESOURCE_PATH);
    }

    /**
     * 根据虚拟路径获取文件的绝对路径
     * @param virtualPath 虚拟路径，如 /profile/resource/xxx.pdf
     * @return 文件的绝对路径
     */
    public static String getAbsolutePath(String virtualPath) {
        if (virtualPath == null || virtualPath.isEmpty()) {
            throw new IllegalArgumentException("文件路径不能为空");
        }

        // 检查路径是否以配置的prefix开头
        if (!virtualPath.startsWith(PATH_PREFIX)) {
            throw new IllegalArgumentException("无效的文件路径格式，应以 " + PATH_PREFIX + " 开头");
        }

        // 去掉prefix前缀，获取相对路径
        String relativePath = virtualPath.substring(PATH_PREFIX.length());
        
        // 确保相对路径以 / 开头
        if (!relativePath.startsWith("/")) {
            relativePath = "/" + relativePath;
        }

        // 拼接完整的物理路径
        String absolutePath = RESOURCE_PATH + relativePath;
        
        // 标准化路径，防止路径遍历攻击
        try {
            Path normalizedPath = Paths.get(absolutePath).normalize();
            
            // 安全检查：确保解析后的路径仍在RESOURCE_PATH目录下
            Path resourceRootPath = Paths.get(RESOURCE_PATH).normalize();
            if (!normalizedPath.startsWith(resourceRootPath)) {
                log.warn("检测到非法的路径访问尝试: {}", virtualPath);
                throw new SecurityException("不允许访问指定路径");
            }
            
            return normalizedPath.toString();
        } catch (Exception e) {
            log.error("路径解析失败: {}", virtualPath, e);
            throw new RuntimeException("路径解析失败", e);
        }
    }

    /**
     * 构建文件下载响应
     * @param virtualPath 虚拟路径，如 /profile/resource/xxx.pdf
     * @param downloadFileName 下载时显示的文件名（可选，为null时使用原文件名）
     * @return ResponseEntity
     */
    public static ResponseEntity<InputStreamResource> buildDownloadResponse(String virtualPath, String downloadFileName) {
        // 获取绝对路径
        String absolutePath = getAbsolutePath(virtualPath);

        // 创建文件对象
        File file = new File(absolutePath);

        // 检查文件是否存在
        if (!file.exists()) {
            log.warn("文件不存在: {}", absolutePath);
            return ResponseEntity.notFound().build();
        }

        // 检查是否是普通文件
        if (!file.isFile()) {
            log.warn("路径不是有效文件: {}", absolutePath);
            return ResponseEntity.badRequest().build();
        }

        // 检查文件是否可读
        if (!file.canRead()) {
            log.warn("文件不可读: {}", absolutePath);
            return ResponseEntity.status(403).build();
        }

        try {
            // 确定文件名
            String fileName = downloadFileName != null ? downloadFileName : file.getName();

            // URL编码文件名，支持中文
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");

            // 探测文件MIME类型
            String contentType = Files.probeContentType(file.toPath());
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            // 创建资源对象
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            // 构建响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentLength(file.length());
            headers.setCacheControl("max-age=3600"); // 缓存1小时

            // 设置Content-Disposition，支持中文文件名
            headers.set(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename*=UTF-8''" + encodedFileName);

            log.info("文件下载成功: {}, 大小: {} bytes", fileName, file.length());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (IOException e) {
            log.error("文件下载失败: {}", absolutePath, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 构建文件下载响应（使用原文件名）
     * @param virtualPath 虚拟路径，如 /profile/resource/xxx.pdf
     * @return ResponseEntity
     */
    public static ResponseEntity<InputStreamResource> buildDownloadResponse(String virtualPath) {
        return buildDownloadResponse(virtualPath, null);
    }

    /**
     * 检查文件是否存在
     * @param virtualPath 虚拟路径
     * @return 文件是否存在
     */
    public static boolean fileExists(String virtualPath) {
        try {
            String absolutePath = getAbsolutePath(virtualPath);
            return new File(absolutePath).exists();
        } catch (Exception e) {
            log.error("检查文件存在性失败: {}", virtualPath, e);
            return false;
        }
    }

    /**
     * 获取文件大小
     * @param virtualPath 虚拟路径
     * @return 文件大小（字节），如果文件不存在返回-1
     */
    public static long getFileSize(String virtualPath) {
        try {
            String absolutePath = getAbsolutePath(virtualPath);
            File file = new File(absolutePath);
            return file.exists() ? file.length() : -1;
        } catch (Exception e) {
            log.error("获取文件大小失败: {}", virtualPath, e);
            return -1;
        }
    }

    /**
     * 删除文件
     * @param virtualPath 虚拟路径
     * @return 是否删除成功
     */
    public static boolean deleteFile(String virtualPath) {
        try {
            String absolutePath = getAbsolutePath(virtualPath);
            File file = new File(absolutePath);
            
            if (!file.exists()) {
                log.warn("文件不存在，无需删除: {}", absolutePath);
                return false;
            }
            
            boolean deleted = file.delete();
            if (deleted) {
                log.info("文件删除成功: {}", absolutePath);
            } else {
                log.warn("文件删除失败: {}", absolutePath);
            }
            return deleted;
        } catch (Exception e) {
            log.error("删除文件失败: {}", virtualPath, e);
            return false;
        }
    }
}
