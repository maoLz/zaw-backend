package com.zaw.aicode.tool;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson2.JSON;
import com.zaw.aicode.dto.EditDTO;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatchApplier {

    public static String applyEdits(String source, List<EditDTO> edits) {
        String result = source;
        for (EditDTO edit : edits) {
            result = applySingleEdit(result, edit);
        }
        return result;
    }

    private static String applySingleEdit(String source, EditDTO edit) {
        Pattern pattern = buildTripleAnchorPattern(
                edit.getBefore(),
                edit.getAnchor(),
                edit.getAfter(),
                edit.getAnchorIsRegex() != null ? edit.getAnchorIsRegex() : false
        );

        Matcher matcher = pattern.matcher(source);

        if (!matcher.find()) {
            throw new IllegalStateException("Patch anchor not found: " + edit.getAnchor());
        }

        int start = matcher.start("anchor");
        int end = matcher.end("anchor");

        if (matcher.find()) {
            throw new IllegalStateException("Patch anchor not unique");
        }

        switch (edit.getType()) {
            case "replace":
                return source.substring(0, start)
                        + edit.getContent()
                        + source.substring(end);

            case "insert_before":
                return source.substring(0, start)
                        +"\n"
                        + edit.getContent()
                        + source.substring(start);

            case "insert_after":
                return source.substring(0, end)
                        +"\n"
                        + edit.getContent()
                        + source.substring(end);

            case "delete":
                return source.substring(0, start)
                        + source.substring(end);

            default:
                throw new IllegalArgumentException("Unknown edit type: " + edit.getType());
        }
    }

    /**
     * 构造 before + anchor + after 的宽松匹配正则
     * 所有空白均视为 \s+
     *
     * @param anchorIsRegex 如果为true，则anchor直接作为正则表达式使用
     */
    private static Pattern buildTripleAnchorPattern(
            String before,
            String anchor,
            String after,
            boolean anchorIsRegex
    ) {
        StringBuilder regex = new StringBuilder();
        regex.append("(?s)");  // DOTALL模式

        if (before != null && !before.isEmpty()) {
            regex.append(before);
            //regex.append("\\s*");
        }

        regex.append("(?<anchor>");
        if (anchorIsRegex) {
            // 直接使用anchor作为正则
            regex.append(anchor);
        } else {
            regex.append(toLooseRegex(anchor));
        }
        regex.append(")");

        if (after != null && !after.isEmpty()) {
            //regex.append("\\s*");
            regex.append(after);
        }
        System.out.println(regex.toString());
        return Pattern.compile(regex.toString());
    }

    /**
     * 将任意文本转成"忽略空白"的安全正则
     */
    private static String toLooseRegex(String text) {
        StringBuilder sb = new StringBuilder();
        boolean inWhitespace = false;

        for (char c : text.toCharArray()) {
            if (Character.isWhitespace(c)) {
                if (!inWhitespace) {
                    sb.append("\\s+");
                    inWhitespace = true;
                }
            } else {
                inWhitespace = false;
                sb.append(Pattern.quote(String.valueOf(c)));
            }
        }
        return sb.toString();
    }

    // public static void main(String[] args) {
    //     String json = "{\n" +
    //             "        \"type\": \"replace\",\n" +
    //             "        \"before\": \"\\\"local\\\":\\\\s*\\\\{.*?\",\n" +
    //             "        \"anchor\": \"\\\"token\\\":\\\\s*\\\"[^\\\"]+\\\"\",\n" +
    //             "        \"anchorIsRegex\": true,\n" +
    //             "        \"after\": \"\",\n" +
    //             "        \"content\": \"\\\"token\\\": \\\"${context.token}\\\"\"\n" +
    //             "      }";
    //     System.out.println(json);
    //     EditDTO edit = JSON.parseObject(json, EditDTO.class);
    //     String fileContent = FileUtil.readString("/Users/zhanglizhong/rebell/ai/.vscode/settings.json", StandardCharsets.UTF_8);
    //     System.out.println(applySingleEdit(fileContent, edit));

    // /*    json =" \"rest-client.environmentVariables\": {\n" +
    //             "        \"$shared\": {\n" +
    //             "            \"username\": \"pkslow\",\n" +
    //             "            \"password\": \"123456\"\n" +
    //             "        },\n" +
    //             "        \"local\": {\n" +
    //             "            \"hostname\": \"http://127.0.0.1:8080\",\n" +
    //             "            \"password\": \"{{$shared password}}\",\n" +
    //             "            \"token\": \"eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2tleSI6IjE1ZWVhNTk5LWU3NmEtNGZkMC04MmRkLTk4YjQ0ZjkzNjM5ZSIsInRva2VuX2tleSI6InRva2VuOm1lcmNoYW50OmFwcDoxNWVlYTU5OS1lNzZhLTRmZDAtODJkZC05OGI0NGY5MzYzOWU6NWM5MWU5MDEtNTk2My00YjQxLThlZWYtNGFlNWViNmI1ODI4IiwiY2xpZW50X3R5cGUiOiJtZXJjaGFudF9hcHAiLCJ0b2tlbl90eXBlIjoiQUNDRVNTIiwicGxhdGZvcm0iOiJtZXJjaGFudCIsInN1YiI6IjE1ZWVhNTk5LWU3NmEtNGZkMC04MmRkLTk4YjQ0ZjkzNjM5ZSIsImlhdCI6MTc2NzUwNzY1MSwiZXhwIjoxNzY3OTkxNDkxLCJhdWQiOiJtZXJjaGFudCJ9.N6YiUpkWXvc5igJc-7-s82Se7G06La4g85NgxcY3VkA\"\n" +
    //             "        },\n" +
    //             "        \"production\": {\n" +
    //             "            \"hostname\": \"https://xaplus-merchant-dev.rebellapp.com\",\n" +
    //             "            \"password\": \"{{$shared password}}\",\n" +
    //             "            \"token\": \"eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2tleSI6IjE1ZWVhNTk5LWU3NmEtNGZkMC04MmRkLTk4YjQ0ZjkzNjM5ZSIsInRva2VuX2tleSI6InRva2VuOm1lcmNoYW50OmFwcDoxNWVlYTU5OS1lNzZhLTRmZDAtODJkZC05OGI0NGY5MzYzOWU6NWM5MWU5MDEtNTk2My00YjQxLThlZWYtNGFlNWViNmI1ODI4IiwiY2xpZW50X3R5cGUiOiJtZXJjaGFudF9hcHAiLCJ0b2tlbl90eXBlIjoiQUNDRVNTIiwicGxhdGZvcm0iOiJtZXJjaGFudCIsInN1YiI6IjE1ZWVhNTk5LWU3NmEtNGZkMC04MmRkLTk4YjQ0ZjkzNjM5ZSIsImlhdCI6MTc2NzUwNzY1MSwiZXhwIjoxNzY3OTkxNDkxLCJhdWQiOiJtZXJjaGFudCJ9.N6YiUpkWXvc5igJc-7-s82Se7G06La4g85NgxcY3VkA\"\n" +
    //             "        }\n" +
    //             "    }";*/
    //     String str = "(?s)\"local\":\\s*\\{.*?(?<anchor>\"token\":\\s*\"[^\"]+\")";
    //     String st1 = "(?s)\"local\":\\s*\\{.*?(?<anchor>\"token\":\\s*\"[^\"]+\")";


    //     /*

    //     Matcher m = p.matcher(json);

    //     System.out.println(m.find());               // true
    //     System.out.println(m.group("anchor"));*/
    // }
}
