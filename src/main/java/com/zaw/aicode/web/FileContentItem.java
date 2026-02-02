package com.zaw.aicode.web;

import lombok.Data;

@Data
public class FileContentItem {

    private String path;
    private String content;

    public FileContentItem(String path, String content) {
        this.path = path;
        this.content = content;
    }

    public String getPath() {
        return path;
    }

    public String getContent() {
        return content;
    }
}
