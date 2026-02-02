package com.zaw.aicode.service;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.zaw.game.entity.GameChapter;
import com.zaw.game.service.GameChapterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OllamaBatchService {

    private static final String API_URL = "http://192.168.1.7:11434/api/chat";
    private static final String DEFAULT_MODEL = "qwen3:30b";
    private static final String DEFAULT_START_NO = "001";
    private static final Pattern FILE_PATTERN = Pattern.compile("^(\\d{3})_.*\\.txt$");

    private final GameChapterService gameChapterService;

    @Async("asyncExecutor")
    public void processDirectory(String systemPrompt, String model, String startNo) {
        String useStartNo = (startNo == null || startNo.isBlank()) ? DEFAULT_START_NO : startNo.trim();
        int startIndex = parseIndex(useStartNo);
        String useModel = (model == null || model.isBlank()) ? DEFAULT_MODEL : model.trim();
        String usePrompt = systemPrompt == null ? "你是一个“文本解析器”，不是作家，也不是评论者。\n" + //
                        "\n" + //
                        "你的任务是：\n" + //
                        "将给定的《三国演义》单一章节原文，转换为【结构化 JSON 素材】，用于文字游戏和互动叙事系统。\n" + //
                        "\n" + //
                        "请严格遵守以下规则：\n" + //
                        "1. 只基于提供的章节原文，不得补写、改写、推测或引入原文未出现的内容\n" + //
                        "2. 不得评价人物，不得分析作者意图\n" + //
                        "3. 不得使用现代视角或现代语言解释历史\n" + //
                        "4. 所有信息必须能在本章节原文中找到依据\n" + //
                        "5. 若某字段在本章节中不存在，必须填 null 或空数组，不得省略字段\n" + //
                        "6. 输出必须是【合法 JSON】，不得包含任何解释性文字、注释或 Markdown 标记\n" + //
                        "7. JSON 最外层必须是一个对象\n" + //
                        "\n" + //
                        "请严格按照下面给定的 JSON 结构输出。\n" + //
                        "\n" + //
                        "{\n" + //
                        "  \"keywords\": [],\n" + //
                        "  \"summary\": \"\",\n" + //
                        "  \"detailed_plot\": [],\n" + //
                        "  \"time_and_place\": {\n" + //
                        "    \"time\": \"\",\n" + //
                        "    \"place\": \"\"\n" + //
                        "  },\n" + //
                        "  \"entities\": {\n" + //
                        "    \"characters\": [\n" + //
                        "      {\n" + //
                        "        \"name\": \"\",\n" + //
                        "        \"identity_or_faction\": \"\",\n" + //
                        "        \"actions_in_chapter\": \"\",\n" + //
                        "        \"state_change\": \"\"\n" + //
                        "      }\n" + //
                        "    ],\n" + //
                        "    \"events\": [\n" + //
                        "      {\n" + //
                        "        \"event_name\": \"\",\n" + //
                        "        \"description\": \"\",\n" + //
                        "        \"immediate_result\": \"\"\n" + //
                        "      }\n" + //
                        "    ],\n" + //
                        "    \"items\": [\n" + //
                        "      {\n" + //
                        "        \"name\": \"\",\n" + //
                        "        \"type\": \"\",\n" + //
                        "        \"role_in_chapter\": \"\",\n" + //
                        "        \"state_change\": \"\"\n" + //
                        "      }\n" + //
                        "    ]\n" + //
                        "  },\n" + //
                        "  \"hidden_information\": []\n" + //
                        "}\n" + //
                        "" : systemPrompt;

        Path baseDir = Path.of("sanguo").toAbsolutePath();
        if (!Files.exists(baseDir) || !Files.isDirectory(baseDir)) {
            log.warn("sanguo directory not found: {}", baseDir);
            return;
        }

        List<Path> files;
        try (var stream = Files.list(baseDir)) {
            files = stream
                    .filter(Files::isRegularFile)
                    .filter(path -> FILE_PATTERN.matcher(path.getFileName().toString()).matches())
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("list sanguo files failed", e);
            return;
        }

        for (Path file : files) {
            String fileName = file.getFileName().toString();
            int index = extractIndex(fileName);
            if (index < startIndex) {
                continue;
            }

            try {
                String content = Files.readString(file, StandardCharsets.UTF_8);
                String responseContent = callOllama(usePrompt, content, useModel);
                GameChapter saved = gameChapterService.saveFromContent(responseContent);
                if (saved != null) {
                    log.info("ollama saved chapter for {} -> id={}, uuid={}", fileName, saved.getId(), saved.getUuid());
                } else {
                    log.warn("ollama save failed for {}", fileName);
                }
            } catch (Exception e) {
                log.error("process file failed: {}", fileName, e);
            }
        }
    }

    private String callOllama(String systemPrompt, String userQuestion, String model) {
        JSONObject payload = new JSONObject();
        payload.put("model", model);
        JSONArray messages = new JSONArray();
        messages.add(new JSONObject()
                .fluentPut("role", "user")
                .fluentPut("content", "[\"系统\"]" + systemPrompt + "[\"处理文本\"]" + userQuestion));
        payload.put("messages", messages);
        JSONObject options = new JSONObject();
        options.put("num_ctx", 8192);
        options.put("num_batch", 256);
        options.put("temperature", 0.7);
        payload.put("options", options);
        payload.put("stream", false);

        String responseStr = HttpRequest.post(API_URL)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(payload.toJSONString())
                .execute()
                .body();
        log.info("ollama raw response: {}", responseStr);

        JSONObject response = JSONObject.parseObject(responseStr);
        if (response != null && response.getJSONObject("message") != null) {
            return response.getJSONObject("message").getString("content");
        }
        return null;
    }

    private int extractIndex(String fileName) {
        Matcher matcher = FILE_PATTERN.matcher(fileName);
        if (matcher.matches()) {
            return parseIndex(matcher.group(1));
        }
        return -1;
    }

    private int parseIndex(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 1;
        }
    }
}
