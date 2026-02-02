package com.zaw.aicode.controller;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.zaw.aicode.service.OllamaBatchService;
import com.zaw.aicode.web.OllamaBatchRequest;
import com.zaw.aicode.web.OllamaChatRequest;
import com.zaw.common.web.R;
import com.zaw.game.entity.GameChapter;
import com.zaw.game.service.GameChapterService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lizhong.zhang@yituishui.com
 * @since 2026/2/1
 */
@RestController
@RequestMapping("/ollama")
@Validated
@Slf4j
@AllArgsConstructor
public class OllamaController {

    private static final String API_URL = "http://192.168.1.7:11434/api/chat";
    private static final String DEFAULT_MODEL = "qwen3:30b";
    private final GameChapterService gameChapterService;
    private final OllamaBatchService ollamaBatchService;

    @PostMapping("/chat")
    public R<JSONObject> chat(@RequestBody OllamaChatRequest request) {
        

        JSONObject payload = new JSONObject();
        String model = (request.getModel() == null || request.getModel().isBlank())
                ? DEFAULT_MODEL
                : request.getModel();
        payload.put("model", model);

        JSONArray messages = new JSONArray();
        messages.add(new JSONObject()
                .fluentPut("role", "user")
                .fluentPut("content", "[\"系统\"]"+request.getSystemPrompt()+"[\"处理文本\"]"+request.getUserQuestion()));
        payload.put("messages", messages);
        JSONObject options = new JSONObject();
        options.put("num_ctx", 8192);
        options.put("num_batch", 256);
        options.put("temperature", 0.7);
        payload.put("options", options);
        payload.put("stream", false);
        System.out.println(payload.toJSONString());
        try {
            String responseStr = HttpRequest.post(API_URL)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .body(payload.toJSONString())
                    .execute()
                    .body();

            JSONObject response = JSONObject.parseObject(responseStr);
            log.info("ollama raw response: {}", responseStr);
            String content = null;
            if (response != null && response.getJSONObject("message") != null) {
                content = response.getJSONObject("message").getString("content");
            }
            GameChapter savedChapter = gameChapterService.saveFromContent(content);
            JSONObject result = new JSONObject();
            result.put("content", content);
            result.put("raw", response);
            if (savedChapter != null) {
                result.put("chapterId", savedChapter.getId());
                result.put("chapterUuid", savedChapter.getUuid());
            }
            return R.ok(result);
        } catch (Exception e) {
            return R.fail("调用 Ollama 失败: " + e.getMessage());
        }
    }

    @PostMapping("/chatBatchAsync")
    public R<JSONObject> chatBatchAsync(@RequestBody(required = false) OllamaBatchRequest request) {
        String systemPrompt = request == null ? null : request.getSystemPrompt();
        String model = request == null ? null : request.getModel();
        String startNo = request == null ? null : request.getStartNo();
        ollamaBatchService.processDirectory(systemPrompt, model, startNo);
        JSONObject result = new JSONObject();
        result.put("started", true);
        result.put("startNo", startNo == null || startNo.isBlank() ? "001" : startNo);
        return R.ok(result);
    }
}
