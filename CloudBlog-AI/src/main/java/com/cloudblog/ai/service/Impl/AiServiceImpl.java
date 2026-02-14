package com.cloudblog.ai.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudblog.ai.mapper.AiMapper;
import com.cloudblog.ai.service.AiService;
import com.cloudblog.common.pojo.DoMain.AiChat;
import com.cloudblog.common.pojo.DoMain.Conversation;
import com.cloudblog.common.pojo.Dto.AiChatDetail;
import com.cloudblog.common.pojo.Dto.AiChatList;
import com.cloudblog.common.pojo.Po.CreateAssistPo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.common.utils.UploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class AiServiceImpl implements AiService {

    @Autowired
    private AiMapper aiMapper;

    @Override
    public void initConversation(Long userId, String conversationId) {
        //判断会话是否存在
        Conversation conversation = aiMapper.getChatByConversationId(conversationId);
        //不存在则创建会话
        if (conversation == null) {
            aiMapper.createConversation(userId, conversationId);
        }
    }

    @Override
    public AjaxResult getChatList(Long userId) {
        if (userId == null) {
            return AjaxResult.error("用户ID不能为空");
        }
        List<AiChatList> chatList = aiMapper.getChatList(userId);
        return AjaxResult.success(chatList);
    }

    @Override
    public AjaxResult getChatDetail(String conversationId) {
        if (conversationId == null) {
            return AjaxResult.success("空的会话id");
        }
        List<AiChatDetail> chatDetails = aiMapper.getChatDetail(conversationId);
        return AjaxResult.success(chatDetails);
    }

    @Transactional
    @Override
    public AjaxResult deleteChat(String conversationId) {
        // 删除会话详情
        aiMapper.delete(new LambdaQueryWrapper<AiChat>().eq(AiChat::getConversationId, conversationId));
        aiMapper.deleteChatMessages(conversationId);
        // 删除会话
        aiMapper.deleteConversation(conversationId);
        return AjaxResult.success("删除成功");
    }

    @Override
    public void saveUserMessage(Long userId, String conversationId, String message, String filePath) {
        aiMapper.saveUserMessage(userId, conversationId, message, filePath);
    }

    @Override
    public void saveAssistantMessage(Long userId, String conversationId, String content) {
        aiMapper.saveAssistantMessage(userId, conversationId, content);
    }

    @Override
    public String uploadFile(Long userId, String conversationId, MultipartFile file) {
        if (file == null) {
            return null;
        }
        if (userId == null || conversationId == null || conversationId.isEmpty()){
            return null;
        }
        String uploadPath = "/file" + "/" + userId + "/" + conversationId;
        return UploadUtil.uploadFile(file,uploadPath);
    }

    @Override
    public Conversation getConversationById(String conversationId) {
        return aiMapper.getConversationById(conversationId);
    }

    @Override
    public void setConversationTitle(String conversationId, String res) {
        aiMapper.setConversationTitle(conversationId, res);
    }

    @Override
    public String processCreateAssistUserMessage(CreateAssistPo po) {
        if (null == po.getType()) {
            return po.getMessage();
        }
        if (po.getType().equals("1")) {
            // 大纲生成
            return po.getMessage();
        } else if (po.getType().equals("2")){
            // 代码生成
            return po.getMessage();
        } else if (po.getType().equals("3")){
            // 修改建议
            String prefix = po.getMessage();
            String end = "给出修改建议,以下是我的文章内容:\n"+
                    "标题："+ po.getTitle()+ "\n"+
                    "正文："+ po.getContent();
            return prefix+end;
        }
        return po.getMessage();
    }
}
