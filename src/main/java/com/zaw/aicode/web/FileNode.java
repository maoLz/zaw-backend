package com.zaw.aicode.web;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class FileNode {

    private String name;
    private String path;
    private String type; // file | directory
    private List<FileNode> children;

    public FileNode(String name, String path, String type) {
        this.name = name;
        this.path = path;
        this.type = type;
    }

    public void addChild(FileNode child) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(child);
    }

}
