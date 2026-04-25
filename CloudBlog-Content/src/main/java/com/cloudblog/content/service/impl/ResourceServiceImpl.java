package com.cloudblog.content.service.impl;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cloudblog.common.enums.PostType;
import com.cloudblog.common.exception.CloudBlogException;
import com.cloudblog.common.exception.CommonError;
import com.cloudblog.common.pojo.DoMain.DownloadResource;
import com.cloudblog.common.pojo.DoMain.Posts;
import com.cloudblog.common.pojo.DoMain.UserInfo;
import com.cloudblog.common.pojo.Dto.PageResponse;
import com.cloudblog.common.pojo.Po.GetIndexResourcePo;
import com.cloudblog.common.pojo.Po.PostPo;
import com.cloudblog.common.pojo.Po.UploadResourcePo;
import com.cloudblog.common.pojo.Vo.IndexResourceVo;
import com.cloudblog.common.pojo.Vo.UserPostVo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.common.utils.FileDownloadUtil;
import com.cloudblog.common.utils.UploadUtil;
import com.cloudblog.common.utils.UserContext;
import com.cloudblog.content.config.ContentStartupConfig;
import com.cloudblog.content.mapper.ResourceMapper;
import com.cloudblog.content.service.InterestService;
import com.cloudblog.content.service.ResourceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceMapper resourceMapper;
    @Autowired
    private ContentStartupConfig contentStartupConfig;
    @Autowired
    private InterestService interestService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${file.resource.content.resourcePath}")
    private String resourcePath;
    @Value("${file.resource.path}")
    private String pathPrefix;

    @Override
    public AjaxResult uploadFile(MultipartFile file) {
        Long user = UserContext.getUser();
        if (user == null) {
            return AjaxResult.error(HttpStatus.UNAUTHORIZED.value(),"请先登录");
        }
        String path = "";
        try {
            path = UploadUtil.uploadFile(file, resourcePath);
        }catch (Exception e) {
            return AjaxResult.error("上传失败"+ e.getMessage());
        }
        return AjaxResult.success("上传成功", path);
    }

    @Transactional
    @Override
    public AjaxResult uploadResource(UploadResourcePo po) {
        if (po.getResourceCreator() == null) {
            return AjaxResult.error("用户id不能为空");
        }
        if (po.getResourceName() == null || po.getResourceName().isEmpty()) {
            return AjaxResult.error("资源名称不能为空");
        }
        if (po.getResourceUrl() == null || po.getResourceUrl().isEmpty()) {
            return AjaxResult.error("资源地址不能为空");
        }
        // 如果传入了文章id，则先判断有没有已经绑定过
        if (po.getResourceBindContentId() != null && po.getResourceBindContentId() > 0) {
            List<DownloadResource> resources = resourceMapper.selectList(
                    new LambdaQueryWrapper<DownloadResource>()
                            .eq(DownloadResource::getResourceBindContentId, po.getResourceBindContentId())
                            .eq(DownloadResource::getResourceBindContentType, po.getResourceBindContentType())
                            .eq(DownloadResource::getResourceStatus, 1)
            );
            // 已经绑定了, 解除绑定
            if (resources != null && !resources.isEmpty()) {
                for (DownloadResource resource : resources) {
                    resource.setResourceBindContentId(null);
                    resource.setResourceBindContentType(null);
                    resourceMapper.update(resource, new LambdaUpdateWrapper<DownloadResource>().eq(DownloadResource::getId, resource.getId()));
                }
            }
        }
        resourceMapper.insert(po);
        return AjaxResult.success("上传成功");
    }

    @Override
    public AjaxResult getResource(Long userId, Long contentId) {
        QueryWrapper<DownloadResource> wrapper = new QueryWrapper<>();
        if (userId != null && userId > 0) {
            wrapper.eq("resource_creator", userId);
        }
        if (contentId != null && contentId > 0) {
            wrapper.eq("resource_bind_content_id", contentId);
        }
        wrapper.eq("resource_status", 1);
        List<DownloadResource> resources = resourceMapper.selectList(wrapper);
        return AjaxResult.success(resources);
    }

    @Override
    public AjaxResult updateResource(UploadResourcePo uploadResourcePo) {
        if (uploadResourcePo.getId() == null) {
            return AjaxResult.error("参数错误");
        }
        resourceMapper.update(
                uploadResourcePo,
                new LambdaUpdateWrapper<DownloadResource>().eq(DownloadResource::getId, uploadResourcePo.getId()));
        return AjaxResult.success("更新成功");
    }

    @Override
    public AjaxResult deleteResource(UploadResourcePo uploadResourcePo) {
        if (uploadResourcePo.getId() == null) {
            return AjaxResult.error("参数错误");
        }
        resourceMapper.update(
                new LambdaUpdateWrapper<DownloadResource>()
                        .eq(DownloadResource::getId, uploadResourcePo.getId())
                        .set(DownloadResource::getResourceStatus, 0)
        );
        return AjaxResult.success("删除成功");
    }

    @Override
    public AjaxResult getResourceCategory() {
        HashMap<String, List<String>> categories = contentStartupConfig.getResourceCategoryList();
        return AjaxResult.success(categories);
    }

    @Override
    public AjaxResult getIndexResource(GetIndexResourcePo po, String cursor, Integer size, String sortBy, String tag) {
        // 判断是否有用户id（是否登录），以此去配置推荐算法
        boolean withInterest = true;
        Object data;
        if (po.getUserId() == null) {
            data = null;
        } else {
            data = interestService.getInterestInfo(po.getUserId()).get("data");
        }
        // 无兴趣/选择了tag/未登录=默认推荐
        if (data == null || (po.getTagName() != null && !"全部".equals(po.getTagName())) || po.getName() != null) {
            withInterest = false;
        }

        ArrayList<String> categoryNames = new ArrayList<>();
        if (po.getTagName() != null && !po.getTagName().isEmpty()) {
            // 处理子分类
            HashMap<String, List<String>> categoryMap = contentStartupConfig.getResourceCategoryList();
            List<String> categories = categoryMap.get(po.getTagName());
            if (categories != null && !categories.isEmpty()) {
                categoryNames.addAll(categories);
            }
            // 将自己也加进去（可能选择的本身就是子分类）
            categoryNames.add(po.getTagName());
        }

        // 执行查询
        try {
            // 默认参数处理
            size = (size == null || size <= 0) ? 10 : Math.min(size, 100); // 限制最大100条

            // 解析游标
            Map<String, Object> cursorMap = parseCursor(cursor);
            Long lastId = null;
            LocalDateTime lastCreateTime = null;

            if (cursorMap != null) {
                lastId = ((Number) cursorMap.get("id")).longValue();
                Object createTimeObj = cursorMap.get("createTime");
                if (createTimeObj != null) {
                    if (createTimeObj instanceof String) {
                        lastCreateTime = LocalDateTime.parse((String) createTimeObj);
                    } else if (createTimeObj instanceof LocalDateTime) {
                        lastCreateTime = (LocalDateTime) createTimeObj;
                    }
                }
            }
            List<IndexResourceVo> resources;
            if (withInterest){
                // 使用Mapper执行查询，多查一条记录用于判断是否还有更多数据
                // 兴趣推荐
                resources = resourceMapper.getResourceListWithInterest(po.getUserId(), categoryNames,  lastId, lastCreateTime, size + 1);
            }else {
                // 默认推荐
                resources = resourceMapper.getResourceListWithNoInterest(po.getUserId(), lastId, po.getName(), lastCreateTime, size + 1, categoryNames);
            }
            // 构建PageResponse返回结果
            PageResponse<IndexResourceVo> response = new PageResponse<>();
            response.setPageSize(size);

            // 判断是否还有更多数据
            boolean hasNext = resources.size() > size;
            response.setHasNext(hasNext);

            // 设置实际返回的数据列表
            if (hasNext) {
                // 移除多查的一个元素
                response.setContent(resources.subList(0, size));
                // 生成下一个游标
                IndexResourceVo lastResource = resources.get(size - 1);
                IndexResourceVo resource = new IndexResourceVo();
                BeanUtils.copyProperties(lastResource, resource);
                String nextCursor = generateCursor(resource);
                response.setNextCursor(nextCursor);
            } else {
                response.setContent(resources);
            }

            return AjaxResult.success(response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CloudBlogException("获取下载资源列表失败", CommonError.INTERNAL_ERROR);
        }
    }

    @Override
    public AjaxResult searchResource(String keyword) {
        List<IndexResourceVo> resources = resourceMapper.queryResource(keyword);
        return AjaxResult.success(resources);
    }

    @Override
    public AjaxResult downloadResource(Long userId, Long resourceId) {
        if (userId == null || resourceId == null) {
            return AjaxResult.error("参数错误");
        }
        // vip信息校验
        DownloadResource downloadResource = resourceMapper.selectOne(new LambdaQueryWrapper<DownloadResource>().eq(DownloadResource::getId, resourceId));
        if (downloadResource == null) {
            return AjaxResult.error("资源不存在");
        }
        if (downloadResource.getResourceStatus() == 0 || downloadResource.getResourceIsPublic() == 0) {
            return AjaxResult.error("资源不存在或没有下载权限");
        }
        if (downloadResource.getVipResource() == 1) {
            UserInfo userInfo = resourceMapper.getUserInfo(userId);
            if (userInfo == null || userInfo.getIsVip() == 0) {
                return AjaxResult.warn("请先开通会员");
            }
        }
        // 开始下载
        String resourceUrl = downloadResource.getResourceUrl();
        if (resourceUrl == null || resourceUrl.isEmpty()) {
            return AjaxResult.error("资源链接无效");
        }

        // 生成下载token,5分钟
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(resourceUrl+":"+userId, token, Duration.ofMinutes(5));

        Map<String, Object> result = new HashMap<>();
        result.put("resourceName", downloadResource.getResourceName());
        result.put("resourceUrl", resourceUrl);
        result.put("downloadUrl", resourceUrl);
        result.put("token", token);

        return AjaxResult.success("获取下载链接成功", result);
    }

    @Override
    public ResponseEntity<InputStreamResource> download(Long userId, String url, String filename, String token) {
        // 校验token
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!Objects.equals(redisTemplate.opsForValue().get(url + ":" + userId), token)) {
            log.warn("{}_{}下载资源失败,token错误", url, userId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // 更新下载次数
        synchronized (url + ":" + userId) {
            DownloadResource resource = resourceMapper.selectOne(new LambdaQueryWrapper<DownloadResource>().eq(DownloadResource::getResourceUrl, url));
            resource.setDownloadCount(resource.getDownloadCount() + 1);
            resourceMapper.update(resource, new LambdaUpdateWrapper<DownloadResource>().eq(DownloadResource::getResourceUrl, url).set(DownloadResource::getDownloadCount, resource.getDownloadCount()));
        }
        return FileDownloadUtil.buildDownloadResponse(url, filename);
    }

    @Override
    public AjaxResult getSelectedResource() {
        List<DownloadResource> resources = resourceMapper.selectList(new LambdaQueryWrapper<DownloadResource>()
                .eq(DownloadResource::getResourceStatus, 1)
                .eq(DownloadResource::getResourceIsPublic, 1)
                .orderByDesc(DownloadResource::getResourceCreateTime)
                .last("limit 20")
        );

        if (resources == null || resources.isEmpty()) {
            return AjaxResult.success(new ArrayList<>());
        }

        // 随机取出六(最多)条
        Random random = new Random();
        int count = Math.min(6, resources.size());
        ArrayList<DownloadResource> lastResource = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            DownloadResource remove = resources.remove(random.nextInt(resources.size()));
            lastResource.add(remove);
        }
        return AjaxResult.success(lastResource);
    }

    /**
     * 解析游标字符串
     * @param cursor 游标字符串
     * @return 解析后的游标数据
     */
    private Map<String, Object> parseCursor(String cursor) {
        if (cursor == null || cursor.isEmpty()) {
            return null;
        }

        try {
            String decoded = new String(Base64.getDecoder().decode(cursor), StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(decoded, Map.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 生成游标字符串
     * @param resource 当前资源
     * @return 游标字符串
     */
    private String generateCursor(IndexResourceVo resource) {
        try {
            Map<String, Object> cursorMap = new HashMap<>();
            cursorMap.put("id", resource.getId());
            cursorMap.put("createTime", resource.getResourceCreateTime().toString());

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(cursorMap);
            return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            return null;
        }
    }
}
