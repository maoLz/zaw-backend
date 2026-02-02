package com.zaw.aicode.web;

import java.util.List;


/**
 * @author lizhong.zhang@yituishui.com
 * @since 2026/1/7
 */
public class FileTreeRequest {
    private String path;
    private List<String> nameFilters;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getNameFilters() {
        return nameFilters;
    }

    public void setNameFilters(List<String> nameFilters) {
        this.nameFilters = nameFilters;
    }
}
