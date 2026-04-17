package com.cloudblog.content.service.impl;

import com.cloudblog.common.pojo.Po.UploadResourcePo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.common.utils.UploadUtil;
import com.cloudblog.common.utils.UserContext;
import com.cloudblog.content.mapper.ResourceMapper;
import com.cloudblog.content.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceMapper resourceMapper;

    @Value("${file.resource.content.resourcePath}")
    private String resourcePath;

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
        resourceMapper.insert(po);
        return AjaxResult.success("上传成功");
    }
}
