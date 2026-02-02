package com.zaw.aicode.tool;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.zaw.aicode.dto.ChatCompletionRequest;
import com.zaw.aicode.dto.ChatCompletionResponse;
import com.zaw.aicode.dto.Message;
import com.zaw.aicode.dto.MessageRequest;
import com.zaw.aicode.dto.ResponseFormat;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lizhong.zhang@yituishui.com
 * @since 2026/1/2
 */
@Slf4j
public class DeepSeekClient {

    private static final String API_URL = "https://api.deepseek.com/chat/completions";

    private static final String TOKEN = "sk-569703c64dbe4136a94b0b0e4c9a3784";

    public static JSONObject send(List<MessageRequest> messageRequests){
        HttpRequest post = HttpUtil.createPost(API_URL);
        post.header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + TOKEN)
                .body(JSON.toJSONString(build(messageRequests)));
        String str =  post.execute().body();
        ChatCompletionResponse response =  JSONUtil.toBean(str, ChatCompletionResponse.class);
        String content = response.getChoices().get(0).getMessage().getContent();
        content = (content.replaceAll("\\n","")
                .replaceAll("```json","")
                .replaceAll("```",""));
         log.info("deepseek response: {}", content);

        return JSONObject.parseObject(content);
    }



    private static ChatCompletionRequest build(List<MessageRequest> messageRequests){
        List<Message> messageList = new ArrayList<>();
         for (MessageRequest messageRequest : messageRequests) {
            Message message = new Message();
            message.setRole(messageRequest.getRole().name());
            message.setContent(messageRequest.getContent());
            messageList.add(message);
        }

        return ChatCompletionRequest.builder()
                .model("deepseek-chat")
                .messages(messageList)
                .responseFormat(new ResponseFormat("json_object"))
                . build();

    }
}
