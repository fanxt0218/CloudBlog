package com.cloudblog.ai.controller;

import com.cloudblog.ai.service.AiService;
import com.cloudblog.ai.util.FileUtil;
import com.cloudblog.common.pojo.DoMain.Conversation;
import com.cloudblog.common.pojo.Dto.AiChatDetail;
import com.cloudblog.common.pojo.Dto.QRContent;
import com.cloudblog.common.pojo.Po.CreateAssistPo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.common.utils.prompt.CreateAssistPrompt;
import com.cloudblog.common.utils.prompt.SummaryPrompt;
import com.cloudblog.common.utils.prompt.SystemPromptGenerator;
import com.cloudblog.common.utils.UploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.content.Media;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

@Slf4j
@RestController
@RequestMapping("/ai")
public class AIController {

    @Autowired
    private AiService aiService;

    private final ChatClient chatClient;

    public AIController(ChatClient.Builder chatClient, VectorStore vectorStore, ChatMemory chatMemory) {
        this.chatClient = chatClient
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
//                        QuestionAnswerAdvisor.builder(vectorStore).build()
                )
                .defaultSystem(SystemPromptGenerator.generateSystemPrompt()).build();
    }

    @PostMapping("/chat")
    public Flux<String> chat(
            @RequestParam Long userId,
            @RequestParam String message,
            @RequestParam String conversationId,
            @RequestParam(value = "filePath", required = false) String filePath) throws IOException {
        // 判断会话是否存在，不存在则创建会话
        aiService.initConversation(userId, conversationId);
        // 保存用户消息（简洁版）
        aiService.saveUserMessage(userId, conversationId, message, filePath);
        // 追加器
        StringBuilder AssistantMessageCollector = new StringBuilder();

        // 流式响应
        Flux<String> originStream = processImagePrompt(message, filePath)  // 处理多模态输入
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .stream()
                .content();

        return originStream
                .doOnNext(AssistantMessageCollector::append) // 追加到容器中
                .doOnComplete(()->{  // 会话结束，保存AI消息
                    aiService.saveAssistantMessage(userId, conversationId, AssistantMessageCollector.toString());
                })
                .doOnError(error -> {
                    // 流处理过程中发生错误，记录日志
                    log.error("Error processing AI stream for conversation {}: {}", conversationId, error.getMessage());
                })
                .doFinally(signalType -> {
                    // 设置会话标题
//                    setConversationTitle(conversationId);
                });
    }

    /**
     * 获取会话列表
     * @param userId
     * @return
     */
    @GetMapping("/chatList")
    public AjaxResult chatList(@RequestParam Long userId) {
        return aiService.getChatList(userId);
    }

    /**
     * 获取会话详情
     * @param conversationId
     * @return
     */
    @GetMapping("/chatDetail")
    public AjaxResult getChatDetail(@RequestParam String conversationId) {
        return aiService.getChatDetail(conversationId);
    }

    /**
     * 删除会话
     * @param conversationId
     * @return
     */
    @PostMapping("/deleteChat")
    public AjaxResult deleteChat(@RequestParam String conversationId) {
        return AjaxResult.success(aiService.deleteChat(conversationId));
    }

    /**
     * 文件上传
     * @param userId
     * @param conversationId
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public AjaxResult upload(
            @RequestParam Long userId,
            @RequestParam String conversationId,
            @RequestParam("file") MultipartFile file) {
        return AjaxResult.success(aiService.uploadFile(userId, conversationId, file));
    }

    /**
     * 设置会话标题
     */
    @PostMapping("/setConversationTitle")
    public AjaxResult setConversationTitle(@RequestParam String conversationId) {
        // 判断该会话有没有标题
        Conversation conversation = aiService.getConversationById(conversationId);
        if (conversation != null && conversation.getTitle() == null) {
            // 取第一条对话内容
            ArrayList<AiChatDetail> his = (ArrayList) (aiService.getChatDetail(conversationId).get("data"));
            QRContent qr = new QRContent();
            for (AiChatDetail hi : his) {
                if (qr.getQ() != null && hi.getType().equals("USER")) {
                    break;
                }
                if (hi.getType().equals("ASSISTANT") && null == qr.getR()) {
                    qr.setR(hi.getContent());
                }
                if (hi.getType().equals("USER") && null == qr.getQ()) {
                    qr.setQ(hi.getContent());
                }
            }
            // AI自动生成标题
            log.info("要总结的内容："+ qr.toString());
            String message = "我将提供一个包含问答或只有问题的内容，你需要根据这些内容总结出一段简短的总结，尽量控制在10个字之内\n" + qr.toString();
            String prompt = new SummaryPrompt().getPrompt();
            String res = tempChat(message, prompt, null);
            aiService.setConversationTitle(conversationId, res);
        }
        return AjaxResult.success();
    }

    /**
     * 进行AI创作辅助
     */
    @PostMapping("/createAssist")
    public Flux<String> CreateAssist(@RequestBody CreateAssistPo po) {
        return chatClient
                .prompt(new CreateAssistPrompt().getPrompt())
                .user(aiService.processCreateAssistUserMessage(po))
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, po.getCurrentConversation()))
                .stream()
                .content();
    }

    /**
     * 处理多模态输入
     * @param message
     * @param filePath
     * @return
     * @throws IOException
     */
    private ChatClient.ChatClientRequestSpec processImagePrompt(String message, String filePath) throws IOException {
        var promptBuilder = chatClient.prompt();
        String fileContentAsContext = "";

        // 1. 如果有文件，提取其内容作为上下文
        if (filePath != null && !filePath.isEmpty()) {
            filePath = UploadUtil.UPLOAD_PATH + filePath.replaceAll("/profile","");
            // 获取文件
            File file = new File(filePath);
            String mimeType = guessMimeType(filePath);

            // -- 图片处理逻辑 --
            if (mimeType != null && mimeType.startsWith("image/")) {
                String dataUri = convertFileToDataUri(file, mimeType);
                promptBuilder.user(userSpec -> userSpec.text(message).media(new Media(MimeType.valueOf(mimeType), URI.create(dataUri))));

                // -- 文本文档处理逻辑 --
            } else {
                fileContentAsContext = extractTextFromFile(file, mimeType);

                // 2. 将提取的文本和用户的问题组合成一个新的提示
                String combinedPrompt = String.format(
                        "基于以下文档内容:\n\n---\n%s\n---\n\n请回答我的问题: %s",
                        fileContentAsContext,
                        message
                );
                promptBuilder.user(combinedPrompt);
            }
        } else {
            // 没有文件，正常处理
            promptBuilder.user(message);
        }
        return promptBuilder;
    }

    /**
     * 尝试根据文件名猜测 MIME 类型
     * @param filePath
     * @return
     */
    private String guessMimeType(String filePath) {
        String extension = "";
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = filePath.substring(lastDotIndex + 1).toLowerCase();
        }

        return switch (extension) {
            case "txt" -> MediaType.TEXT_PLAIN_VALUE;
            case "pdf" -> MediaType.APPLICATION_PDF_VALUE;
            case "png" -> MediaType.IMAGE_PNG_VALUE;
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG_VALUE;
            case "gif" -> MediaType.IMAGE_GIF_VALUE;
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xlsx", "xls" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            default -> MediaType.APPLICATION_OCTET_STREAM_VALUE;
        };
    }

    /**
     * 将 MultipartFile 转换为 Base64 编码的 Data URI 字符串
     * @param file 上传的文件
     * @return Data URI 字符串，例如 "data:image/png;base64,iVBORw0KGgo..."
     * @throws IOException 读取文件字节时可能发生异常
     */
    private String convertFileToDataUri(File file, String mimeType) throws IOException {
        try {
            // 使用 Files.readAllBytes 读取文件内容
            byte[] bytes = java.nio.file.Files.readAllBytes(file.toPath());
            String base64Data = Base64.getEncoder().encodeToString(bytes);
            return "data:" + mimeType + ";base64," + base64Data;
        } catch (Exception e) {
            log.error("Failed to convert file to Data URI: {}", e.getMessage(), e);
            throw new IOException("Failed to process file: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * 根据文件类型提取纯文本内容
     */
    private String extractTextFromFile(File file, String mimeType) throws IOException {
        try (InputStream inputStream = new java.io.FileInputStream(file)) {
            if (MediaType.TEXT_PLAIN_VALUE.equals(mimeType)) {
                return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            } else if (MediaType.APPLICATION_PDF_VALUE.equals(mimeType)) {
                try (PDDocument document = PDDocument.load(inputStream)) {
                    return new PDFTextStripper().getText(document);
                }
            } else if ("application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(mimeType)) { // .docx
                try (XWPFDocument doc = new XWPFDocument(inputStream);
                     XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
                    return extractor.getText();
                }
            } else if ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(mimeType)) { // .xlsx
                return FileUtil.extractExcelText(inputStream);
            } else {
                // 返回一个友好的提示，告知不支持此文件类型
                return "不支持的文件类型: " + mimeType;
            }
        }
    }

    /**
     * 进行临时AI对话
     */
    private String tempChat(String message, String prompt, String filePath) {
        return chatClient
                .prompt(prompt)
                .user(message)
                .call()
                .content();
    }


}
