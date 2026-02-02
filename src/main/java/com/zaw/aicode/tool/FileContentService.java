package com.zaw.aicode.tool;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.zaw.aicode.web.FileContentItem;
import com.zaw.aicode.web.FileContentResponse;

@Slf4j
public class FileContentService {

    public static FileContentResponse readFiles(String projectPath, List<String> relativePaths) {

        Path root = Path.of(projectPath).normalize().toAbsolutePath();
        List<FileContentItem> result = new ArrayList<>();
        log.info("result.size() = {}", result.size());
        for (String relativePath : relativePaths) {
            if(relativePath.startsWith(".")){
                relativePath = relativePath.substring(1);
            }
            if(relativePath.startsWith("/")){
                relativePath = relativePath.substring(1);
            }
            Path filePath = root.resolve(relativePath).normalize();
            log.info("ReadFile path:{}", filePath.toString());
            // 防止路径穿越攻击
            if (!filePath.startsWith(root)) {
                continue;
            }
            if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
                try {
                    String content = Files.readString(filePath, StandardCharsets.UTF_8);
                    result.add(new FileContentItem(relativePath, content));
                } catch (IOException e) {
                    // 读取失败可选择跳过 or 返回错误信息
                    result.add(new FileContentItem(relativePath,
                            "// ERROR: failed to read file - " + e.getMessage()));
                }
            }
        }
        return new FileContentResponse(result);
    }
}
