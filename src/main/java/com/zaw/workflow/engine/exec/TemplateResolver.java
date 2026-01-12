package com.zaw.workflow.engine.exec;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateResolver {

    private static final Pattern PATTERN = Pattern.compile("\\$\\{([^}]+)}");
    private static final int MAX_DEPTH = 0;

    public static String resolve(String value, JsonNode context) {
        return resolve(value, context, 0);
    }

    private static String resolve(String value, JsonNode context, int depth) {
        if (value == null || !value.contains("${")) {
            return value;
        }

        if (depth > MAX_DEPTH) {
            throw new IllegalStateException("Template resolve exceeded max depth: " + value);
        }

        Matcher matcher = PATTERN.matcher(value);
        StringBuffer sb = new StringBuffer();
        boolean replaced = false;

        while (matcher.find()) {
            String key = matcher.group(1);

            String replacement = "";

            if (key.startsWith("context.")) {
                JsonNode node = context.get(key.substring("context.".length()));
                if (node != null && !node.isNull()) {
                    if(node.isObject() || node.isArray()){
                        replacement = node.toString();
                    }else{
                        replacement = node.asText();
                    }
                }
            }
            if(StringUtils.isNotBlank(replacement)) {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
                replaced = true;
            }
        }

        matcher.appendTail(sb);

        // 如果本轮发生了替换，且结果中仍包含 ${}，继续递归
        String result = sb.toString();
        if (replaced && result.contains("${") && depth < MAX_DEPTH) {
            return resolve(result, context, depth + 1);
        }

        return result;
    }
}
