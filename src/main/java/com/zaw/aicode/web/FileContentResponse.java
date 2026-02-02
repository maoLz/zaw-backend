package com.zaw.aicode.web;

import java.util.List;

public class FileContentResponse {

    private List<FileContentItem> files;

    public FileContentResponse(List<FileContentItem> files) {
        this.files = files;
    }

    public List<FileContentItem> getFiles() {
        return files;
    }
}
