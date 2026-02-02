package com.zaw.aicode.dto;

import lombok.Data;

@Data
public class EditDTO {

    /**
     * insert_after | insert_before | replace | delete | new_file
     */
    private String type;

    /**
     * 稳定的前置锚点（可选，但强烈推荐）
     */
    private String before;

    /**
     * 核心锚点（必须）
     */
    private String anchor;

    /**
     * 稳定的后置锚点（可选，但强烈推荐）
     */
    private String after;

    /**
     * 新内容
     */
    private String content;

    // getters / setters

    private Boolean anchorIsRegex = false;

}
